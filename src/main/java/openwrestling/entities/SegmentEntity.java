package openwrestling.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.segmentEnum.AngleType;
import openwrestling.model.segmentEnum.JoinTeamType;
import openwrestling.model.segmentEnum.MatchFinish;
import openwrestling.model.segmentEnum.MatchRule;
import openwrestling.model.segmentEnum.PresenceType;
import openwrestling.model.segmentEnum.PromoType;
import openwrestling.model.segmentEnum.SegmentType;
import openwrestling.model.segmentEnum.ShowType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "segments")
public class SegmentEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long segmentID;

    @ForeignCollectionField(eager = true)
    private Collection<SegmentTeamEntity> teams;

    @ForeignCollectionField
    private Collection<MatchTitleEntity> titles;

    @DatabaseField(foreign = true)
    private TitleEntity matchTitle;

    @DatabaseField
    private SegmentType segmentType;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private EventEntity event;

    @DatabaseField(foreign = true)
    private StaffMemberEntity referee;

    @DatabaseField(foreign = true)
    private StableEntity newStable;

    @DatabaseField
    private int workRating;

    @DatabaseField
    private int crowdRating;

    @DatabaseField
    private MatchFinish matchFinish;

    @DatabaseField
    private MatchRule matchRule;

    @DatabaseField
    private int segmentLength;

    @DatabaseField
    private AngleType angleType;

    @DatabaseField
    private String challengeEventName;

    @DatabaseField
    private JoinTeamType joinTeamType;

    @DatabaseField
    private PresenceType presenceType;

    @DatabaseField
    private PromoType promoType;

    @DatabaseField
    private ShowType showType;

    @DatabaseField(foreign = true)
    private StableEntity joinStable;

    public List<? extends Entity> childrenToInsert() {
        if (CollectionUtils.isEmpty(teams)) {
            return List.of();
        }
        teams.forEach(team -> team.setSegmentEntity(this));
        return new ArrayList<>(teams);
    }
}
