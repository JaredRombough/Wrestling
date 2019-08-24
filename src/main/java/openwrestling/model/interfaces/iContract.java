package openwrestling.model.interfaces;

import java.time.LocalDate;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.modelView.StaffView;
import openwrestling.model.gameObjects.Worker;

/**
 *
 * @author jared
 */
public interface iContract {

    public iPerson getPerson();

    public LocalDate getEndDate();

    public LocalDate getStartDate();

    public Promotion getPromotion();

    public boolean isExclusive();

    public int getMonthlyCost();

    public int getAppearanceCost();

    public void setActive(boolean active);

    public Worker getWorker();

    public StaffView getStaff();
    
    public LocalDate getLastShowDate();
    
    public void setEndDate(LocalDate date);

}
