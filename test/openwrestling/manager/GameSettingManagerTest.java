package openwrestling.manager;

import openwrestling.database.Database;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static openwrestling.model.constants.SettingKeys.GAME_DATE;
import static org.assertj.core.api.Assertions.assertThat;

public class GameSettingManagerTest {

    @Before
    public void setUp() {
        Database.createNewTempDatabase("testdb");
    }

    @Test
    public void setGameDate() {
        LocalDate date = LocalDate.now();
        LocalDate date2 = LocalDate.now().plusDays(3);
        assertThat(date).isNotEqualTo(date2);

        GameSettingManager.setGameDate(GAME_DATE, date);
        LocalDate savedDate = GameSettingManager.getGameSettingDate(GAME_DATE);
        assertThat(savedDate).isEqualTo(date);

        GameSettingManager.setGameDate(GAME_DATE, date2);
        LocalDate savedDate2 = GameSettingManager.getGameSettingDate(GAME_DATE);
        assertThat(savedDate2).isEqualTo(date2);
    }

}