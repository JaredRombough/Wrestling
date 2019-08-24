package openwrestling.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.modelView.Entourage;
import openwrestling.model.segmentEnum.Gender;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "workers")
public class WorkerEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long workerID;

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

    @DatabaseField
    private int minimumPopularity;

//    @ForeignCollectionField
//    private Collection<Contract> contracts;

//    @DatabaseField(foreign = true)
//    private Injury injury;

    @DatabaseField(foreign = true)
    private WorkerEntity manager;

//    @DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
//    private Entourage entourage;

//    @ForeignCollectionField
//    private Collection<Worker> entourage;

}
