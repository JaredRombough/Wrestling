package openwrestling.model;

import java.time.LocalDate;
import openwrestling.model.modelView.PromotionView;
import openwrestling.model.modelView.SegmentView;
import openwrestling.model.modelView.WorkerView;

public class Injury {

    private final LocalDate expiryDate;
    private final LocalDate startDate;
    private final WorkerView workerView;
    private final PromotionView promotion;

    public Injury(LocalDate starDate, LocalDate expiryDate, WorkerView workerView, PromotionView promotion) {
        this.startDate = starDate;
        this.expiryDate = expiryDate;
        this.workerView = workerView;
        this.promotion = promotion;
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

    public LocalDate getDate() {
        return startDate;
    }

    public PromotionView getPromotion() {
        return promotion;
    }
}