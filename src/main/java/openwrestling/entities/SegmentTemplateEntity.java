package openwrestling.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "segment_templates")
public class SegmentTemplateEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long segmentTemplateID;

    @ForeignCollectionField(eager = true)
    private Collection<SegmentTeamEntity> segmentTeams;

    @DatabaseField(foreign = true)
    private TitleEntity title;

    @DatabaseField(foreign = true)
    private EventTemplateEntity eventTemplate;

    @DatabaseField
    private Date sourceEventDate;

    @DatabaseField
    private String sourceEventName;

    public List<? extends Entity> childrenToInsert() {
        if (CollectionUtils.isEmpty(segmentTeams)) {
            return List.of();
        }
        segmentTeams.forEach(segmentTeamEntity -> segmentTeamEntity.setSegmentTemplateEntity(this));
        return new ArrayList<>(segmentTeams);
    }
}
