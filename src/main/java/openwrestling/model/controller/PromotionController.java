package openwrestling.model.controller;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import static java.time.temporal.TemporalAdjusters.firstInMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.Event;
import openwrestling.model.EventTemplate;
import openwrestling.model.EventWorker;
import openwrestling.model.Title;
import static openwrestling.model.constants.GameConstants.BASE_TRAINER_SUCCESS_RATE;
import openwrestling.model.factory.ContractFactory;
import openwrestling.model.factory.EventFactory;
import openwrestling.model.factory.MatchFactory;
import openwrestling.manager.ContractManager;
import openwrestling.model.manager.DateManager;
import openwrestling.model.manager.EventManager;
import openwrestling.model.manager.NewsManager;
import openwrestling.model.manager.TitleManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.modelView.EventView;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.modelView.SegmentTeam;
import openwrestling.model.modelView.SegmentView;
import openwrestling.model.modelView.StaffView;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segmentEnum.EventFrequency;
import openwrestling.model.segmentEnum.EventRecurrence;
import openwrestling.model.segmentEnum.SegmentType;
import openwrestling.model.segmentEnum.StaffType;
import openwrestling.model.segmentEnum.TeamType;
import openwrestling.model.utility.ModelUtils;
import openwrestling.model.utility.StaffUtils;

public class PromotionController implements Serializable {

    private final ContractFactory contractFactory;
    private final EventFactory eventFactory;
    private final MatchFactory matchFactory;

    private final ContractManager contractManager;
    private final DateManager dateManager;
    private final EventManager eventManager;
    private final TitleManager titleManager;
    private final WorkerManager workerManager;
    private final NewsManager newsManager;

    public PromotionController(
            ContractFactory contractFactory,
            EventFactory eventFactory,
            MatchFactory matchFactory,
            ContractManager contractManager,
            DateManager dateManager,
            EventManager eventManager,
            TitleManager titleManager,
            WorkerManager workerManager,
            NewsManager newsManager) {
        this.contractFactory = contractFactory;
        this.eventFactory = eventFactory;
        this.matchFactory = matchFactory;
        this.contractManager = contractManager;
        this.dateManager = dateManager;
        this.eventManager = eventManager;
        this.titleManager = titleManager;
        this.workerManager = workerManager;
        this.newsManager = newsManager;
    }

    private int idealRosterSize(Promotion promotion) {
        return 10 + (promotion.getLevel() * 10);
    }

    private int maxPushListSize(Promotion promotion) {
        return 2 + (promotion.getLevel() * 2);
    }

    private void updatePushed(Promotion promotion) {

        List<Worker> pushList = contractManager.getPushed(promotion);
        int diff = maxPushListSize(promotion) - pushList.size();

        if (diff > 0) {
            int i = 0;
            List<Worker> roster = workerManager.selectRoster(promotion);
            for (Worker worker :roster) {
                if (!pushList.contains(worker) && worker.isFullTime()) {
                    contractManager.getContract(worker, promotion).setPushed(true);
                }
                if (i >= diff) {
                    break;
                }
                i++;
            }
        } else if (diff < 0) {
            for (int i = 0; i < pushList.size(); i++) {
                contractManager.getContract(pushList.get(i), promotion).setPushed(false);
                if (i >= Math.abs(diff)) {
                    break;
                }
            }
        }
    }

    public void gainPopularity(Promotion promotion) {
        int increment = 1;
        int maxPop = 100;
        int maxLevel = 5;
        int basePop = 10;
        promotion.setPopularity(promotion.getPopularity() + increment);

        if (promotion.getPopularity() >= maxPop) {
            if (promotion.getLevel() != maxLevel) {
                promotion.setLevel(promotion.getLevel() + increment);
                promotion.setPopularity(basePop);
            } else {
                promotion.setPopularity(maxPop);
            }
        }
    }

    //call this method every day for each ai
    //put the general decision making sequence here
    public void dailyUpdate(Promotion promotion) {
        if (contractManager.getPushed(promotion).size() != maxPushListSize(promotion)) {
            updatePushed(promotion);
        }
        int activeRosterSize = contractManager.getActiveRoster(promotion).size();
        while (activeRosterSize < idealRosterSize(promotion) && !workerManager.freeAgents(promotion).isEmpty()) {
            signContract(promotion);
            activeRosterSize++;
        }
        //book a show if we have one scheduled today
        Event eventToday = eventManager.getEventOnDate(promotion, dateManager.today());
        if (eventToday != null) {
            List<Worker> roster = workerManager.selectRoster(promotion);
            if (roster.size() >= 2) {
                bookEvent(eventToday, promotion);

            } else {
                //cancel event
            }
        }
    }

