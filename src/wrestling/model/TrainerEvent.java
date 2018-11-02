package wrestling.model;

import java.time.LocalDate;
import wrestling.model.interfaces.iNewsItem;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.WorkerView;

public class TrainerEvent implements iNewsItem {

    private final WorkerView worker;
    private final StaffView trainer;
    private final LocalDate date;
    private final String stat;
    private final PromotionView promotion;

    public TrainerEvent(WorkerView worker, StaffView trainer, LocalDate date, String stat,
            PromotionView promotion) {
        this.worker = worker;
        this.trainer = trainer;
        this.date = date;
        this.stat = stat;
        this.promotion = promotion;
    }

    @Override
    public String toString() {
        return String.format("%s training", worker.getLongName());
    }

    @Override
    public String getSummary() {
        return String.format("%s increased %s while training with %s.",
                worker.toString(), stat, trainer.toString());
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public PromotionView getPromotion() {
        return promotion;
    }

}
