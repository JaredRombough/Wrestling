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
@DatabaseTable(tableName = "roster_split_workers")
public class RosterSplitWorkerEntity extends Entity {
    @DatabaseField(generatedId = true)
    private long rosterSplitWorkerID;

    @DatabaseField(foreign = true)
    private WorkerEntity workerEntity;

    @DatabaseField(foreign = true)
    private RosterSplitEntity rosterSplitEntity;
}
