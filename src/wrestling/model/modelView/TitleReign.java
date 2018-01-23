package wrestling.model.modelView;

import java.time.LocalDate;
import java.util.List;
import wrestling.model.Worker;

public class TitleReign {

    private List<Worker> workers;
    private LocalDate dayWon;
    private LocalDate dayLost;

    public TitleReign(List<Worker> workers, LocalDate dayWon) {
        this.workers = workers;
        this.dayWon = dayWon;
    }

    /**
     * @return the workers
     */
    public List<Worker> getWorkers() {
        return workers;
    }

    /**
     * @return the dayWon
     */
    public LocalDate getDayWon() {
        return dayWon;
    }

    /**
     * @return the dayLost
     */
    public LocalDate getDayLost() {
        return dayLost;
    }

    /**
     * @param dayLost the dayLost to set
     */
    public void setDayLost(LocalDate dayLost) {
        this.dayLost = dayLost;
    }

}
