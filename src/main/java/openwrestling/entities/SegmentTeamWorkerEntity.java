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
@DatabaseTable(tableName = "segment_team_workers")
public class SegmentTeamWorkerEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long segmentTeamWorkerID;

    @DatabaseField(foreign = true)
    private WorkerEntity workerEntity;

    @DatabaseField(foreign = true)
    private SegmentTeamEntity segmentTeamEntity;
}
