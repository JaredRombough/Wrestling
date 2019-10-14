package openwrestling.entities;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
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
    private long promotionID;

    @DatabaseField
    private int importKey;

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

    @ForeignCollectionField(eager = true)
    public ForeignCollection<ContractEntity> contractEntities;


//    public List<ContractEntity> contracts;

//    private List<StaffView> allStaff;
//    private List<StaffView> defaultBroadcastTeam;
//    private List<EventTemplate> eventTemplates;
}