    //pay everyone
    public void payDay(Promotion promotion, LocalDate date) {

        for (Contract c : contractManager.getContracts(promotion)) {
            contractManager.payDay(date, c);
        }

        for (StaffView staff : promotion.getAllStaff()) {
            contractManager.payDay(date, staff.getStaffContract());
        }
    }

    public void trainerUpdate(Promotion promotion) {
        for (StaffView trainer : StaffUtils.getStaff(StaffType.TRAINER, promotion)) {
            if (RandomUtils.nextInt(0, BASE_TRAINER_SUCCESS_RATE) == 1 && RandomUtils.nextInt(0, BASE_TRAINER_SUCCESS_RATE) < trainer.getSkill()) {
                List<Worker> roster = workerManager.selectRoster(promotion);
                Worker worker = roster.get(RandomUtils.nextInt(0, roster.size() - 1));
                Map<String, Integer> properties = new HashMap<>();
                properties.put("striking", worker.getStriking());
                properties.put("flying", worker.getFlying());
                properties.put("wrestling", worker.getWrestling());
                properties.put("charisma", worker.getCharisma());
                List keys = new ArrayList(properties.keySet());
                Collections.shuffle(keys);
                boolean success = false;
                String stat = "";
                for (Object o : keys) {
                    int value = properties.get(o.toString());
                    if (value < trainer.getSkill()) {
                        stat = o.toString();
                        switch (o.toString()) {
                            case "striking":
                                worker.setStriking(value + 1);
                                break;
                            case "flying":
                                worker.setStriking(value + 1);
                                break;
                            case "wrestling":
                                worker.setStriking(value + 1);
                                break;
                            case "charisma":
                                worker.setStriking(value + 1);
                                break;
                        }
                        success = true;
                        break;
                    }

                }
                if (success) {
                    newsManager.addTrainingNewsItem(worker, trainer, promotion, stat, dateManager.today());
                }
            }
        }
    }

    private void sortByPopularity(List<Worker> workerList) {
        //sort roster by popularity
        Collections.sort(workerList, (Worker w1, Worker w2) -> -Integer.valueOf(w1.getPopularity()).compareTo(w2.getPopularity()));
    }

    //sign a contract with the first suitable worker found
    private void signContract(Promotion promotion) {

        for (Worker worker : workerManager.freeAgents(promotion)) {
            if (worker.getPopularity() <= ModelUtils.maxPopularity(promotion)) {
                contractFactory.createContract(worker, promotion, dateManager.today());
                break;
            }
        }
    }

    //sign a contract with the first suitable worker found
    private void signContract(Promotion promotion, LocalDate date) {

        for (Worker worker : workerManager.freeAgents(promotion)) {
            if (worker.getPopularity() <= ModelUtils.maxPopularity(promotion)
                    && !eventManager.isBooked(worker, date)) {
                contractFactory.createContract(worker, promotion, dateManager.today());
                break;
            }
        }
    }

    //determine how many future events the promotion is meant to have at a given time
    private int eventAmountTarget(Promotion promotion) {

        int target = 0;

        switch (promotion.getLevel()) {
            case 1:
                target = 1;
                break;
            case 2:
                target = 1;
                break;
            case 3:
                target = 2;
                break;
            case 4:
                target = 4;
                break;
            case 5:
                target = 20;
                break;
            default:
                break;

        }

        return target;
    }

    public void bookNextEvent(EventTemplate template, LocalDate eventDate) {
        Event event = bookNextEvent(template.getPromotion(), eventDate);
        event.setName(template.getName());
        event.setEventTemplate(template);
        event.setDefaultDuration(template.getDefaultDuration());
    }

    public Event bookNextEvent(Promotion promotion, LocalDate eventDate) {

        int workersNeeded = idealRosterSize(promotion) - contractManager.getActiveRoster(promotion).size();

        if (workersNeeded > 0) {
            for (int i = 0; i < workersNeeded; i++) {
                signContract(promotion, dateManager.today());
            }
        }

        Event event = new Event(promotion, eventDate);
        eventManager.addEvent(event);

        //book the roster for the date
        List<Worker> roster = workerManager.selectRoster(promotion);
        for (Worker worker : roster) {
            if (!eventManager.isBooked(worker, eventDate)) {
                eventManager.addEventWorker(new EventWorker(event, worker));
            }
        }

        return event;

    }

    public void bookNextEvent(Promotion promotion) {

        LocalDate eventDate = LocalDate.ofYearDay(dateManager.today().getYear(), dateManager.today().getDayOfYear());
        eventDate = LocalDate.from(eventDate).plusDays(RandomUtils.nextInt(25, 35));
        bookNextEvent(promotion, eventDate);

    }

