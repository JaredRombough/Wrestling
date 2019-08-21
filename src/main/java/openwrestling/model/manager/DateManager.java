package openwrestling.model.manager;

import java.io.Serializable;
import java.time.LocalDate;
import openwrestling.model.utility.ModelUtils;

public class DateManager implements Serializable {

    private LocalDate gameDate;

    public DateManager(LocalDate startDate) {
        gameDate = startDate;
    }

    public String todayString() {
        return ModelUtils.dateString(gameDate);
    }

    public void nextDay() {
        //advance the day by one
        gameDate = LocalDate.from(gameDate).plusDays(1);
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
