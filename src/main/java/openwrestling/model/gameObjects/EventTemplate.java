package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.SegmentItem;
import openwrestling.model.interfaces.iRosterSplit;
import openwrestling.model.segment.constants.EventBroadcast;
import openwrestling.model.segment.constants.EventFrequency;
import openwrestling.model.segment.constants.EventVenueSize;
import org.apache.commons.lang3.RandomUtils;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventTemplate extends GameObject implements Serializable, SegmentItem, iRosterSplit {

    private long eventTemplateID;
    private Promotion promotion;
    @Builder.Default
    private int defaultDuration = 180;
    private String name;
    @Builder.Default
    private EventFrequency eventFrequency = EventFrequency.ANNUAL;
    @Builder.Default
    private EventBroadcast eventBroadcast = EventBroadcast.NONE;
    @Builder.Default
    private EventVenueSize eventVenueSize = EventVenueSize.MEDIUM;
    @Builder.Default
    private DayOfWeek dayOfWeek = Arrays.asList(
            DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).get(
            RandomUtils.nextInt(0, 2));
    @Builder.Default
    private int month = 1;
    private RosterSplit rosterSplit;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof EventTemplate &&
                Objects.equals(((EventTemplate) object).getEventTemplateID(), eventTemplateID);
    }

}
