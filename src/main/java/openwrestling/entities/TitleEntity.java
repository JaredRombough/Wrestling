package openwrestling.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.segmentEnum.ActiveType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "titles")
public class TitleEntity extends Entity {
    @DatabaseField(generatedId = true)
    private long titleID;

    @DatabaseField(foreign = true)
    private TitleReignEntity championTitleReign;

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
}
