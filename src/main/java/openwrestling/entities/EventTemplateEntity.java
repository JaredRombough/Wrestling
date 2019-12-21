package openwrestling.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.segmentEnum.EventBroadcast;
import openwrestling.model.segmentEnum.EventFrequency;
import openwrestling.model.segmentEnum.EventVenueSize;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "event_templates")
public class EventTemplateEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long eventTemplateID;

    @DatabaseField(foreign = true)
    private PromotionEntity promotion;

    @DatabaseField
    private Date nextDate;

    @DatabaseField
    private Date bookedUntil;

    @DatabaseField
    private long defaultDuration;

    @DatabaseField
    private String name;

    @DatabaseField
    private EventFrequency eventFrequency;

    @DatabaseField
    private EventBroadcast eventBroadcast;

    @DatabaseField
    private EventVenueSize eventVenueSize;

    @DatabaseField
    private long eventsLeft;

    @DatabaseField
    private DayOfWeek dayOfWeek;

    @DatabaseField
    private long month;

    @DatabaseField(foreign = true)
    private RosterSplitEntity rosterSplit;

    @ForeignCollectionField
    private Collection<EventEntity> events;
}
