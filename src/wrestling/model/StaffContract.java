package wrestling.model;

import java.io.Serializable;
import java.time.LocalDate;
import wrestling.model.interfaces.iContract;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.WorkerView;

public class StaffContract implements Serializable, iContract {

    private StaffView staff;
    private PromotionView promotion;
    private LocalDate startDate;
    private boolean active;
    private int duration;
    private int biWeeklyCost;

    public StaffContract() {
        active = true;
    }

    /**
     * @return the staff
     */
    @Override
    public StaffView getStaff() {
        return staff;
    }

    /**
     * @param staff the staff to set
     */
    public void setStaff(StaffView staff) {
        this.staff = staff;
    }

    /**
     * @return the promotion
     */
    @Override
    public PromotionView getPromotion() {
        return promotion;
    }

    /**
     * @param promotion the promotion to set
     */
    public void setPromotion(PromotionView promotion) {
        this.promotion = promotion;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * @return the startDate
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    @Override
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the biWeeklyCost
     */
    @Override
    public int getBiWeeklyCost() {
        return biWeeklyCost;
    }

    /**
     * @param biWeeklyCost the biWeeklyCost to set
     */
    public void setBiWeeklyCost(int biWeeklyCost) {
        this.biWeeklyCost = biWeeklyCost;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public boolean isExclusive() {
        return true;
    }

    @Override
    public int getAppearanceCost() {
        return 0;
    }

    @Override
    public WorkerView getWorker() {
        return null;
    }

}
