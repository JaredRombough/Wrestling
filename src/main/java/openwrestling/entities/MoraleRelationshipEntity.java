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
@DatabaseTable(tableName = "morale_relationships")
public class MoraleRelationshipEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long relationshipID;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private WorkerEntity worker;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private PromotionEntity promotion;

    @DatabaseField
    private int level;
}
