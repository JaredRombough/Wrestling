package wrestling.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class PromotionAi implements Serializable {
    
    private Promotion promotion;
    
    private GameController gameController;
    
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
        if (gameController.date() == nextEvent) {
            bookEvent();
            //schedule another match for next week
            nextEvent += 7;
        }
        
    }
    
    private void bookEvent() {
        
        //sort roster by popularity
        Collections.sort(promotion.roster, new Comparator<Worker>() {
            @Override
            public int compare(Worker w1, Worker w2) {
                return Integer.valueOf(w1.getPopularity()).compareTo(w2.getPopularity());
            }
        });

        List<Segment> segments = new ArrayList<>();
        
        //go through the roster by popularity and make singles matches
        for (int i = 0; i < promotion.roster.size(); i+=2) {
            if(promotion.roster.size() > i + 1) {
                Match match = new Match(promotion.roster.get(i), promotion.roster.get(i + 1));
                segments.add(match);
            }
        }
        
        Event event = new Event(segments, gameController.date(), promotion);
        event.processEvent();
        
    }

}
