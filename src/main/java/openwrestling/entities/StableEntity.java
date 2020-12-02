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
@DatabaseTable(tableName = "stables")
public class StableEntity extends Entity {

    @ForeignCollectionField(eager = true)
    public ForeignCollection<StableMemberEntity> stableWorkers;
    @DatabaseField(generatedId = true)
    private long stableID;
    @DatabaseField
    private String name;
    @DatabaseField(foreign = true)
    private PromotionEntity owner;

    private Collection<WorkerEntity> workers;

    public List<? extends Entity> childrenToInsert() {
        if (CollectionUtils.isEmpty(workers)) {
            return List.of();
        }
        return workers.stream().map(worker ->
                StableMemberEntity.builder()
                        .worker(worker)
                        .stable(this)
                        .build()
        ).collect(Collectors.toList());
    }

    public void selectChildren() {
        workers = stableWorkers.stream()
                .map(StableMemberEntity::getWorker)
                .collect(Collectors.toList());
    }

}
