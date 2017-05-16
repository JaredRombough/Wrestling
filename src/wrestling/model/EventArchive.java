package wrestling.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * for storing a completed event
 *
 * 
 */
public class EventArchive implements Serializable {

    private final int totalCost;

    private final int gate;

    private final String promotionName;

    private final LocalDate date;

    private final int attendance;

    private String summary;

    public EventArchive(String promotionName, final int totalCost, final int gate, final int attendance, LocalDate date, String summary) {
        this.gate = gate;
        this.totalCost = totalCost;
        this.promotionName = promotionName;
        this.date = date;
        this.attendance = attendance;
        this.summary = summary;
    }

    public String getSummary() {

        return this.summary;
    }

    @Override
    public String toString() {
        String string = "";
        string += promotionName + " event, day " + getDate();
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
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @return the attendance
     */
    public int getAttendance() {
        return attendance;
    }
}
