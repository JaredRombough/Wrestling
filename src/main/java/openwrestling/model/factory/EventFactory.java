package openwrestling.model.factory;

import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import openwrestling.model.AngleParams;
import openwrestling.model.Event;
import openwrestling.model.EventTemplate;
import openwrestling.model.EventWorker;
import openwrestling.model.Match;
import openwrestling.model.MatchEvent;
import openwrestling.model.NewsItem;
import openwrestling.model.SegmentTemplate;
import static openwrestling.model.constants.GameConstants.MORALE_BONUS_MATCH_WIN;
import static openwrestling.model.constants.GameConstants.MORALE_BONUS_TITLE_MATCH_WIN;
import openwrestling.model.controller.PromotionController;
import openwrestling.model.interfaces.Segment;
import openwrestling.model.interfaces.iEvent;
import openwrestling.model.manager.ContractManager;
import openwrestling.model.manager.EventManager;
import openwrestling.model.manager.NewsManager;
import openwrestling.model.manager.PromotionManager;
import openwrestling.model.manager.RelationshipManager;
import openwrestling.model.manager.StableManager;
import openwrestling.model.manager.TagTeamManager;
import openwrestling.model.manager.TitleManager;
import openwrestling.model.manager.WorkerManager;
import openwrestling.model.modelView.EventView;
import openwrestling.model.modelView.PromotionView;
import openwrestling.model.modelView.SegmentTeam;
import openwrestling.model.modelView.SegmentView;
import openwrestling.model.modelView.TitleView;
import openwrestling.model.modelView.WorkerGroup;
import openwrestling.model.modelView.WorkerView;
import openwrestling.model.segmentEnum.AngleType;
import openwrestling.model.segmentEnum.EventVenueSize;
import openwrestling.model.segmentEnum.JoinTeamType;
import openwrestling.model.segmentEnum.ResponseType;
import openwrestling.model.segmentEnum.ShowType;
import openwrestling.model.segmentEnum.TeamType;
import openwrestling.model.segmentEnum.TransactionType;
import openwrestling.model.utility.ModelUtils;
import static openwrestling.model.utility.SegmentUtils.getMatchMoralePenalties;

public class EventFactory {

    private final ContractManager contractManager;
    private final EventManager eventManager;
    private final TitleManager titleManager;
    private final WorkerManager workerManager;
    private final MatchFactory matchFactory;
    private final PromotionManager promotionManager;
    private final TagTeamManager tagTeamManager;
    private final StableManager stableManager;
    private final RelationshipManager relationshipManager;
    private final NewsManager newsManager;

    public EventFactory(
            ContractManager contractManager,
            EventManager eventManager,
            MatchFactory matchFactory,
            PromotionManager promotionManager,
            TitleManager titleManager,
            WorkerManager workerManager,
            TagTeamManager tagTeamManager,
            StableManager stableManager,
            RelationshipManager relationshipManager,
            NewsManager newsManager) {
        this.contractManager = contractManager;
        this.eventManager = eventManager;
        this.matchFactory = matchFactory;
        this.promotionManager = promotionManager;
        this.titleManager = titleManager;
        this.workerManager = workerManager;
        this.tagTeamManager = tagTeamManager;
        this.stableManager = stableManager;
        this.relationshipManager = relationshipManager;
        this.newsManager = newsManager;
    }

    public void processEventView(EventView eventView, boolean processSegments, PromotionController promotionController) {
        Event event = eventView.getEvent();

        if (processSegments) {
            for (SegmentView segmentView : eventView.getSegmentViews()) {
                segmentView.setSegment(processSegmentView(eventView, segmentView));
            }
        }

        clearOldSegmentTemplates(event);

        List<Segment> segments = segmentsFromSegmentViews(eventView.getSegmentViews());

        setEventStats(event, segments);

        promotionManager.getBankAccount(event.getPromotion()).addFunds(
                eventManager.calculateGate(event), TransactionType.GATE, eventView.getEvent().getDate());

        for (WorkerView worker : eventManager.allWorkers(segments)) {
            EventWorker eventWorker = new EventWorker(event, worker);
            eventManager.addEventWorker(eventWorker);
        }
        processContracts(event, segments);

        eventManager.addEventView(eventView);

        promotionController.updateEventTemplate(eventView);
    }

    public void createMonthlyEvents(PromotionView promotion) {

        Month month = Month.JANUARY;
        for (int i = 0; i < 12; i++) {

            EventTemplate template = new EventTemplate();
            template.setPromotion(promotion);
            template.setMonth(month);
            template.setName(promotion.getShortName() + " "
                    + month.toString().substring(0, 1).toUpperCase()
                    + month.toString().toLowerCase().substring(1)
                    + " Event");
            month = month.plus(1);
            if (promotion.getLevel() > 4) {
                template.setEventVenueSize(EventVenueSize.LARGE);
            } else if (promotion.getLevel() <= 4
                    && promotion.getLevel() >= 3) {
                template.setEventVenueSize(EventVenueSize.MEDIUM);
            } else {
                template.setEventVenueSize(EventVenueSize.SMALL);
            }
            eventManager.addEventTemplate(template);

        }
    }

    private void clearOldSegmentTemplates(Event event) {
        List<SegmentTemplate> templatesForEvent = event.getEventTemplate().getSegmentTemplates().stream()
                .filter(segmentTemplate -> segmentTemplate.getSourceEvent().equals(event))
                .collect(Collectors.toList());
        event.getEventTemplate().getSegmentTemplates().clear();
        event.getEventTemplate().getSegmentTemplates().addAll(templatesForEvent);
    }

