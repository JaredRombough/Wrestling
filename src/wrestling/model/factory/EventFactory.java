package wrestling.model.factory;

import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import wrestling.model.AngleParams;
import wrestling.model.Event;
import wrestling.model.EventTemplate;
import wrestling.model.EventWorker;
import wrestling.model.Match;
import wrestling.model.MatchEvent;
import wrestling.model.controller.PromotionController;
import wrestling.model.interfaces.Segment;
import wrestling.model.interfaces.iEvent;
import wrestling.model.manager.ContractManager;
import wrestling.model.manager.EventManager;
import wrestling.model.manager.PromotionManager;
import wrestling.model.manager.SegmentManager;
import wrestling.model.manager.StableManager;
import wrestling.model.manager.TagTeamManager;
import wrestling.model.manager.TitleManager;
import wrestling.model.manager.WorkerManager;
import wrestling.model.modelView.EventView;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.modelView.SegmentView;
import wrestling.model.modelView.StableView;
import wrestling.model.modelView.TitleView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.segmentEnum.AngleType;
import wrestling.model.segmentEnum.EventVenueSize;
import wrestling.model.segmentEnum.JoinTeamType;
import wrestling.model.segmentEnum.ResponseType;
import wrestling.model.segmentEnum.ShowType;
import wrestling.model.segmentEnum.TeamType;
import wrestling.model.segmentEnum.TransactionType;
import wrestling.model.utility.ModelUtils;
import wrestling.view.utility.ViewUtils;

public class EventFactory {

    private final ContractManager contractManager;
    private final EventManager eventManager;
    private final TitleManager titleManager;
    private final WorkerManager workerManager;
    private final SegmentManager matchManager;
    private final MatchFactory matchFactory;
    private final PromotionManager promotionManager;
    private final TagTeamManager tagTeamManager;
    private final StableManager stableManager;

    public EventFactory(
            ContractManager contractManager,
            EventManager eventManager,
            MatchFactory matchFactory,
            SegmentManager matchManager,
            PromotionManager promotionManager,
            TitleManager titleManager,
            WorkerManager workerManager,
            TagTeamManager tagTeamManager,
            StableManager stableManager) {
        this.contractManager = contractManager;
        this.eventManager = eventManager;
        this.matchFactory = matchFactory;
        this.matchManager = matchManager;
        this.promotionManager = promotionManager;
        this.titleManager = titleManager;
        this.workerManager = workerManager;
        this.tagTeamManager = tagTeamManager;
        this.stableManager = stableManager;
    }

    public void processEventView(EventView eventView, boolean processSegments,
            PromotionController promotionController) {

        if (processSegments) {
            for (SegmentView segmentView : eventView.getSegmentViews()) {
                segmentView.setSegment(processSegmentView(eventView, segmentView));
            }
        }
        Event event = eventView.getEvent();
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
        eventManager.allWorkers(segments).stream().map((worker) -> contractManager.getContract(worker, event.getPromotion())).forEach((contract) -> {
            contractManager.appearance(event.getDate(), contract);
        });
    }

    public Segment processSegmentView(EventView eventView, SegmentView segmentView) {
        segmentView.setEventView(eventView);
        Segment segment = matchFactory.saveSegment(segmentView);
        if (segment instanceof Match) {
            eventManager.addMatchEvent(new MatchEvent((Match) segment, eventView.getEvent()));
            List<WorkerView> winners = matchManager.getWinners((Match) segment);
            winners.stream().forEach((w) -> {
                workerManager.gainPopularity(w);
            });

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
                StableView stable = new StableView(ModelUtils.slashNames(newMembers), segmentView.getPromotion());
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
