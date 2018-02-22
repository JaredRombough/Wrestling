package wrestling.model.controller;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import wrestling.model.Contract;
import wrestling.model.Event;
import wrestling.model.EventName;
import wrestling.model.EventWorker;
import wrestling.model.Match;
import wrestling.model.Promotion;
import wrestling.model.Television;
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.model.factory.ContractFactory;
import wrestling.model.factory.EventFactory;
import wrestling.model.factory.MatchFactory;
import wrestling.model.interfaces.Segment;
import wrestling.model.manager.ContractManager;
import wrestling.model.manager.DateManager;
import wrestling.model.manager.EventManager;
import wrestling.model.manager.TelevisionManager;
import wrestling.model.manager.TitleManager;
import wrestling.model.manager.WorkerManager;
import wrestling.model.utility.ModelUtilityFunctions;

public class PromotionController implements Serializable {

    private final ContractFactory contractFactory;
    private final EventFactory eventFactory;
    private final MatchFactory matchFactory;

    private final ContractManager contractManager;
    private final DateManager dateManager;
    private final EventManager eventManager;
    private final TelevisionManager televisionManager;
    private final TitleManager titleManager;
    private final WorkerManager workerManager;

    public PromotionController(
            ContractFactory contractFactory,
            EventFactory eventFactory,
            MatchFactory matchFactory,
            ContractManager contractManager,
            DateManager dateManager,
            EventManager eventManager,
            TelevisionManager televisionManager,
            TitleManager titleManager,
            WorkerManager workerManager) {
        this.contractFactory = contractFactory;
        this.eventFactory = eventFactory;
        this.matchFactory = matchFactory;
        this.contractManager = contractManager;
        this.dateManager = dateManager;
        this.eventManager = eventManager;
        this.televisionManager = televisionManager;
        this.titleManager = titleManager;
        this.workerManager = workerManager;
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
            for (Worker worker : contractManager.getFullRoster(promotion)) {
                if (!pushList.contains(worker) && !worker.isManager() && worker.isFullTime()) {
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
        promotion.setPopularity(promotion.getPopulatirty() + increment);

        if (promotion.getPopulatirty() >= maxPop) {
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

        dailyUpdateContracts(promotion);

        if (contractManager.getPushed(promotion).size() != maxPushListSize(promotion)) {
            updatePushed(promotion);
        }

        if (dateManager.isPayDay()) {
            payDay(promotion, dateManager.today());
        }

        int activeRosterSize = contractManager.getActiveRoster(promotion).size();
        while (activeRosterSize < idealRosterSize(promotion) && !workerManager.freeAgents(promotion).isEmpty()) {
            signContract(promotion);
            activeRosterSize++;
        }

        //book a show if we have one scheduled today
        Event eventToday = eventManager.getEventOnDate(promotion, dateManager.today());
        if (eventToday != null) {
            if (contractManager.getFullRoster(promotion).size() >= 2) {
                bookEvent(eventToday, promotion);

            } else {
                //cancel event
            }
        }

    }

    private void dailyUpdateContracts(Promotion promotion) {
        //update all the contracts associated with the current promotion
        List<Contract> tempContractList = new ArrayList<>(contractManager.getContracts(promotion));
        for (Contract contract : tempContractList) {
            if (!contractManager.nextDay(contract)) {
                titleManager.stripTitles(promotion, contract);
            }
        }
    }

    //pay everyone
    public void payDay(Promotion promotion, LocalDate date) {

        for (Contract c : contractManager.getContracts(promotion)) {
            contractManager.payDay(date, c);
        }
    }

    private void sortByPopularity(List<Worker> workerList) {
        //sort roster by popularity
        Collections.sort(workerList, (Worker w1, Worker w2) -> -Integer.valueOf(w1.getPopularity()).compareTo(w2.getPopularity()));
    }

    //sign a contract with the first suitable worker found
    private void signContract(Promotion promotion) {

        for (Worker worker : workerManager.freeAgents(promotion)) {
            if (worker.getPopularity() <= ModelUtilityFunctions.maxPopularity(promotion)) {
                contractFactory.createContract(worker, promotion, dateManager.today());
                break;
            }
        }
    }

    //sign a contract with the first suitable worker found
    private void signContract(Promotion promotion, LocalDate date) {

        for (Worker worker : workerManager.freeAgents(promotion)) {
            if (worker.getPopularity() <= ModelUtilityFunctions.maxPopularity(promotion)
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

    public void bookNextEvent(Promotion promotion, LocalDate eventDate, Television television) {
        Event event = bookNextEvent(promotion, eventDate);
        event.setTelevision(television);
    }

    public void bookNextEvent(Promotion promotion, LocalDate eventDate, EventName eventName) {
        Event event = bookNextEvent(promotion, eventDate);
        event.setEventName(eventName);
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
        for (Worker worker : contractManager.getFullRoster(promotion)) {
            if (!eventManager.isBooked(worker, eventDate)) {
                eventManager.addEventWorker(new EventWorker(event, worker));
            }
        }

        return event;

    }

    public void bookNextEvent(Promotion promotion) {

        LocalDate eventDate = LocalDate.ofYearDay(dateManager.today().getYear(), dateManager.today().getDayOfYear());
        eventDate = LocalDate.from(eventDate).plusDays(ModelUtilityFunctions.randRange(25, 35));
        bookNextEvent(promotion, eventDate);

    }

    private List<Segment> bookSegments(Promotion promotion) {
        //maximum segments for the event
        int maxSegments = 8;

        List<Worker> pushList = contractManager.getPushed(promotion);

        //bigger promotions get more segments
        if (promotion.getLevel() > 3) {
            maxSegments += 2;
        }
        //lists to track workers the event roster
        //and workers that are already booked on this date
        List<Worker> eventRoster = getEventRoster(promotion);

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
        List<Segment> segments = new ArrayList<>();

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
            int random = ModelUtilityFunctions.randRange(1, 10);
            if (random > 8) {
                teamsNeeded += 10 - random;
            }

            List<List<Worker>> matchTeams = new ArrayList<>();
            List<Worker> champs = titleManager.getCurrentChampionWorkers(title);

            //if the title is not vacant, make the title holders team 1
            if (!champs.isEmpty()) {
                matchTeams.add(champs);
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
                            matchTeams.add(team);
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
                if (ModelUtilityFunctions.randRange(1, 10) > 5) {
                    Collections.swap(matchTeams, 0, 1);
                }

                Match match = matchFactory.CreateMatch(matchTeams, title);
                segments.add(match);
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
                    List<List<Worker>> teams = new ArrayList<>(Arrays.asList(teamA, teamB));
                    Match match = matchFactory.CreateMatch(teams);

                    segments.add(match);
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
        eventFactory.processEvent(event, bookSegments(promotion), dateManager.today(), promotion);
    }

    private List<Worker> getEventRoster(Promotion promotion) {
        //lists to track workers the event roster
        //and workers that are already booked on this date
        List<Worker> eventRoster = contractManager.getFullRoster(promotion);
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
            if (worker.isManager() || !worker.isFullTime() || !worker.isMainRoster()) {

                nonCompetitors.add(worker);
            }
        }

        //remove noncompetitors from the event roster
        eventRoster.removeAll(nonCompetitors);

        sortByPopularity(eventRoster);

        return eventRoster;
    }
}
