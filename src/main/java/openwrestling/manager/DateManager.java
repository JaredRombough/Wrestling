package openwrestling.manager;


import openwrestling.database.Database;
import openwrestling.model.gameObjects.gamesettings.GameSetting;
import openwrestling.model.utility.ModelUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static openwrestling.model.constants.GameConstants.DEFAULT_START_DATE;
import static openwrestling.model.constants.SettingKeys.GAME_DATE;

public class DateManager extends GameObjectManager implements Serializable {

    private LocalDate gameDate;

    public DateManager(Database database) {
        super(database);
        selectData();
        if (gameDate == null) {
            setGameDate(DEFAULT_START_DATE);
        }
    }

    @Override
    public void selectData() {
        List<GameSetting> gameSettings = getDatabase().selectAll(GameSetting.class);
        gameDate = gameSettings.stream()
                .filter(gameSetting -> gameSetting.getKey().equals(GAME_DATE))
                .map(gameSetting -> LocalDate.parse(gameSetting.getValue(), DateTimeFormatter.ISO_DATE))
                .findFirst()
                .orElse(null);
    }

    public String todayString() {
        return ModelUtils.dateString(gameDate);
    }

    public void nextDay() {
        gameDate = LocalDate.from(gameDate).plusDays(1);
        setGameDate(gameDate);
    }

    public void setGameDate(LocalDate gameDate) {
        this.gameDate = gameDate;
        List<GameSetting> gameSettings = getDatabase().selectAll(GameSetting.class);
        GameSetting dateSetting = gameSettings.stream()
                .filter(gameSetting -> gameSetting.getKey().equals(GAME_DATE))
                .findFirst()
                .orElse(null);
        if (dateSetting == null) {
            dateSetting = GameSetting.builder()
                    .key(GAME_DATE)
                    .value(gameDate.format(DateTimeFormatter.ISO_DATE))
                    .build();
        } else {
            dateSetting.setValue(gameDate.format(DateTimeFormatter.ISO_DATE));
        }
        getDatabase().insertList(List.of(dateSetting));
    }

    /**
     * @return the gameDate
     */
    public LocalDate today() {
        return gameDate;
    }

    public boolean isPayDay() {
        return gameDate.getDayOfMonth() == 1;
    }

}
