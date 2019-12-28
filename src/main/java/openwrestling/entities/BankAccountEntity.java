package openwrestling.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "bank_accounts")
public class BankAccountEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long bankAccountID;

    @DatabaseField(foreign = true)
    private PromotionEntity promotion;

    @DatabaseField
    private long funds;

}
