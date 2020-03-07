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
@DatabaseTable(tableName = "contracts")
public class ContractEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long contractID;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private PromotionEntity promotion;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private WorkerEntity worker;

    @DatabaseField
    private boolean active;

    @DatabaseField
    private Date startDate;

    @DatabaseField
    private Date endDate;

    @DatabaseField
    private Date lastShowDate;

    @DatabaseField
    private boolean exclusive;

    @DatabaseField
    private boolean pushed;

    @DatabaseField
    private int appearanceCost;

    @DatabaseField
    private int monthlyCost;

}
