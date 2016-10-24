package wrestling.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PromotionAi implements Serializable {

    private final Promotion promotion;

    private final GameController gameController;

    public PromotionAi(Promotion promotion, GameController gameController) {
        nextEvent = 2;
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

        if (promotion.getRoster().size() < (promotion.getLevel() * 2) + 10) {
            signContract();

        }

    }

    private void sortByPopularity(List<Worker> workerList) {
        //sort roster by popularity
        Collections.sort(workerList, new Comparator<Worker>() {
            @Override
            public int compare(Worker w1, Worker w2) {
                return Integer.valueOf(w1.getPopularity()).compareTo(w2.getPopularity());
            }
        });
    }

    //sign a contract with the first suitable worker found
    private void signContract() {

        for (Worker worker : gameController.freeAgents(promotion)) {
            if (worker.getPopularity() <= promotion.maxPopularity()) {
                Contract contract = new Contract(worker, promotion, true, true, 12);
                worker.addContract(contract);
                promotion.addContract(contract);
                break;
            }
        }

    }

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

        List<Segment> segments = new ArrayList<>();

        //go through the roster by popularity and make singles matches
        for (int i = 0; i < eventRoster.size(); i += 2) {
            if (eventRoster.size() > i + 1) {
                Match match = new Match(eventRoster.get(i), eventRoster.get(i + 1));
                segments.add(match);
            }
            
            if(segments.size() > maxSegments) {
                break;
            }
        }

        Event event = new Event(segments, gameController.date(), promotion);
        event.scheduleEvent(gameController.date());

    }

}
