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
import org.apache.commons.collections4.CollectionUtils;

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
        if (CollectionUtils.isEmpty(workers)) {
            return List.of();
        }
        return workers.stream().map(worker ->
                RosterSplitWorkerEntity.builder()
                        .worker(worker)
                        .rosterSplit(this)
                        .build()
        ).collect(Collectors.toList());
    }

    public void selectChildren() {
        workers = rosterSplitWorkers.stream()
                .map(RosterSplitWorkerEntity::getWorker)
                .collect(Collectors.toList());
    }

}
