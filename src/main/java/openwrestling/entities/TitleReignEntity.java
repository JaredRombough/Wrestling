package openwrestling.entities;

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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "title_reigns")
public class TitleReignEntity extends Entity {
    @DatabaseField(generatedId = true)
    private long titleReignID;

    @ForeignCollectionField(eager = true)
    public Collection<TitleReignWorkerEntity> titleReignWorkers;

    private Collection<WorkerEntity> workers;

    @DatabaseField
    private Date dayWon;

    @DatabaseField
    private Date dayLost;

    @DatabaseField
    private long sequenceNumber;

    @DatabaseField(foreign = true)
    private TitleEntity title;

    public List<? extends Entity> childrenToInsert() {
        if (CollectionUtils.isEmpty(workers)) {
            return List.of();
        }
        return workers.stream().map(worker ->
                TitleReignWorkerEntity.builder()
                        .workerEntity(worker)
                        .titleReignEntity(this)
                        .build()
        ).collect(Collectors.toList());
    }

    public void selectChildren() {
        workers = titleReignWorkers.stream()
                .map(titleReignWorker -> titleReignWorker.getWorkerEntity())
                .collect(Collectors.toList());
    }
}
