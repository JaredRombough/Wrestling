package wrestling.model;

import wrestling.model.utility.UtilityFunctions;
import wrestling.model.factory.ContractFactory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PromotionAi implements Serializable {

    private final Promotion promotion;

    private final GameController gameController;

    public PromotionAi(Promotion promotion, GameController gameController) {
        nextEvent = UtilityFunctions.randRange(2, 7);
        this.promotion = promotion;
        this.gameController = gameController;
        this.pushList = new ArrayList<>();
    }

    //the date the next event is scheduled for
    private int nextEvent;

    private List<Worker> pushList;

    private int idealRosterSize() {
        return 10 + (promotion.getLevel() * 10);
    }

    public void updatePushList() {
        int maxPushListSize = 2 + (promotion.getLevel() * 2);

        List<Worker> departedWorkers = new ArrayList<>();

        for (Worker worker : pushList) {
            if (!promotion.getFullRoster().contains(worker)) {
                departedWorkers.add(worker);
            }
        }

        pushList.removeAll(departedWorkers);

        //list is too small
        if (promotion.getFullRoster().size() > maxPushListSize
                && pushList.size() < maxPushListSize) {

            for (Worker worker : promotion.getFullRoster()) {
                if (!pushList.contains(worker) && pushList.size() < maxPushListSize
                        && !worker.isManager() && worker.isFullTime()) {
                    pushList.add(worker);
                } else if (pushList.size() >= maxPushListSize) {
                    break;
                }
            }

            //list is too big
        } else if (promotion.getFullRoster().size() > maxPushListSize
                && pushList.size() > maxPushListSize) {
            while (pushList.size() > maxPushListSize) {
                pushList.remove(0);
            }
        }

    }

    //call this method every day for each ai
    //put the general decision making sequence here
    public void dailyUpdate() {

        while (promotion.getActiveRoster().size() < idealRosterSize() && !gameController.freeAgents(promotion).isEmpty()) {
            signContract();
        }

        //book a show if we have one scheduled today
        if (gameController.date() == nextEvent && promotion.getFullRoster().size() >= 2) {
            bookEvent();
            //schedule another match for next week
            nextEvent += 7;
        } else if (gameController.date() == nextEvent && promotion.getFullRoster().size() < 2) {
            //we don't have enough workers, postpone
            nextEvent += 7;
        }

    }

    private void sortByPopularity(List<Worker> workerList) {
        //sort roster by popularity
        Collections.sort(workerList, new Comparator<Worker>() {
            @Override
            public int compare(Worker w1, Worker w2) {
                return -Integer.valueOf(w1.getPopularity()).compareTo(w2.getPopularity());
            }
        });
    }

    //sign a contract with the first suitable worker found
    private void signContract() {

        for (Worker worker : gameController.freeAgents(promotion)) {
            if (worker.getPopularity() <= promotion.maxPopularity()) {

                ContractFactory.createContract(worker, promotion);

                break;
            }
        }

    }

    /*
    
    ideas?
    
    have a list of guys to push
    loop through list by pop
    for each loop
    random chance of match type
    random chance of pushed opponent for big event
    random non-pushed opponents/partners fill out match
    random chance of win/loss
    
    
    have a way to check if a match is good
    have a budget
    
     */
    private void bookEvent() {

        int maxSegments = 8;

        if (promotion.getLevel() > 3) {
            maxSegments += 2;
        }

        //check for workers that are already booked on this date
        List<Worker> eventRoster = promotion.getFullRoster();
        List<Worker> alreadyBooked = new ArrayList<>();

        for (Worker worker : eventRoster) {
            if (worker.isBooked(gameController.date())) {
                alreadyBooked.add(worker);
            }
        }

        eventRoster.removeAll(alreadyBooked);

        List<Worker> nonCompetitors = new ArrayList<>();

        for (Worker worker : eventRoster) {
            if (worker.isManager() || !worker.isFullTime() || !worker.isMainRoster()) {
                nonCompetitors.add(worker);
            }
        }

        eventRoster.removeAll(nonCompetitors);

        List<Worker> pushListPresent = new ArrayList<>();

        for (int i = 0; i < pushList.size(); i++) {
            if (eventRoster.contains(pushList.get(i))) {
                eventRoster.remove(pushList.get(i));
                pushListPresent.add(pushList.get(i));
            }
        }
        eventRoster.removeAll(pushList);
        sortByPopularity(pushList);
        sortByPopularity(eventRoster);

        List<Segment> segments = new ArrayList<>();
        List<Worker> matchBooked = new ArrayList<>();

        for (int i = 0; i < pushListPresent.size(); i++) {

            if (eventRoster.size() == matchBooked.size()) {
                break;
            }

            //here we would randomly determine if it is a match or other segment type
            //determine the number of teams (usually 2)
            int teams = 2;
            int random = UtilityFunctions.randRange(1, 10);
            if (random > 8) {
                teams += 10 - random;
            }

            //determine the size of teams (usually 1)
            int teamSize = 1;
            random = UtilityFunctions.randRange(1, 10);
            if (random > 8) {
                teamSize += 10 - random;
            }

            List<List<Worker>> matchTeams = new ArrayList<>();

            //iterate through the teams we have to fill
            for (int a = 0; a < teams; a++) {

                List<Worker> team = new ArrayList<>();

                //add the push target if this is the first spot on the first team
                if (matchTeams.isEmpty() && team.isEmpty()) {
                    team.add(pushList.get(i));
                }

                //iterate through the event roster to fill in the team
                for (Worker worker : eventRoster) {
                    //make sure the team isn't already full
                    if (team.size() >= teamSize) {
                        break;
                    }

                    //add the worker if they aren't in a match and they aren't being pushed
                    if (!matchBooked.contains(worker)
                            && !pushList.contains(worker)) {
                        team.add(worker);
                    }
                }

                matchTeams.add(team);
                matchBooked.addAll(team);
            }

            Match match = new Match(matchTeams);

            if (UtilityFunctions.randRange(1, 10) > 5 && matchTeams.size() >= 1) {
                match.setWinner(1);
            }

            segments.add(match);

            if (segments.size() > maxSegments) {
                break;
            }

        }

        //fill up the segments if we don't have enough for some reason
        if (segments.size()
                < maxSegments) {
            eventRoster.removeAll(matchBooked);
            //go through the roster by popularity and make singles matches
            for (int i = 0; i < eventRoster.size(); i += 2) {
                if (eventRoster.size() > i + 1) {
                    Match match = new Match(eventRoster.get(i), eventRoster.get(i + 1));
                    segments.add(match);
                }

                if (segments.size() > maxSegments) {
                    break;
                }

            }
        }

        gameController.eventFactory.createEvent(segments, gameController.date(), promotion);
        gameController.eventFactory.processEvent();

    }

}
