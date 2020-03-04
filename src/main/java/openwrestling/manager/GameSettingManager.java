package openwrestling.manager;


import openwrestling.database.Database;
import openwrestling.model.gameObjects.gamesettings.GameSetting;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GameSettingManager extends GameObjectManager {

    public GameSettingManager(Database database) {
        super(database);
    }

    public void setGameSettingLong(String key, long value) {
        GameSetting gameSetting = GameSetting.builder()
                .key(key)
                .value(value + "")
                .build();
        getDatabase().insertList(List.of(gameSetting));
    }

    public void setGameDate(String key, LocalDate value) {
        List<GameSetting> gameSettings = getDatabase().selectAll(GameSetting.class);
        GameSetting dateSetting = gameSettings.stream()
                .filter(gameSetting -> gameSetting.getKey().equals(key))
                .findFirst()
                .orElse(null);
        if (dateSetting == null) {
            dateSetting = GameSetting.builder()
                    .key(key)
                    .value(value.format(DateTimeFormatter.ISO_DATE))
                    .build();
        } else {
            dateSetting.setValue(value.format(DateTimeFormatter.ISO_DATE));
        }
        getDatabase().insertList(List.of(dateSetting));
    }

    public long getGameSettingLong(String key) {
        List<GameSetting> gameSettings = getDatabase().selectAll(GameSetting.class);
        return gameSettings.stream()
                .filter(gameSetting -> gameSetting.getKey().equals(key))
                .map(gameSetting -> Long.parseLong(gameSetting.getValue()))
                .findFirst()
                .orElseThrow();
    }


}
