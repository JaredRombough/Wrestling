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
@DatabaseTable(tableName = "match_titles")
public class MatchTitleEntity extends Entity {
    @DatabaseField(generatedId = true)
    long matchTitleID;

    @DatabaseField(foreign = true)
    TitleEntity titleEntity;

    @DatabaseField(foreign = true)
    SegmentEntity segmentEntity;
}
