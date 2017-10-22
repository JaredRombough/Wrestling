package wrestling.model.controller;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import wrestling.model.Contract;
import wrestling.model.Match;
import wrestling.model.Promotion;
import wrestling.model.Segment;
import wrestling.model.Television;
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.model.factory.ContractFactory;
import wrestling.model.factory.EventFactory;
import wrestling.model.utility.ModelUtilityFunctions;

public class PromotionController implements Serializable {

    private final ContractFactory contractFactory;
    private final EventFactory eventFactory;

    private final BookingManager bookingManager;
    private final ContractManager contractManager;
    private final DateManager dateManager;
    private final PromotionEventManager eventManager;
    private final TelevisionManager televisionManager;
    private final TitleManager titleManager;
    private final WorkerManager workerManager;

    public PromotionController(
            ContractFactory contractFactory,
            EventFactory eventFactory,
            BookingManager bookingManager,
            ContractManager contractManager,
            DateManager dateManager,
            PromotionEventManager eventManager,
            TelevisionManager televisionManager,
            TitleManager titleManager,
            WorkerManager workerManager) {
        this.contractFactory = contractFactory;
        this.eventFactory = eventFactory;
        this.bookingManager = bookingManager;
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

        int eventsBooked = 0;

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

        for (Television tv : televisionManager.tvOnDay(promotion, dateManager.today())) {
            bookEvent(promotion, tv);
            eventsBooked++;
        }

        //book a show if we have one scheduled today
        if (eventManager.hasEventOnDate(promotion, dateManager.today())
                && contractManager.getFullRoster(promotion).size() >= 2) {
            bookEvent(promotion);

            //schedule future events as necessary
            int futureDates = eventManager.eventsAfterDate(promotion, dateManager.today());
            for (int i = futureDates; i < eventAmountTarget(promotion); i++) {
                bookNextEvent(promotion);
                eventsBooked++;
            }
        }

        if (eventsBooked > 0) {
            gainPopularity(promotion);
        }

    }

    private void dailyUpdateContracts(Promotion promotion) {
        //update all the contracts associated with the current promotion
        List<Contract> tempContractList = new ArrayList<>(contractManager.getContracts(promotion));
        for (Contract contract : tempContractList) {
            if (!contractManager.nextDay(contract)) {
                contractManager.reportExpiration(contract);
                titleManager.stripTitles(promotion, contract, dateManager.today());
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
                    && !bookingManager.isBooked(worker, date)) {
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

    private void bookNextEvent(Promotion promotion) {

        LocalDate eventDate = LocalDate.ofYearDay(dateManager.today().getYear(), dateManager.today().getDayOfYear());
        eventDate = LocalDate.from(eventDate).plusDays(30);
        LocalDate bestDate = eventDate;
        double threshold = 0.8;
        double bestThreshold = 0;

        boolean dateFound = false;

        //go through a range of dates after the first acceptable next event date
        for (int i = 0; i < 40; i++) {

            eventDate = LocalDate.from(eventDate).plusDays(1);

            //if we don't already have an event scheduled on this date
            if (!eventManager.hasEventOnDate(promotion, eventDate)) {

                //count the workers that are availeable on the date
                double available = 0;
                for (Worker worker : contractManager.getActiveRoster(promotion)) {
                    if (bookingManager.isAvailable(worker, eventDate, promotion)) {
                        available++;
                    }
                }

                double percentAvailable = available / (double) contractManager.getActiveRoster(promotion).size();

                //if a large enough portion of the roster is available, book it
                if (percentAvailable > threshold) {
                    dateFound = true;
                    break;

                } else if (percentAvailable > bestThreshold) {

                    bestThreshold = percentAvailable;
                    bestDate = eventDate;
                }
            }

        }

        //we didn't find a good date, take the best we did find and book some extra people
        if (!dateFound) {

            eventDate = bestDate;

            int workersNeeded = (int) Math.round((threshold - bestThreshold) * contractManager.getActiveRoster(promotion).size());

            for (int i = 0; i < workersNeeded; i++) {
                signContract(promotion, dateManager.today());
            }
        }

        //book the roster for the date
        for (Worker worker : contractManager.getFullRoster(promotion)) {
            if (!bookingManager.isBooked(worker, eventDate)) {
                contractManager.getContract(worker, promotion).bookDate(eventDate);
            }
        }

        eventManager.addEventDate(eventDate, promotion);
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
        List<Title> eventTitles = getEventTitles(promotion, eventRoster);

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

            //if the title is not vacant, make the title holders team 1
            if (!title.isVacant()) {
                matchTeams.add(title.getWorkers());
                matchBooked.addAll(title.getWorkers());
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

                Match match = new Match(matchTeams, title);
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
                    Match match = new Match(teams);

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
    private void bookEvent(Promotion promotion) {
        eventFactory.createEvent(bookSegments(promotion), dateManager.today(), promotion);
    }

    private void bookEvent(Promotion promotion, Television television) {
        eventFactory.createEvent(bookSegments(promotion), dateManager.today(), promotion, television);
    }

    private List<Worker> getEventRoster(Promotion promotion) {
        //lists to track workers the event roster
        //and workers that are already booked on this date
        List<Worker> eventRoster = contractManager.getFullRoster(promotion);
        List<Worker> unavailable = new ArrayList<>();

        //go through the event roster and check for workers already booked
        for (Worker worker : eventRoster) {

            //the worker is unavailable if they are booked and the booking isn't with us
            if (!bookingManager.isAvailable(worker, dateManager.today(), promotion)) {
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

    //returns a list of titles available for an event
    private List<Title> getEventTitles(Promotion promotion, List<Worker> eventRoster) {

        List<Title> eventTitles = new ArrayList<>();

        for (Title title : titleManager.getTitles(promotion)) {

            if (title.getWorkers().isEmpty()) {
                eventTitles.add(title);
            } else {
                boolean titleWorkersPresent = true;

                for (Worker worker : title.getWorkers()) {
                    if (!eventRoster.contains(worker)) {
                        titleWorkersPresent = false;
                    }
                }
                if (titleWorkersPresent) {
                    eventTitles.add(title);
                }
            }
        }
        return eventTitles;
    }
}
