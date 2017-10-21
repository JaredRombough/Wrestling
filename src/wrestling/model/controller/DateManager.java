package wrestling.model.controller;

import java.time.LocalDate;

public class DateManager {
    
    private LocalDate gameDate;
    
    public DateManager(LocalDate startDate) {
        gameDate = startDate;
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

}
