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
@DatabaseTable(tableName = "title_reign_workers")
public class TitleReignWorkerEntity extends Entity {
    @DatabaseField(generatedId = true)
    long titleReignWorkerID;

    @DatabaseField(foreign = true)
    TitleReignEntity titleReignEntity;

    @DatabaseField(foreign = true)
    WorkerEntity workerEntity;
}
