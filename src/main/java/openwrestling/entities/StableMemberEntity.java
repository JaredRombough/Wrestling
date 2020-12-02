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
@DatabaseTable(tableName = "stable_members")
public class StableMemberEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long stableMemberID;

    @DatabaseField(foreign = true)
    private WorkerEntity worker;

    @DatabaseField(foreign = true)
    private StableEntity stable;
}
