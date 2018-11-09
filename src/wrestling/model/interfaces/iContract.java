package wrestling.model.interfaces;

import java.time.LocalDate;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.WorkerView;

/**
 *
 * @author jared
 */
public interface iContract {

    public void setEndDate(LocalDate date);

    public LocalDate getEndDate();

    public void setStartDate(LocalDate date);

    public LocalDate getStartDate();

    public PromotionView getPromotion();

    public boolean isExclusive();

    public int getMonthlyCost();

    public int getAppearanceCost();

    public void setActive(boolean active);

    public WorkerView getWorker();

    public StaffView getStaff();

}
