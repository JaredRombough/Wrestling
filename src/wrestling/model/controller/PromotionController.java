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
import wrestling.model.utility.ModelUtilityFunctions;

public class PromotionController implements Serializable {

    private final Promotion promotion;

    private final GameController gameController;
    private final ContractManager contractManager;

    private final List<Worker> pushList;
    //list of dates on which the promotion has events scheduled
    private List<LocalDate> eventDates = new ArrayList<>();

    public PromotionController(Promotion promotion, GameController gameController) {

        eventDates.add(LocalDate.from(gameController.date()).plusDays(ModelUtilityFunctions.randRange(2, 7)));
        this.promotion = promotion;
        this.gameController = gameController;
        this.pushList = new ArrayList<>();
        this.contractManager = gameController.getContractManager();
    }

    private int idealRosterSize() {
        return 10 + (getPromotion().getLevel() * 10);
    }

    private int maxPushListSize() {
        return 2 + (getPromotion().getLevel() * 2);
    }

    public void updatePushList() {

        List<Worker> departedWorkers = new ArrayList<>();

        for (Worker worker : pushList) {
            if (!contractManager.getFullRoster(getPromotion()).contains(worker)) {
                departedWorkers.add(worker);
            }
        }

        pushList.removeAll(departedWorkers);

        //list is too small
        if (contractManager.getFullRoster(getPromotion()).size() > maxPushListSize()
                && pushList.size() < maxPushListSize()) {

            for (Worker worker : contractManager.getFullRoster(getPromotion())) {
                if (!pushList.contains(worker) && pushList.size() < maxPushListSize()
                        && !worker.isManager() && worker.isFullTime()) {
                    pushList.add(worker);
                } else if (pushList.size() >= maxPushListSize()) {
                    break;
                }
            }

            //list is too big
        } else if (contractManager.getFullRoster(getPromotion()).size() > maxPushListSize()
                && pushList.size() > maxPushListSize()) {
            while (pushList.size() > maxPushListSize()) {
                pushList.remove(0);
            }
        }

    }

