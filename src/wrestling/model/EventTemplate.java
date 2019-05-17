package wrestling.model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.RandomUtils;
import wrestling.model.interfaces.iDate;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.StaffView;
import wrestling.model.segmentEnum.EventBroadcast;
import wrestling.model.segmentEnum.EventFrequency;
import wrestling.model.segmentEnum.EventRecurrence;
import wrestling.model.segmentEnum.EventVenueSize;

public class EventTemplate implements Serializable, iDate, SegmentItem {

    private PromotionView promotion;
    private LocalDate nextDate;
    private LocalDate bookedUntil;
    private int defaultDuration;
    private String name;
    private EventFrequency eventFrequency;
    private EventBroadcast eventBroadcast;
    private EventRecurrence eventRecurrence;
    private EventVenueSize eventVenueSize;
    private int eventsLeft;
    private DayOfWeek dayOfWeek;
    private Month month;
    private List<StaffView> defaultBroadcastTeam;
    private final List<SegmentTemplate> segmentTemplates;

    public EventTemplate() {
        bookedUntil = LocalDate.MIN;
        defaultDuration = 180;
        eventFrequency = EventFrequency.ANNUAL;
        eventBroadcast = EventBroadcast.NONE;
        eventRecurrence = EventRecurrence.UNLIMITED;
        eventVenueSize = EventVenueSize.MEDIUM;
        eventsLeft = 1;
        dayOfWeek = Arrays.asList(
                DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).get(
                        RandomUtils.nextInt(0, 2));
        month = Month.JANUARY;
        defaultBroadcastTeam = new ArrayList<>();
        segmentTemplates = new ArrayList<>();
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * @return the promotion
     */
    public PromotionView getPromotion() {
        return promotion;
    }

    /**
     * @param promotion the promotion to set
     */
    public void setPromotion(PromotionView promotion) {
        this.promotion = promotion;
    }

    /**
     * @return the nextDate
     */
    public LocalDate getBookedUntil() {
        return bookedUntil;
    }

    /**
     * @param bookedUntil the nextDate to set
     */
    public void setBookedUntil(LocalDate bookedUntil) {
        this.bookedUntil = bookedUntil;
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

    @Override
    public LocalDate getDate() {
        return nextDate;
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
     * @return the eventVenueSize
     */
    public EventVenueSize getEventVenueSize() {
        return eventVenueSize;
    }

    /**
     * @param eventVenueSize the eventVenueSize to set
     */
    public void setEventVenueSize(EventVenueSize eventVenueSize) {
        this.eventVenueSize = eventVenueSize;
    }

    /**
     * @return the defaultBroadcastTeam
     */
    public List<StaffView> getDefaultBroadcastTeam() {
        return defaultBroadcastTeam;
    }

    /**
     * @param defaultBroadcastTeam the defaultBroadcastTeam to set
     */
    public void setDefaultBroadcastTeam(List<StaffView> defaultBroadcastTeam) {
        this.defaultBroadcastTeam = defaultBroadcastTeam;
    }

    /**
     * @return the segmentTemplates
     */
    public List<SegmentTemplate> getSegmentTemplates() {
        return segmentTemplates;
    }

}
