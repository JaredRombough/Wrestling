package openwrestling.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "injuries")
public class InjuryEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long injuryID;

    @DatabaseField
    private Date expiryDate;

    @DatabaseField
    private Date startDate;

    @DatabaseField(foreign = true)
    private WorkerEntity worker;

    @DatabaseField(foreign = true)
    private PromotionEntity promotion;

}
