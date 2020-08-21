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
import openwrestling.model.segment.constants.ActiveType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "tag_teams")
public class TagTeamEntity extends Entity {

    @ForeignCollectionField(eager = true)
    public ForeignCollection<TagTeamWorkerEntity> tagTeamWorkers;
    @DatabaseField(generatedId = true)
    private long tagTeamID;
    private Collection<WorkerEntity> workers;

    @DatabaseField
    private String name;

    @DatabaseField
    private ActiveType activeType;

    @DatabaseField
    private int experience;

    public List<? extends Entity> childrenToInsert() {
        if (CollectionUtils.isEmpty(workers)) {
            return List.of();
        }
        return workers.stream().map(worker ->
                TagTeamWorkerEntity.builder()
                        .workerEntity(worker)
                        .tagTeamEntity(this)
                        .build()
        ).collect(Collectors.toList());
    }

    public void selectChildren() {
        workers = tagTeamWorkers.stream()
                .map(TagTeamWorkerEntity::getWorkerEntity)
                .collect(Collectors.toList());
    }
}
