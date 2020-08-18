package openwrestling.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.segmentEnum.OutcomeType;
import openwrestling.model.segmentEnum.PresenceType;
import openwrestling.model.segmentEnum.ResponseType;
import openwrestling.model.segmentEnum.SuccessType;
import openwrestling.model.segmentEnum.TeamType;
import openwrestling.model.segmentEnum.TimingType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "segment_teams")
public class SegmentTeamEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long segmentTeamID;

    @DatabaseField(foreign = true)
    private SegmentEntity segment;

    @DatabaseField(foreign = true)
    private SegmentTemplateEntity segmentTemplateEntity;

    private Collection<WorkerEntity> workers;

    @ForeignCollectionField
    private Collection<SegmentTeamWorkerEntity> segmentTeamWorkers;

    @ForeignCollectionField
    private Collection<SegmentTeamEntourageEntity> segmentTeamEntourage;

    @DatabaseField
    private TeamType type;

    @DatabaseField(foreign = true)
    private SegmentTeamEntity target;

    @DatabaseField
    private SuccessType success;

    @DatabaseField
    private TimingType timing;

    @DatabaseField
    private OutcomeType outcome;

    @DatabaseField
    private PresenceType presence;

    @DatabaseField
    private ResponseType response;

    public List<? extends Entity> childrenToInsert() {
        if (CollectionUtils.isEmpty(workers)) {
            return List.of();
        }
        return workers.stream().map(worker ->
                SegmentTeamWorkerEntity.builder()
                        .workerEntity(worker)
                        .segmentTeamEntity(this)
                        .build()
        ).collect(Collectors.toList());
    }

    public void selectChildren() {
        workers = segmentTeamWorkers.stream()
                .map(SegmentTeamWorkerEntity::getWorkerEntity)
                .collect(Collectors.toList());
    }

}
