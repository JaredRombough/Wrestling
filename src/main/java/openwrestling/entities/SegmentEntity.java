package openwrestling.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.segment.constants.AngleType;
import openwrestling.model.segment.constants.JoinTeamType;
import openwrestling.model.segment.constants.MatchFinish;
import openwrestling.model.segment.constants.PresenceType;
import openwrestling.model.segment.constants.PromoType;
import openwrestling.model.segment.constants.SegmentType;
import openwrestling.model.segment.constants.ShowType;

import java.util.Collection;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "segments")
public class SegmentEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long segmentID;

    @ForeignCollectionField
    private Collection<MatchTitleEntity> titles;

    @DatabaseField(foreign = true)
    private TitleEntity matchTitle;

    @DatabaseField
    private SegmentType segmentType;

    @DatabaseField(foreign = true)
    private EventEntity event;

    @DatabaseField
    private Date date;

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

    @DatabaseField(foreign = true)
    private MatchRulesEntity matchRules;

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
}
