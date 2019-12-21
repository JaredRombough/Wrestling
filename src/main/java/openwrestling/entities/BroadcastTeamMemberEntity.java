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
@DatabaseTable(tableName = "broadcast_teams")
public class BroadcastTeamMemberEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long broadcastTeamID;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private WorkerEntity worker;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private StaffMemberEntity staffMember;

    @DatabaseField(foreign = true)
    private EventTemplateEntity eventTemplate;

    @DatabaseField(foreign = true)
    private PromotionEntity promotion;
}
