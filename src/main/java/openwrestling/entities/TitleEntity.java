package openwrestling.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.segment.constants.ActiveType;

import java.util.Collection;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "titles")
public class TitleEntity extends Entity {
    @DatabaseField(generatedId = true)
    private long titleID;

    @ForeignCollectionField
    private Collection<TitleReignEntity> titleReigns;

    @DatabaseField(foreign = true)
    private RosterSplitEntity rosterSplit;

    @DatabaseField(foreign = true)
    private PromotionEntity promotion;

    @DatabaseField
    private int teamSize;

    @DatabaseField
    private String name;

    @DatabaseField
    private ActiveType activeType;

    @DatabaseField
    private int prestige;

    @ForeignCollectionField
    private Collection<SegmentEntity> matches;

    @ForeignCollectionField
    private Collection<SegmentTemplateEntity> segmentTemplateEntities;
}
