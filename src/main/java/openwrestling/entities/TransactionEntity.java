package openwrestling.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.segmentEnum.TransactionType;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "transactions")
public class TransactionEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long transactionID;

    @DatabaseField(foreign = true)
    private PromotionEntity promotion;

    @DatabaseField
    private long amount;

    @DatabaseField
    private TransactionType type;

    @DatabaseField
    private Date date;
}
