package openwrestling.manager;

import openwrestling.model.utility.ModelUtils;

import java.io.Serializable;
import java.time.LocalDate;

import static openwrestling.model.constants.SettingKeys.GAME_DATE;

public class DateManager extends GameObjectManager implements Serializable {

    private LocalDate gameDate;

    public void setGameDate(LocalDate gameDate) {
        this.gameDate = gameDate;
        GameSettingManager.setGameDate(GAME_DATE, gameDate);
    }

    @Override
    public void selectData() {
        gameDate = GameSettingManager.getGameSettingDate(GAME_DATE);
    }

    public String todayString() {
        return ModelUtils.dateString(gameDate);
    }

    public void nextDay() {
        //advance the day by one
        gameDate = LocalDate.from(gameDate).plusDays(1);
        GameSettingManager.setGameDate(GAME_DATE, gameDate);
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
