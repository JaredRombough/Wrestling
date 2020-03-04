package openwrestling.manager;

import openwrestling.database.Database;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static openwrestling.TestUtils.TEST_DB_PATH;
import static org.assertj.core.api.Assertions.assertThat;

public class DateManagerTest {

    private Database database;
    private DateManager dateManager;

    @Before
    public void setUp() {
        database = new Database(TEST_DB_PATH);
        dateManager = new DateManager(database);
    }

    @Test
    public void setGameDate() {
        LocalDate date = LocalDate.now();
        LocalDate date2 = LocalDate.now().plusDays(3);
        assertThat(date).isNotEqualTo(date2);

        dateManager.setGameDate(date);
        LocalDate savedDate = dateManager.today();
        assertThat(savedDate).isEqualTo(date);

        dateManager.setGameDate(date2);
        LocalDate savedDate2 = dateManager.today();
        assertThat(savedDate2).isEqualTo(date2);
    }
}