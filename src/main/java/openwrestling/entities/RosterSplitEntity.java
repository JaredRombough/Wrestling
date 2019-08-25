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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "roster_splits")
public class RosterSplitEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long rosterSplitID;

    @DatabaseField
    private String name;

    @ForeignCollectionField(eager = true)
    public ForeignCollection<RosterSplitWorkerEntity> rosterSplitWorkers;

    @DatabaseField(foreign = true)
    private PromotionEntity owner;

    private Collection<WorkerEntity> workers;

    public List<? extends Entity> childrenToInsert() {
        return workers.stream().map(worker ->
                RosterSplitWorkerEntity.builder()
                        .workerEntity(worker)
                        .rosterSplitEntity(this)
                        .build()
        ).collect(Collectors.toList());
    }
}
