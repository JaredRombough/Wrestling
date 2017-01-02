package wrestling.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class PromotionAi implements Serializable {

    private final Promotion promotion;

    private final GameController gameController;

    public PromotionAi(Promotion promotion, GameController gameController) {
        nextEvent = randRange(2, 7);
        this.promotion = promotion;
        this.gameController = gameController;
    }

    //the date the next event is scheduled for
    private int nextEvent;

    //call this method every day for each ai
    //put the general decision making sequence here
    public void dailyUpdate() {

        //book a show if we have one scheduled today
        if (gameController.date() == nextEvent && promotion.getRoster().size() >= 2) {
            bookEvent();
            //schedule another match for next week
            nextEvent += 7;
        } else if (gameController.date() == nextEvent && promotion.getRoster().size() < 2) {
            //we don't have enough workers, postpone
            nextEvent += 7;
        }

        //sign a contract if we are under the ideal amount of workers
        if (promotion.getRoster().size() < 10 + (promotion.getLevel() * 10)) {

            signContract();

            //sign a contract if we have too many expiring contracts coming up
            //to keep our roster at a reasonable size
        } else if (promotion.getRoster().size() >= 10) {
            int expiringCount = 0;

            for (Contract contract : promotion.getContracts()) {
                if (contract.getDuration() < 14) {
                    expiringCount++;
                }
            }

            if (expiringCount > 5) {
                signContract();
            }
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

                gameController.contractFactory.createContract(worker, promotion);

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

        sortByPopularity(promotion.getRoster());

        //check for workers that are already booked on this date
        List<Worker> eventRoster = promotion.getRoster();
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

        List<Segment> segments = new ArrayList<>();

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

        gameController.eventFactory.createEvent(segments, gameController.date(), promotion);
        gameController.eventFactory.processEvent();

    }

    private int randRange(int low, int high) {
        Random r = new Random();
        return r.nextInt(high - low) + low;
    }

}
