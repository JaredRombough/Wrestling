package wrestling.model;

import wrestling.model.modelView.WorkerView;
import java.time.LocalDate;

public class TitleWorker {
    
    private final Title title;
    private final WorkerView worker;
    
    private final LocalDate dayWon;
    private LocalDate dayLost;
    
    public TitleWorker(Title title, WorkerView worker, LocalDate dayWon) {
        this.title = title;
        this.worker = worker;
        this.dayWon = dayWon;
    }

    /**
     * @return the title
     */
    public Title getTitle() {
        return title;
    }

    /**
     * @return the worker
     */
    public WorkerView getWorker() {
        return worker;
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
