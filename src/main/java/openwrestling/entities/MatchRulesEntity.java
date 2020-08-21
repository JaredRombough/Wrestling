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
@DatabaseTable(tableName = "match_rules")
public class MatchRulesEntity extends Entity {

    @DatabaseField(generatedId = true)
    private long matchRulesID;

    @DatabaseField
    private boolean noDQ;

    @DatabaseField
    private String description;

    @DatabaseField
    private int strikingModifier;

    @DatabaseField
    private int flyingModifier;

    @DatabaseField
    private int wrestingModifier;

    @DatabaseField
    private int injuryModifier;

}
