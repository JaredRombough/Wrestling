package wrestling.model.modelView;

import java.time.LocalDate;
import java.util.List;
import wrestling.model.Worker;

public class TitleReign {

    private final List<Worker> workers;
    private final LocalDate dayWon;
    private LocalDate dayLost;
    private final int sequenceNumber;

    public TitleReign(List<Worker> workers, LocalDate dayWon, int sequenceNumber) {
        this.workers = workers;
        this.dayWon = dayWon;
        this.sequenceNumber = sequenceNumber;
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
    public LocalDate getDateLost() {
        return dayLost;
    }

    public String getDayLostString() {
        return dayLost == null ? "Today" : dayLost.toString();
    }

    /**
     * @param dayLost the dayLost to set
     */
    public void setDayLost(LocalDate dayLost) {
        this.dayLost = dayLost;
    }

    /**
     * @return the sequenceNumber
     */
    public int getSequenceNumber() {
        return sequenceNumber;
    }

}
