package openwrestling.model.gameObjects.gamesettings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.gameObjects.GameObject;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameSetting extends GameObject {
    private long gameSettingID;
    private String key;
    private String value;

    @Override
    public boolean equals(Object object) {
        return object instanceof GameSetting &&
                Objects.equals(((GameSetting) object).getKey(), key);
    }
}
