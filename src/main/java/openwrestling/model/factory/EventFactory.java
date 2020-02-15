package openwrestling.model.factory;

import openwrestling.Logging;
import openwrestling.manager.BankAccountManager;
import openwrestling.manager.ContractManager;
import openwrestling.manager.EventManager;
import openwrestling.manager.NewsManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.RelationshipManager;
import openwrestling.manager.SegmentManager;
import openwrestling.manager.StableManager;
import openwrestling.manager.TagTeamManager;
import openwrestling.manager.TitleManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.MoraleRelationship;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Segment;
import openwrestling.model.gameObjects.SegmentTeam;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.interfaces.iEvent;
import openwrestling.model.segmentEnum.AngleType;
import openwrestling.model.segmentEnum.EventFrequency;
import openwrestling.model.segmentEnum.EventVenueSize;
import openwrestling.model.segmentEnum.JoinTeamType;
import openwrestling.model.segmentEnum.ResponseType;
import openwrestling.model.segmentEnum.SegmentType;
import openwrestling.model.segmentEnum.ShowType;
import openwrestling.model.segmentEnum.TeamType;
import openwrestling.model.utility.ModelUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.temporal.TemporalAdjusters.next;
import static openwrestling.model.constants.GameConstants.*;
import static openwrestling.model.utility.EventUtils.dateForAnnualEvent;
import static openwrestling.model.utility.SegmentUtils.getMatchMoralePenalties;

