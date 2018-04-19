package wrestling.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import wrestling.model.segmentEnum.EventBroadcast;
import wrestling.model.segmentEnum.EventFrequency;
import wrestling.model.segmentEnum.EventRecurrence;

public class EventTemplate {

    private Promotion promotion;
    private LocalDate nextDate;
    private int defaultDuration;
    private String name;
    private EventFrequency eventFrequency;
    private EventBroadcast eventBroadcast;
    private EventRecurrence eventRecurrence;
    private int eventsLeft;
    private DayOfWeek dayOfWeek;
    private Month month;

    public EventTemplate() {
        nextDate = LocalDate.MIN;
        defaultDuration = 180;
        eventFrequency = EventFrequency.ANNUAL;
        eventBroadcast = EventBroadcast.NONE;
        eventRecurrence = EventRecurrence.UNLIMITED;
        eventsLeft = 0;
        dayOfWeek = DayOfWeek.MONDAY;
        month = Month.JANUARY;
    }

    /**
     * @return the promotion
     */
    public Promotion getPromotion() {
        return promotion;
    }

    /**
     * @param promotion the promotion to set
     */
    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    /**
     * @return the nextDate
     */
    public LocalDate getNextDate() {
        return nextDate;
    }

    /**
     * @param nextDate the nextDate to set
     */
    public void setNextDate(LocalDate nextDate) {
        this.nextDate = nextDate;
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
     * @return the name
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
     * @return the eventFrequency
     */
    public EventFrequency getEventFrequency() {
        return eventFrequency;
    }

    /**
     * @param eventFrequency the eventFrequency to set
     */
    public void setEventFrequency(EventFrequency eventFrequency) {
        this.eventFrequency = eventFrequency;
    }

    /**
     * @return the eventBroadcast
     */
    public EventBroadcast getEventBroadcast() {
        return eventBroadcast;
    }

    /**
     * @param eventBroadcast the eventBroadcast to set
     */
    public void setEventBroadcast(EventBroadcast eventBroadcast) {
        this.eventBroadcast = eventBroadcast;
    }

    /**
     * @return the dayOfWeek
     */
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * @param dayOfWeek the dayOfWeek to set
     */
    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * @return the month
     */
    public Month getMonth() {
        return month;
    }

    /**
     * @param month the month to set
     */
    public void setMonth(Month month) {
        this.month = month;
    }

    /**
     * @return the eventRecurrence
     */
    public EventRecurrence getEventRecurrence() {
        return eventRecurrence;
    }

    /**
     * @param eventRecurrence the eventRecurrence to set
     */
    public void setEventRecurrence(EventRecurrence eventRecurrence) {
        this.eventRecurrence = eventRecurrence;
    }

    /**
     * @return the eventsLeft
     */
    public int getEventsLeft() {
        return eventsLeft;
    }

    /**
     * @param eventsLeft the eventsLeft to set
     */
    public void setEventsLeft(int eventsLeft) {
        this.eventsLeft = eventsLeft;
    }

}
