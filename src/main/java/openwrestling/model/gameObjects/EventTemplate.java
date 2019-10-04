package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import openwrestling.model.SegmentItem;
import openwrestling.model.SegmentTemplate;
import openwrestling.model.gameObjects.GameObject;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.RosterSplit;
import openwrestling.model.interfaces.iDate;
import openwrestling.model.interfaces.iRosterSplit;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.segmentEnum.EventBroadcast;
import openwrestling.model.segmentEnum.EventFrequency;
import openwrestling.model.segmentEnum.EventRecurrence;
import openwrestling.model.segmentEnum.EventVenueSize;
import org.apache.commons.lang3.RandomUtils;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EventTemplate extends GameObject implements Serializable, iDate, SegmentItem, iRosterSplit {

    private Promotion promotion;
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
    private int month;
    private List<StaffMember> defaultBroadcastTeam;
    private final List<SegmentTemplate> segmentTemplates;
    private RosterSplit rosterSplit;

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
        month = 1;
        defaultBroadcastTeam = new ArrayList<>();
        segmentTemplates = new ArrayList<>();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public LocalDate getDate() {
        return nextDate;
    }

}
