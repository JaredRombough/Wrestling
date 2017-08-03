package wrestling.model.dirt;

import java.time.LocalDate;
import java.util.List;
import wrestling.model.Promotion;
import wrestling.model.Worker;

public class SegmentRecord implements Dirt {

    private LocalDate date;
    private final String string;
    private final List<Worker> workers;
    private final Promotion promotion;
    private final EventArchive eventArchive;

    public SegmentRecord(String string, List<Worker> workers, Promotion promotion, EventArchive eventArchive) {
        this.string = string;
        this.workers = workers;
        this.promotion = promotion;
        this.eventArchive = eventArchive;
    }

    @Override
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public List<Worker> getWorkers() {
        return workers;
    }

    /**
     * @return the eventArchive
     */
    public EventArchive getEventArchive() {
        return eventArchive;
    }

    @Override
    public Promotion getPromotion() {
        return promotion;
    }

}
