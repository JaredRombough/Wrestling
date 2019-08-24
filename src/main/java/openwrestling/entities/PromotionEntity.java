package openwrestling.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "promotions")
public class PromotionEntity extends Entity {

    @DatabaseField(generatedId = true)
    private int promotionID;

    @DatabaseField
    private String name;

    @DatabaseField
    private String shortName;

    @DatabaseField
    private String imagePath;

    @DatabaseField
    private int popularity;

    @DatabaseField
    private int level;
//    private List<WorkerView> fullRoster;
//    private List<StaffView> allStaff;
//    private List<StaffView> defaultBroadcastTeam;
//    private List<EventTemplate> eventTemplates;
}