    public void gainPopularity() {
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
    public void dailyUpdate() {

        dailyUpdateContracts();

        if (pushList.size() != maxPushListSize()) {
            updatePushList();
        }

        if (gameController.isPayDay()) {

            payDay(gameController.date());
        }

        while (contractManager.getActiveRoster(getPromotion()).size() < idealRosterSize() && !gameController.freeAgents(promotion).isEmpty()) {
            signContract();
        }

        for (Television tv : tvToday()) {
            bookEvent(tv);
        }

        //book a show if we have one scheduled today
        if (eventDates.contains(gameController.date()) && contractManager.getFullRoster(getPromotion()).size() >= 2) {
            bookEvent();

            //schedule future events as necessary
            while (futureEvents() <= eventAmountTarget()) {

                bookNextEvent();

            }

        }

    }

    private List<Television> tvToday() {
        List<Television> today = new ArrayList<>();
        for (Television tv : gameController.getTelevision()) {
            if (tv.getPromotion().equals(promotion)
                    && tv.getDay().equals(gameController.date().getDayOfWeek())) {
                today.add(tv);
            }
        }

        return today;
    }

    //check if we have any outstanding titles from expired contracts
    public void stripTitles(Contract c) {
        for (Title t : promotion.getTitles()) {
            for (Worker w : t.getWorkers()) {
                if (w.equals(c.getWorker())) {
                    gameController.getTitleFactory().stripTitle(t, gameController.date());
                }
            }
        }
    }

    public void dailyUpdateContracts() {
        //update all the contracts associated with the current promotion
        List<Contract> tempContractList = new ArrayList<>(contractManager.getContracts(promotion));
        for (Contract contract : tempContractList) {
            if (!gameController.getContractManager().nextDay(contract)) {
                gameController.getContractFactory().reportExpiration(contract);
                stripTitles(contract);
            }
        }
    }

    //pay everyone
    private void payDay(LocalDate date) {

        for (Contract c : contractManager.getContracts(promotion)) {
            gameController.getContractManager().payDay(date, c);
        }
    }

    private void sortByPopularity(List<Worker> workerList) {
        //sort roster by popularity
        Collections.sort(workerList, (Worker w1, Worker w2) -> -Integer.valueOf(w1.getPopularity()).compareTo(w2.getPopularity()));
    }

    //sign a contract with the first suitable worker found
    private void signContract() {

        for (Worker worker : gameController.freeAgents(getPromotion())) {
            if (worker.getPopularity() <= ModelUtilityFunctions.maxPopularity(getPromotion())) {

                gameController.getContractFactory().createContract(worker, getPromotion(), gameController.date());

                break;
            }
        }

    }

    //sign a contract with the first suitable worker found
    private void signContract(LocalDate date) {

        for (Worker worker : gameController.freeAgents(getPromotion())) {
            if (worker.getPopularity() <= ModelUtilityFunctions.maxPopularity(getPromotion()) && !worker.getController().isBooked(date)) {

                gameController.getContractFactory().createContract(worker, getPromotion(), gameController.date());

                break;
            }
        }

    }

    //determine how many future events the promotion is meant to have at a given time
    private int eventAmountTarget() {

        int target = 0;

        switch (getPromotion().getLevel()) {
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

    private int futureEvents() {

        int futureEvents = 0;

        for (LocalDate ed : eventDates) {
            if (ed.isAfter(gameController.date())) {
                futureEvents++;
            }
        }

        return futureEvents;
    }

    private void bookNextEvent() {

        LocalDate eventDate = LocalDate.ofYearDay(gameController.date().getYear(), gameController.date().getDayOfYear());
        eventDate = LocalDate.from(eventDate).plusDays(30);
        LocalDate bestDate = eventDate;
        double threshold = 0.8;
        double bestThreshold = 0;

        boolean dateFound = false;

        //go through a range of dates after the first acceptable next event date
        for (int i = 0; i < 40; i++) {

            eventDate = LocalDate.from(eventDate).plusDays(1);

            //if we don't already have an event scheduled on this date
            if (!eventDates.contains(eventDate)) {

                //count the workers that are availeable on the date
                double available = 0;
                for (Worker worker : contractManager.getActiveRoster(getPromotion())) {
                    if (!worker.getController().isBooked(eventDate, promotion)) {
                        available++;
                    }
                }

                double percentAvailable = available / (double) contractManager.getActiveRoster(getPromotion()).size();

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

            int workersNeeded = (int) Math.round((threshold - bestThreshold) * contractManager.getActiveRoster(getPromotion()).size());

            for (int i = 0; i < workersNeeded; i++) {
                signContract(gameController.date());
            }

        }

        //book the roster for the date
        for (Worker worker : contractManager.getFullRoster(getPromotion())) {
            if (!worker.getController().isBooked(eventDate)) {
                worker.getController().getContract(getPromotion()).bookDate(eventDate);

            }
        }

        eventDates.add(eventDate);

    }

    private List<Segment> bookSegments() {
        //maximum segments for the event
        int maxSegments = 8;

        //bigger promotions get more segments
        if (getPromotion().getLevel() > 3) {
            maxSegments += 2;
        }
        //lists to track workers the event roster
        //and workers that are already booked on this date
        List<Worker> eventRoster = getEventRoster();

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
        List<Title> eventTitles = getEventTitles(eventRoster);

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
    private void bookEvent() {

        gameController.getEventFactory().createEvent(bookSegments(), gameController.date(), getPromotion());

    }

    private void bookEvent(Television television) {
        gameController.getEventFactory().createEvent(bookSegments(), gameController.date(), getPromotion(), television);
    }

    private List<Worker> getEventRoster() {
        //lists to track workers the event roster
        //and workers that are already booked on this date
        List<Worker> eventRoster = contractManager.getFullRoster(getPromotion());
        List<Worker> unavailable = new ArrayList<>();

        //go through the event roster and check for workers already booked
        for (Worker worker : eventRoster) {

            //the worker is unavailable if they are booked and the booking isn't with us
            if (worker.getController().isBooked(gameController.date(), getPromotion())) {
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
    private List<Title> getEventTitles(List<Worker> eventRoster) {

        List<Title> eventTitles = new ArrayList<>();

        for (Title title : getPromotion().getTitles()) {

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

    /**
     * @return the promotion
     */
    public Promotion getPromotion() {
        return promotion;
    }

}
