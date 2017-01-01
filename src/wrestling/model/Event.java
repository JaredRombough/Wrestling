package wrestling.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * an Event has a date, promotion, a list of segments (matches etc.) 
 * when the event is processed, it creates an EventArchive which is passed
 * to the promotion
 *
 */
public class Event implements Serializable {
    
    private List<Segment> segments;
    
    private Integer date;
    
    public int getDate() {
        return date;
    }
    
    private Promotion promotion;
    
    public Promotion getPromotion() {
        return this.promotion;
    }
    
    private boolean isComplete;
    
    public boolean isComplete() {
        return isComplete;
    }
    
    public Event(Integer date, Promotion promotion) {
        this.isComplete = false;
        this.segments = new ArrayList<Segment>();
        this.date = date;
        this.promotion = promotion;
        
    }
    
    public Event(final List<Segment> segments, Integer date, Promotion promotion) {
        this.isComplete = false;
        this.segments = new ArrayList<Segment>(segments);
        this.date = date;
        this.promotion = promotion;
        
    }
    
    public void setSegments(final List<Segment> segments) {
        this.segments = segments;
    }
    
    public void scheduleEvent(int date) {
        this.date = date;
        for (Worker worker : allWorkers()) {
            worker.addBooking(this);
        }

        //this is important, adds the event to the promotions' list
        //otherwise the event will be lost
        this.promotion.scheduleEvent(this);
        
    }

    /*
    to be run once only when the event actually happens
    process money and anything else
    injuries or something like that
     */
    public void processEvent() {
        
        processContracts();
        
        processSegments();
        
        promotion.gainPopularity();
        promotion.addFunds(grossProfit());
        
        isComplete = true;
        
       
        
        //this is all that will remain of the event
        EventArchive eventArchive = new EventArchive(promotion.getName(), currentCost(), grossProfit(), attendance(), date, getSummary());
        promotion.archiveEvent(eventArchive);
        
    }
    
    public String getSummary() {
        String eventString = new String();

        for (Segment segment : segments) {

            if (segment.isComplete()) {
                eventString += segment.toString();
                if (segment instanceof Match) {
                    eventString += "\n";
                    eventString += "Rating: " + ((Match) segment).segmentRating();

                }
                eventString += "\n";
            }
        }

        eventString += "\n";
        eventString += "Total cost: $" + currentCost();
        eventString += "\n";
        eventString += "Gross profit: $" + grossProfit();

        return eventString;
    }
    
    private int attendance() {
        int attendance = 0;
        
        switch (promotion.getLevel()) {
            case 0:
                attendance += 50;
                break;
            case 1:
                attendance += 100;
                break;
            case 2:
                attendance += 250;
                break;
            case 3:
                attendance += 500;
                break;
            case 4:
                attendance += 2000;
                break;
            case 5:
                attendance += 5000;
                break;
        }
        
        return attendance;
    }

    /*
    runs through all contracts associated with the event
    and takes money from the promotion accordingly
    also notifies contracts of appearances
     */
    private void processContracts() {
        
        for (Worker worker : allWorkers()) {
            promotion.removeFunds(worker.getContract(promotion).getUnitCost());
            worker.getContract(promotion).appearance();
            
        }
        
    }
    
    private void processSegments() {
        for (Segment segment : segments) {
            segment.processSegment();
        }
        
    }

    //this will return a list of all workers currently booked
    //without any duplicates
    //so if a worker is in two different segments he is only on the list
    //one time. useful for cost calculation so we don't pay people
    //twice for the same show
    private List<Worker> allWorkers() {
        
        List allWorkers = new ArrayList<Worker>();
        for (Segment currentSegment : segments) {
            allWorkers.addAll(currentSegment.allWorkers());
            
        }

        //this should take the list of workers generated above
        //and convert it to a set, removing duplicates
        Set<Worker> allWorkersSet = new LinkedHashSet<>(allWorkers);
        //convert the set back to a list with no duplicates
        allWorkers = new ArrayList<Worker>(allWorkersSet);
        
        return allWorkers;
    }
    
    //dynamic current cost calculation to be called while the player is booking
    public int currentCost() {
        
        int currentCost = 0;
        
        for (Worker currentWorker : allWorkers()) {
            currentCost += currentWorker.getContract(promotion).getUnitCost();
        }
        
        return currentCost;
    }
    
    //gross profit for the event
    private int grossProfit() {
        int grossProfit = 0;
        
        grossProfit = attendance() * 10;
        for(int i = 0; i < promotion.getLevel(); i++) {
            grossProfit += attendance() * 2;
        }
        
        return grossProfit;
    }
    
    @Override
    public String toString() {
        String string = new String();
        string += promotion.toString() + " event, day " + date;
        return string;
    }
    
}
