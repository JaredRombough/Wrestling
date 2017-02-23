package wrestling.model;

import wrestling.model.utility.UtilityFunctions;
import wrestling.model.factory.ContractFactory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PromotionAi implements Serializable {

    private final Promotion promotion;

    public final GameController gameController;

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

    //book an event
    private void bookEvent() {

        //maximum segments for the event
        int maxSegments = 8;

        //bigger promotions get more segments
        if (promotion.getLevel() > 3) {
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
            int random = UtilityFunctions.randRange(1, 10);
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
                if (UtilityFunctions.randRange(1, 10) > 5) {
                    Collections.swap(matchTeams, 0, 1);
                }

                Match match = new Match(matchTeams, title);
                segments.add(match);
            }

        }

        //fill up the segments if we don't have enough for some reason
        if (segments.size() < maxSegments) {
            //eventRoster.removeAll(matchBooked);
            //go through the roster by popularity and make singles matches
            for (int i = 0; i < eventRoster.size(); i += 2) {
                if (eventRoster.size() > i + 1) {
                    //move this somewhere else, like a matchFactory
                    List<Worker> teamA = new ArrayList<>(Arrays.asList(eventRoster.get(i)));
                    List<Worker> teamB = new ArrayList<>(Arrays.asList(eventRoster.get(i + 1)));
                    List<List<Worker>> teams = new ArrayList<>(Arrays.asList(teamA, teamB));
                    Match match = new Match(teams);
                    //Match match = new Match(eventRoster.get(i), eventRoster.get(i + 1));
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

    private List<Worker> getEventRoster() {
        //lists to track workers the event roster
        //and workers that are already booked on this date
        List<Worker> eventRoster = promotion.getFullRoster();
        List<Worker> alreadyBooked = new ArrayList<>();

        //go through the event roster and check for workers already booked
        for (Worker worker : eventRoster) {
            if (worker.isBooked(gameController.date())) {
                alreadyBooked.add(worker);
            }
        }

        //remove all booked workers from the event roster
        eventRoster.removeAll(alreadyBooked);

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

        for (Title title : promotion.getTitles()) {

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
