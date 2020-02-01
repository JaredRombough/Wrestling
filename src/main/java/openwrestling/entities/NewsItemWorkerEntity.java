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
@DatabaseTable(tableName = "news_item_workers")
public class NewsItemWorkerEntity extends Entity {
    @DatabaseField(generatedId = true)
    private long newsItemWorkerID;

    @DatabaseField(foreign = true)
    private WorkerEntity workerEntity;

    @DatabaseField(foreign = true)
    private NewsItemEntity newsItemEntity;
}
