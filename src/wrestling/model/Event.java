package wrestling.model;

import java.time.LocalDate;
import wrestling.model.interfaces.iEvent;

public class Event implements iEvent {

    private final Promotion promotion;

    private LocalDate date;
    private EventType eventType;
    private int cost;
    private int gate;
    private int attendance;
    private Television television;

    public Event(Promotion promotion, LocalDate date, EventType eventType, int cost, int gate, int attendance) {
        this.promotion = promotion;
        this.date = date;
        this.eventType = eventType;
        this.cost = cost;
        this.gate = gate;
        this.attendance = attendance;
    }

    public Event(Promotion promotion, LocalDate date) {
        this.promotion = promotion;
        this.date = date;
    }

    @Override
    public String toString() {
        String eventName = television == null
                ? promotion.getShortName() + " event"
                : television.getName();
        return eventName + " " + date.toString();
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    /**
     * @return the eventType
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * @return the cost
     */
    public int getCost() {
        return cost;
    }

    /**
     * @return the gate
     */
    public int getGate() {
        return gate;
    }

    /**
     * @return the attendance
     */
    public int getAttendance() {
        return attendance;
    }

    /**
     * @return the television
     */
    public Television getTelevision() {
        return television;
    }

    /**
     * @param television the television to set
     */
    public void setTelevision(Television television) {
        this.television = television;
    }

    /**
     * @param eventType the eventType to set
     */
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    /**
     * @param cost the cost to set
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * @param gate the gate to set
     */
    public void setGate(int gate) {
        this.gate = gate;
    }

    /**
     * @param attendance the attendance to set
     */
    public void setAttendance(int attendance) {
        this.attendance = attendance;
    }

}
