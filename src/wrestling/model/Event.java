package wrestling.model;

import java.time.LocalDate;
import org.apache.commons.lang3.StringUtils;
import wrestling.model.interfaces.iEvent;
import wrestling.model.modelView.PromotionView;
import wrestling.model.interfaces.iDate;

public class Event implements iEvent, iDate {

    private final PromotionView promotion;

    private LocalDate date;
    private EventType eventType;
    private int cost;
    private int gate;
    private int attendance;
    private int defaultDuration;
    private String name;
    private EventTemplate eventTemplate;

    public Event(EventTemplate eventTemplate, LocalDate date) {
        this.eventTemplate = eventTemplate;
        this.promotion = eventTemplate.getPromotion();
        this.date = date;
        this.name = eventTemplate.getName();
        this.defaultDuration = eventTemplate.getDefaultDuration();
    }

    public Event(PromotionView promotion, LocalDate date) {
        this.promotion = promotion;
        this.date = date;
    }

    @Override
    public String toString() {
        return StringUtils.containsIgnoreCase(name, promotion.getShortName())
                ? name : promotion.getShortName() + " " + name;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public PromotionView getPromotion() {
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

    /**
     * @return the eventName
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the defaultDuration
     */
    public int getDefaultDuration() {
        return defaultDuration;
    }

    /**
     * @param defaultDuration the defaultDuration to set
     */
    public void setDefaultDuration(int defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    /**
     * @return the eventTemplate
     */
    public EventTemplate getEventTemplate() {
        return eventTemplate;
    }

    /**
     * @param eventTemplate the eventTemplate to set
     */
    public void setEventTemplate(EventTemplate eventTemplate) {
        this.eventTemplate = eventTemplate;
    }

}