    private List<SegmentView> bookSegments(Promotion promotion) {
        //maximum segments for the event
        int maxSegments = 8;

        List<Worker> pushList = contractManager.getPushed(promotion);

        //bigger promotions get more segments
        if (promotion.getLevel() > 3) {
            maxSegments += 2;
        }
        //lists to track workers the event roster
        //and workers that are already booked on this date
        List<Worker> eventRoster = getEventMatchRoster(promotion);

        //list to track workers on the pushlist that are still available
        List<Worker> pushListPresent = new ArrayList<>();

        //move pushlist workers present to the pushlistpresent from the event roster
        for (int i = 0; i < pushList.size(); i++) {
            if (eventRoster.contains(pushList.get(i))) {
                eventRoster.remove(pushList.get(i));
                pushListPresent.add(pushList.get(i));
            }
        }

        //sort the lists of workers for the event by popularity
        sortByPopularity(pushList);
        sortByPopularity(eventRoster);

        //list to hold event segments
        List<SegmentView> segments = new ArrayList<>();

        //list to hold workers who have been booked for this event
        List<Worker> matchBooked = new ArrayList<>();

        //get a list of titles available for the event
        List<Title> eventTitles = titleManager.getEventTitles(promotion, eventRoster);

        //book title matches
        for (Title title : eventTitles) {

            //determine team size based on the title
            int teamSize = title.getTeamSize();

            //determine the number of teams (usually 2)
            int teamsNeeded = 2;
            int random = RandomUtils.nextInt(1, 10);
            if (random > 8) {
                teamsNeeded += 10 - random;
            }

            List<SegmentTeam> matchTeams = new ArrayList<>();
            List<Worker> champs = titleManager.getCurrentChampionWorkers(title);

            //if the title is not vacant, make the title holders team 1
            if (!champs.isEmpty()) {
                matchTeams.add(new SegmentTeam(champs, TeamType.WINNER));
                matchBooked.addAll(champs);
            }

            //list to hold the lists we will draw workers from
            //in order of priority
            List<List<Worker>> workerResources = new ArrayList<>();

            workerResources.add(pushListPresent);
            workerResources.add(eventRoster);

            //loop for the number of teams we want
            for (int i = 0; i < teamsNeeded; i++) {

                List<Worker> team = new ArrayList<>();
                boolean teamMade = false;

                //iterate through resources
                for (List<Worker> resouce : workerResources) {

                    //iterate through workers in the resource
                    for (Worker worker : resouce) {

                        //if the worker isn't in this team or already booked, add them
                        //to the team
                        if (!matchBooked.contains(worker) && !team.contains(worker)) {
                            team.add(worker);
                        }

                        //if the team is big enough, break out of the loop
                        if (team.size() >= teamSize) {
                            matchTeams.add(new SegmentTeam(team, TeamType.LOSER));
                            matchBooked.addAll(team);
                            teamMade = true;
                            break;
                        }

                    }
                    if (teamMade) {
                        break;
                    }
                }
            }

            //make sure we have enough workers for a match
            if (matchTeams.size() > 1) {
                //roll for title change
                if (RandomUtils.nextInt(1, 10) > 5) {
                    Collections.swap(matchTeams, 0, 1);
                }

                SegmentView segmentView = new SegmentView(SegmentType.MATCH);
                segmentView.setTeams(matchTeams);
                segmentView.addTitle(titleManager.getTitleView(title));
                segments.add(segmentView);
            }
        }

        //fill up the segments if we don't have enough for some reason
        if (segments.size() < maxSegments) {

            //go through the roster by popularity and make singles matches
            for (int i = 0; i < eventRoster.size(); i += 2) {
                if (eventRoster.size() > i + 1) {
                    //move this somewhere else, like a matchFactory
                    List<Worker> teamA = new ArrayList<>(Arrays.asList(eventRoster.get(i)));
                    List<Worker> teamB = new ArrayList<>(Arrays.asList(eventRoster.get(i + 1)));
                    List<SegmentTeam> teams = new ArrayList<>();
                    teams.add(new SegmentTeam(teamA, TeamType.WINNER));
                    teams.add(new SegmentTeam(teamB, TeamType.LOSER));

                    SegmentView segmentView = new SegmentView(SegmentType.MATCH);
                    segmentView.setTeams(teams);

                    segments.add(segmentView);
                }

                if (segments.size() > maxSegments) {
                    break;
                }

            }
        }

        return segments;
    }

    //book an event
    private void bookEvent(Event event, Promotion promotion) {
        eventFactory.processEventView(new EventView(event, bookSegments(promotion)), true, this);
    }

