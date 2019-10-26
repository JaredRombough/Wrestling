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
@DatabaseTable(tableName = "entourage_members")
public class EntourageMemberEntity extends Entity {
    @DatabaseField(generatedId = true)
    private long entourageMemberID;

    @DatabaseField(foreign = true)
    private WorkerEntity leader;

    @DatabaseField(foreign = true)
    private WorkerEntity follower;

    @DatabaseField
    private boolean active;
}
