package wrestling.model;

import java.time.LocalDate;
import wrestling.model.interfaces.iNewsItem;
import wrestling.model.modelView.SegmentView;
import wrestling.model.modelView.WorkerView;

public class Injury implements iNewsItem {

    private final LocalDate expiryDate;
    private final LocalDate startDate;
    private final WorkerView workerView;
    private final SegmentView segmentView;

    public Injury(LocalDate starDate, LocalDate expiryDate, WorkerView workerView, SegmentView segmentView) {
        this.startDate = starDate;
        this.expiryDate = expiryDate;
        this.workerView = workerView;
        this.segmentView = segmentView;
    }

    /**
     * @return the expiryDate
     */
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    /**
     * @return the worker
     */
    public WorkerView getWorkerView() {
        return workerView;
    }

    /**
     * @return the startDate
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public String toString() {
        return String.format("%s injured", workerView.getLongName());
    }

    /**
     * @return the segmentView
     */
    public SegmentView getSegmentView() {
        return segmentView;
    }

    @Override
    public String getSummary() {
        return String.format("%s was injured in a match at %s on %s. They are expected to be out until %s.",
                workerView.getLongName(),
                segmentView.getEventView().toString(),
                startDate.toString(),
                expiryDate.toString());
    }
}
