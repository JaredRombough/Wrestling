package openwrestling.model.modelView;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class TitleReign implements Serializable{

    private final List<WorkerView> workers;
    private final LocalDate dayWon;
    private LocalDate dayLost;
    private final int sequenceNumber;

    public TitleReign(List<WorkerView> workers, LocalDate dayWon, int sequenceNumber) {
        this.workers = workers;
        this.dayWon = dayWon;
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * @return the workers
     */
    public List<WorkerView> getWorkers() {
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
