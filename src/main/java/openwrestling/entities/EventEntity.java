package openwrestling.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.segmentEnum.EventType;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "events")
public class EventEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long eventID;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private PromotionEntity promotion;

    @DatabaseField
    private Date date;

    @DatabaseField
    private EventType eventType;

    @DatabaseField
    private int cost;

    @DatabaseField
    private int gate;

    @DatabaseField
    private int attendance;

    @DatabaseField
    private int defaultDuration;

    @DatabaseField
    private String name;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private EventTemplateEntity eventTemplate;

}
