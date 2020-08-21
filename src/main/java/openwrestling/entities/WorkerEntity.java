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
import openwrestling.model.segment.constants.Gender;

import java.util.Collection;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "workers")
public class WorkerEntity extends Entity {

    @ForeignCollectionField(eager = true)
    public ForeignCollection<ContractEntity> contractEntities;
    @ForeignCollectionField(eager = true)
    public Collection<StableWorkerEntity> workerGroups;
    @DatabaseField(generatedId = true)
    private long workerID;
    @DatabaseField
    private long importKey;
    @DatabaseField
    private String name;
    @DatabaseField
    private String shortName;
    @DatabaseField
    private String imageString;
    @DatabaseField
    private int striking;
    @DatabaseField
    private int flying;
    @DatabaseField
    private int wrestling;
    @DatabaseField
    private int charisma;
    @DatabaseField
    private int behaviour;
    @DatabaseField
    private int popularity;
    @DatabaseField
    private int age;
    @DatabaseField
    private Gender gender;
    @DatabaseField
    private boolean fullTime;
    @DatabaseField
    private boolean mainRoster;
//
//    public List<ContractEntity> contracts;

//    @DatabaseField(foreign = true)
//    private Injury injury;
    @DatabaseField
    private int minimumPopularity;

//    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
//    private Entourage entourage;

//    @ForeignCollectionField
//    private Collection<Worker> entourage;
    @DatabaseField(foreign = true)
    private WorkerEntity manager;
    @ForeignCollectionField
    private Collection<SegmentTeamWorkerEntity> segmentTeams;

    @ForeignCollectionField
    private Collection<SegmentTeamEntourageEntity> entourageTeams;

}
