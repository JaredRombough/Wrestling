package openwrestling.model.interfaces;

import java.time.LocalDate;
import openwrestling.model.modelView.PromotionView;
import openwrestling.model.modelView.StaffView;
import openwrestling.model.modelView.WorkerView;

/**
 *
 * @author jared
 */
public interface iContract {

    public iPerson getPerson();

    public LocalDate getEndDate();

    public LocalDate getStartDate();

    public PromotionView getPromotion();

    public boolean isExclusive();

    public int getMonthlyCost();

    public int getAppearanceCost();

    public void setActive(boolean active);

    public WorkerView getWorker();

    public StaffView getStaff();
    
    public LocalDate getLastShowDate();
    
    public void setEndDate(LocalDate date);

}