    private LocalDate generateEventTemplateStartDate(EventTemplate eventTemplate) {
        LocalDate date = dateManager.today();
        if (eventTemplate.getEventFrequency().equals(EventFrequency.ANNUAL)) {
            while (date.getMonth().getValue() != eventTemplate.getMonth()) {
                date = date.plusMonths(1);
            }
            date = date.with(TemporalAdjusters.dayOfWeekInMonth(
                    RandomUtils.nextInt(1, 4),
                    eventTemplate.getDayOfWeek()));
            eventTemplate.setNextDate(date);
            bookNextEvent(eventTemplate, date);
        }
        return date;
    }

    public LocalDate bookEventTemplate(EventTemplate eventTemplate) {
        return bookEventTemplate(eventTemplate, generateEventTemplateStartDate(eventTemplate));
    }

    public LocalDate bookEventTemplate(EventTemplate eventTemplate, YearMonth yearMonth) {

        LocalDate startDate = LocalDate.of(yearMonth.getYear(),
                yearMonth.getMonth(),
                1);
        startDate = startDate.with(firstInMonth(eventTemplate.getDayOfWeek()));
        if (eventTemplate.getEventFrequency().equals(EventFrequency.ANNUAL)) {
            startDate = startDate.plusWeeks(RandomUtils.nextInt(0, 4));
            eventTemplate.setNextDate(startDate);
            bookNextEvent(eventTemplate, startDate);
            eventTemplate.setBookedUntil(startDate);
        } else {
            if (eventTemplate.getNextDate() == null) {
                eventTemplate.setNextDate(startDate);
            }
            LocalDate weeklyDate = LocalDate.from(startDate);
            int eventsToBook = eventManager.getEventsLeftFuture(eventTemplate, yearMonth);
            int booked = 0;
            for (int i = 0; i < startDate.lengthOfMonth(); i++) {
                if (booked >= eventsToBook || !weeklyDate.getMonth().equals(yearMonth.getMonth())) {
                    break;
                }
                bookNextEvent(eventTemplate, weeklyDate);
                weeklyDate = weeklyDate.plusWeeks(1);
                booked++;
            }
            eventTemplate.setBookedUntil(weeklyDate);
        }

        return startDate;
    }

    public LocalDate bookEventTemplate(EventTemplate eventTemplate, LocalDate startDate) {

        if (eventTemplate.getBookedUntil().isBefore(dateManager.today())) {
            int timesToBook = eventTemplate.getEventsLeft();

            if (eventTemplate.getEventFrequency().equals(EventFrequency.ANNUAL)) {
                eventTemplate.setNextDate(startDate);
                bookNextEvent(eventTemplate, startDate);
            } else {
                if (startDate.getDayOfWeek() != eventTemplate.getDayOfWeek()) {
                    startDate = startDate.with(
                            TemporalAdjusters.next(eventTemplate.getDayOfWeek()));
                }
                eventTemplate.setNextDate(startDate);
                for (int i = 0; i < timesToBook; i++) {
                    bookNextEvent(eventTemplate, startDate);
                    startDate = startDate.plusWeeks(1);
                }
            }
            eventTemplate.setBookedUntil(startDate);

        }
        return startDate;
    }

    public void updateEventTemplate(EventView eventView) {
        EventTemplate eventTemplate = eventView.getEvent().getEventTemplate();

        if (eventTemplate != null) {
            LocalDate nextDate = eventTemplate.getNextDate();
            if (eventTemplate.getEventRecurrence().equals(EventRecurrence.LIMITED)) {
                eventTemplate.setEventsLeft(eventTemplate.getEventsLeft() - 1);
                nextDate = nextDate.with(TemporalAdjusters.next(eventTemplate.getDayOfWeek()));
            }

            if (eventTemplate.getEventsLeft() <= 0) {
                nextDate = bookEventTemplate(eventTemplate);
            }
            eventTemplate.setNextDate(nextDate);
        }
    }

    private List<Worker> getEventMatchRoster(Promotion promotion) {
        //lists to track workers the event roster
        //and workers that are already booked on this date
        List<Worker> eventRoster = workerManager.selectRoster(promotion);
        List<Worker> unavailable = new ArrayList<>();

        //go through the event roster and check for workers already booked
        for (Worker worker : eventRoster) {

            //the worker is unavailable if they are booked and the booking isn't with us
            if (!eventManager.isAvailable(worker, dateManager.today(), promotion)) {
                unavailable.add(worker);
            }
        }

        //remove all booked workers from the event roster
        eventRoster.removeAll(unavailable);

        //list to hold noncompetitors (managers, etc)
        List<Worker> nonCompetitors = new ArrayList<>();

        //go through the event roster and collect noncompetitors
        for (Worker worker : eventRoster) {
            if (!worker.isFullTime() || !worker.isMainRoster()) {

                nonCompetitors.add(worker);
            }
        }

        //remove noncompetitors from the event roster
        eventRoster.removeAll(nonCompetitors);

        sortByPopularity(eventRoster);

        return eventRoster;
    }
}
