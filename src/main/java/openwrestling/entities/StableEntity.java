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
@DatabaseTable(tableName = "stables")
public class StableEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long stableID;

    @DatabaseField
    private String name;

    @ForeignCollectionField(eager = true)
    public ForeignCollection<StableWorkerEntity> stableWorkers;

    @DatabaseField(foreign = true)
    private PromotionEntity owner;

    private Collection<WorkerEntity> workers;

    public List<? extends Entity> childrenToInsert() {
        return workers.stream().map(worker ->
            StableWorkerEntity.builder()
                    .workerEntity(worker)
                    .stableEntity(this)
                    .build()
        ).collect(Collectors.toList());
    }

}
