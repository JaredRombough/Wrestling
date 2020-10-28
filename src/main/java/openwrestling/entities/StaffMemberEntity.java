package openwrestling.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.segment.constants.Gender;
import openwrestling.model.segment.constants.StaffType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "staff_members")
public class StaffMemberEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long staffMemberID;

    @DatabaseField
    private long importKey;

    @DatabaseField
    private String name;

    @DatabaseField
    private Gender gender;

    @DatabaseField
    private int age;

    @DatabaseField
    private int skill;

    @DatabaseField
    private int behaviour;

    @DatabaseField
    private StaffType staffType;

    @DatabaseField
    private String imageFileName;

    @DatabaseField(foreign = true)
    private StaffContractEntity staffContract;


}
