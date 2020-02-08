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
@DatabaseTable(tableName = "game_settings")
public class GameSettingEntity extends Entity {
    @DatabaseField(generatedId = true)
    private long gameSettingID;

    @DatabaseField
    private String key;

    @DatabaseField
    private String value;

}