    private void setEventStats(Event event, List<Segment> segments) {
        event.setCost(eventManager.calculateCost(segments, event.getPromotion()));
        event.setGate(eventManager.calculateGate(segments, event.getPromotion()));
        event.setAttendance(eventManager.calculateAttendance(segments, event.getPromotion()));
    }

    private List<Segment> segmentsFromSegmentViews(List<SegmentView> segmentViews) {
        List<Segment> segments = new ArrayList<>();
        for (SegmentView segmentView : segmentViews) {
            segments.add(segmentView.getSegment());
        }
        return segments;
    }

    private void processContracts(iEvent event, List<Segment> segments) {
        eventManager.allWorkers(segments).stream().forEach((worker) -> {
            contractManager.appearance(event.getDate(), worker, event.getPromotion());
        });
    }

    public Segment processSegmentView(EventView eventView, SegmentView segmentView) {
        segmentView.setEventView(eventView);
        Segment segment = matchFactory.saveSegment(segmentView);
        if (segment instanceof Match) {
            eventManager.addMatchEvent(new MatchEvent((Match) segment, eventView.getEvent()));
            List<WorkerView> winners = segmentView.getWinners();
            winners.stream().forEach((worker) -> {
                workerManager.gainPopularity(worker);
                relationshipManager.addRelationshipValue(worker, segmentView.getPromotion(), MORALE_BONUS_MATCH_WIN);
                if (!segmentView.getTitleViews().isEmpty()) {
                    relationshipManager.addRelationshipValue(worker, segmentView.getPromotion(), MORALE_BONUS_TITLE_MATCH_WIN);
                }
            });
            if (!winners.isEmpty()) {
                getMatchMoralePenalties(segmentView).entrySet().stream().forEach(entry -> {
                    relationshipManager.addRelationshipValue(entry.getKey(), segmentView.getPromotion(), -entry.getValue());
                    newsManager.addJobComplaintNewsItem(entry.getKey(), winners, segmentView.getPromotion(), segmentView.getDate());
                });
            }
            if (!segmentView.getTitleViews().isEmpty() && !winners.isEmpty()) {
                processTitleChanges(segmentView, winners);
            }
        } else {
            AngleParams angleParams = (AngleParams) segmentView.getSegment().getSegmentParams();
            if (AngleType.OFFER.equals(angleParams.getAngleType())) {
                processOffer(segmentView, angleParams);
            } else if (AngleType.CHALLENGE.equals(angleParams.getAngleType())) {
                processChallenge(angleParams);
            }
        }
        return segment;
    }

    private void processOffer(SegmentView segmentView, AngleParams angleParams) {
        SegmentTeam offerer = segmentView.getTeams(TeamType.OFFERER).stream().findFirst().orElse(null);
        List<SegmentTeam> offerees = segmentView.getTeams(TeamType.OFFEREE).stream().collect(Collectors.toList());

        if (JoinTeamType.TAG_TEAM.equals(angleParams.getJoinTeamType())) {
            SegmentTeam offeree = offerees.get(0);
            if (ResponseType.YES.equals(offeree.getResponse())) {
                tagTeamManager.createTagTeam(
                        ModelUtils.andTeams(Arrays.asList(offerer, offeree)),
                        offerer.getWorkers().get(0),
                        offeree.getWorkers().get(0));
            }
        } else if (JoinTeamType.NEW_STABLE.equals(angleParams.getJoinTeamType())) {
            List<WorkerView> newMembers = new ArrayList<>();
            newMembers.addAll(offerer.getWorkers());
            offerees.forEach(offeree -> {
                if (ResponseType.YES.equals(offeree.getResponse())) {
                    newMembers.addAll(offeree.getWorkers());
                }
            });

            if (newMembers.size() > 1) {
                WorkerGroup stable = new WorkerGroup(ModelUtils.slashNames(newMembers), segmentView.getPromotion());
                stable.setWorkers(newMembers);
                stableManager.addStable(stable);
                segmentView.setNewStable(stable);
            }

        } else if (JoinTeamType.STABLE.equals(angleParams.getJoinTeamType())) {
            List<WorkerView> newMembers = new ArrayList<>();
            offerees.forEach(offeree -> {
                if (ResponseType.YES.equals(offeree.getResponse())) {
                    newMembers.addAll(offeree.getWorkers());
                }
            });
            segmentView.getSegment().getSegmentParams().getJoinStable().getWorkers().addAll(newMembers);
        }
    }

    private void processTitleChanges(SegmentView segmentView, List<WorkerView> winners) {
        for (TitleView titleView : segmentView.getTitleViews()) {
            boolean change = !winners.equals(titleView.getChampions());
            if (change) {
                titleManager.titleChange(titleView.getTitle(), winners);
            }
        }
    }

    private void processChallenge(AngleParams angleParams) {
        if (angleParams.getShowType().equals(ShowType.NEXT_SHOW)) {
            List<SegmentTeam> teams = angleParams.getChallengeSegment().getSegmentTeams().stream()
                    .filter(team -> team.getType().equals(TeamType.CHALLENGER) || ResponseType.YES.equals(team.getResponse()))
                    .collect(Collectors.toList());
            if (teams.size() > 1) {
                angleParams.getChallengeSegment().getEventTemplate().getSegmentTemplates().add(angleParams.getChallengeSegment());
            }
        }
    }

}
