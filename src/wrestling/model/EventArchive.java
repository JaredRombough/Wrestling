package wrestling.model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * for storing a completed event
 *
 * we will need a static list of workers the cost the profits the date the match
 * results, ratings
 *
 * all of this needs to be fixed and unchangeable
 */
public class EventArchive implements Serializable {

    private final int totalCost;

    private final int gate;

    private final List<Segment> segments;

    private final Promotion promotion;

    private final int date;
    
    private final int attendance;

    public EventArchive(Promotion promotion, final List<Segment> segments, final int totalCost, final int gate, final int attendance, int date) {
        this.gate = gate;
        this.totalCost = totalCost;
        this.segments = segments;
        this.promotion = promotion;
        this.date = date;
        this.attendance = attendance;
    }

    public String getSummary() {
        String eventString = new String();

        for (Segment segment : getSegments()) {

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
        eventString += "Total cost: $" + getTotalCost();
        eventString += "\n";
        eventString += "Total gate: $" + getGate();

        return eventString;
    }

    @Override
    public String toString() {
        String string = new String();
        string += getPromotion().toString() + " event, day " + getDate();
        return string;
    }

    /**
     * @return the totalCost
     */
    public int getTotalCost() {
        return totalCost;
    }

    /**
     * @return the gate
     */
    public int getGate() {
        return gate;
    }

    /**
     * @return the segments
     */
    public List<Segment> getSegments() {
        return segments;
    }

    /**
     * @return the promotion
     */
    public Promotion getPromotion() {
        return promotion;
    }

    /**
     * @return the date
     */
    public int getDate() {
        return date;
    }
}
