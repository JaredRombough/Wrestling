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

    public void setDuration(int duration);

    public void setStartDate(LocalDate date);

    public PromotionView getPromotion();

    public int getDuration();

    public boolean isExclusive();

    public int getBiWeeklyCost();

    public int getAppearanceCost();

    public void setActive(boolean active);

    public WorkerView getWorker();

    public StaffView getStaff();

}