public class EventFactory extends Logging {

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
    private final BankAccountManager bankAccountManager;
    private final SegmentManager segmentManager;

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
            NewsManager newsManager,
            BankAccountManager bankAccountManager,
            SegmentManager segmentManager
    ) {
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
        this.bankAccountManager = bankAccountManager;
        this.segmentManager = segmentManager;
    }

    public Event processEventView(Event event, boolean processSegments) {
        logger.log(Level.DEBUG, "start process processEventView for " + event.getName());
        try {

            if (processSegments) {
                logger.log(Level.DEBUG, "processing " + event.getSegments().size() + " segments");
                for (Segment segment : event.getSegments()) {
                    processSegment(event, segment);
                }
            }

            logger.log(Level.DEBUG, "after processSegments for " + event.getName());


            setEventStats(event, event.getSegments());

            event.setGate(eventManager.calculateGate(event));

            processContracts(event, event.getSegments());

            logger.log(Level.DEBUG, "end process processEventView for " + event.getName());
        } catch (Exception e) {
            logger.log(Level.ERROR, e.getMessage());
            logger.log(Level.ERROR, ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return event;
    }

    public static List<EventTemplate> generateMonthlyEventTemplates(Promotion promotion, LocalDate startDate) {
        List<EventTemplate> eventTemplates = new ArrayList<>();
        Month month = Month.JANUARY;
        for (int i = 0; i < 12; i++) {

            EventTemplate template = new EventTemplate();
            template.setPromotion(promotion);
            template.setMonth(month.getValue());
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
            eventTemplates.add(template);


        }
        return eventTemplates;
    }

    public static List<Event> bookEventsForNewEventTemplate(EventTemplate eventTemplate, LocalDate startDate) {
        List<Event> newEvents = new ArrayList<>();
        if (eventTemplate.getEventFrequency().equals(EventFrequency.ANNUAL)) {
            Event event = bookEventForNewAnnualEventTemplateAfterDate(eventTemplate, startDate);
            newEvents.add(event);
        } else if (eventTemplate.getEventFrequency().equals(EventFrequency.WEEKLY)) {
            LocalDate weeklyDate = startDate;
            for (int i = 0; i < WEEKLY_EVENTS_TO_ADVANCE_BOOK_ON_INIT; i++) {
                Event event = bookEventForTemplateOnDate(eventTemplate, weeklyDate);
                newEvents.add(event);
                weeklyDate = weeklyDate.with(next(eventTemplate.getDayOfWeek()));
            }
        }
        return newEvents;
    }

    public static Event bookEventForCompletedAnnualEventTemplateAfterDate(EventTemplate eventTemplate, LocalDate startDate) {
        LocalDate date = startDate.plusMonths(1).withDayOfMonth(1);
        return bookEventForTemplateOnDate(eventTemplate,
                dateForAnnualEvent(eventTemplate.getMonth(), eventTemplate.getDayOfWeek(), date)
        );
    }

    public static Event bookEventForNewAnnualEventTemplateAfterDate(EventTemplate eventTemplate, LocalDate startDate) {
        return bookEventForTemplateOnDate(eventTemplate,
                dateForAnnualEvent(eventTemplate.getMonth(), eventTemplate.getDayOfWeek(), startDate)
        );
    }


    public static Event bookEventForTemplateOnDate(EventTemplate eventTemplate, LocalDate date) {
        Event event = new Event();
        event.setDate(date);
        event.setPromotion(eventTemplate.getPromotion());
        event.setName(eventTemplate.getName());
        event.setEventTemplate(eventTemplate);
        event.setDefaultDuration(eventTemplate.getDefaultDuration());
        return event;
    }

    private void setEventStats(Event event, List<Segment> segments) {
        event.setCost(eventManager.calculateCost(segments, event.getPromotion()));
        event.setGate(eventManager.calculateGate(segments, event.getPromotion()));
        event.setAttendance(eventManager.calculateAttendance(segments, event.getPromotion()));
    }


    private void processContracts(iEvent event, List<Segment> segments) {
        eventManager.allWorkers(segments).stream().forEach((worker) -> {
            contractManager.appearance(event.getDate(), worker, event.getPromotion());
        });
    }

    public Segment processSegment(Event event, Segment toProcess) {
        logger.log(Level.DEBUG, "start processSegment for " + event.getName());
        toProcess.setEvent(event);
        Segment segment = matchFactory.saveSegment(toProcess);
        if (SegmentType.MATCH.equals(segment.getSegmentType())) {
            logger.log(Level.DEBUG, "processing match");
            List<Worker> winners = toProcess.getWinners();
            Map<Worker, MoraleRelationship> relationshipMap = new HashMap<>();
            winners.forEach((worker) -> {
                logger.log(Level.DEBUG, "processing winner " + worker.getName());
                workerManager.gainPopularity(worker);
                relationshipMap.put(worker, relationshipManager.getMoraleRelationship(worker, event.getPromotion()));
                relationshipMap.get(worker).modifyValue(MORALE_BONUS_MATCH_WIN);
                if (!toProcess.getTitles().isEmpty()) {
                    relationshipMap.get(worker).modifyValue(MORALE_BONUS_TITLE_MATCH_WIN);
                }
            });
            if (!winners.isEmpty()) {
                getMatchMoralePenalties(toProcess).forEach((key, value) -> {
                    if (!relationshipMap.containsKey(key)) {
                        relationshipMap.put(key, relationshipManager.getMoraleRelationship(key, event.getPromotion()));
                    }
                    relationshipMap.get(key).modifyValue(-value);
                    newsManager.addJobComplaintNewsItem(key, winners, toProcess.getPromotion(), toProcess.getDate());
                });
            }

            segment.setMoraleRelationshipMap(relationshipMap);

            if (!toProcess.getTitles().isEmpty() && !winners.isEmpty()) {
                processTitleChanges(toProcess, winners);
            }
        } else {
            if (AngleType.OFFER.equals(segment.getAngleType())) {
                processOffer(segment);
            } else if (AngleType.CHALLENGE.equals(segment.getAngleType())) {
                processChallenge(segment);
            }
        }
        return segment;
    }

    private void processOffer(Segment segment) {
        SegmentTeam offerer = segment.getTeams(TeamType.OFFERER).stream().findFirst().orElse(null);
        List<SegmentTeam> offerees = new ArrayList<>(segment.getTeams(TeamType.OFFEREE));

        if (JoinTeamType.TAG_TEAM.equals(segment.getJoinTeamType())) {
            SegmentTeam offeree = offerees.get(0);
            if (ResponseType.YES.equals(offeree.getResponse())) {
                tagTeamManager.createTagTeam(
                        ModelUtils.andTeams(Arrays.asList(offerer, offeree)),
                        offerer.getWorkers().get(0),
                        offeree.getWorkers().get(0));
            }
        } else if (JoinTeamType.NEW_STABLE.equals(segment.getJoinTeamType())) {
            List<Worker> newMembers = new ArrayList<>();
            newMembers.addAll(offerer.getWorkers());
            offerees.forEach(offeree -> {
                if (ResponseType.YES.equals(offeree.getResponse())) {
                    newMembers.addAll(offeree.getWorkers());
                }
            });

            if (newMembers.size() > 1) {
                Stable stable = new Stable(ModelUtils.slashNames(newMembers), segment.getPromotion());
                stable.setWorkers(newMembers);
                stableManager.addStable(stable);
                segment.setNewStable(stable);
            }

        } else if (JoinTeamType.STABLE.equals(segment.getJoinTeamType())) {
            List<Worker> newMembers = new ArrayList<>();
            offerees.forEach(offeree -> {
                if (ResponseType.YES.equals(offeree.getResponse())) {
                    newMembers.addAll(offeree.getWorkers());
                }
            });
            segment.getJoinStable().getWorkers().addAll(newMembers);
        }
    }

    private void processTitleChanges(Segment segment, List<Worker> winners) {
        for (Title title : segment.getTitles()) {
            boolean change = !winners.equals(title.getChampions());
            if (change) {
                titleManager.titleChange(title, winners);
            }
        }
    }

    private void processChallenge(Segment segment) {
        if (segment.getShowType().equals(ShowType.NEXT_SHOW)) {
            List<SegmentTeam> teams = segment.getChallengeSegment().getSegmentTeams().stream()
                    .filter(team -> team.getType().equals(TeamType.CHALLENGER) || ResponseType.YES.equals(team.getResponse()))
                    .collect(Collectors.toList());
            if (teams.size() < 2) {
                segment.setChallengeSegment(null);
            }
        }
    }

}
