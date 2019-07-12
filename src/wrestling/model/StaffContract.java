package wrestling.model;

import java.io.Serializable;
import java.time.LocalDate;
import wrestling.model.interfaces.iContract;
import wrestling.model.interfaces.iPerson;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.WorkerView;

public class StaffContract implements Serializable, iContract {

    private StaffView staff;
    private PromotionView promotion;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastShowDate;
    private boolean active;
    private int biWeeklyCost;
    private int morale;

    public StaffContract() {
        active = true;
        morale = 100;
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

    /**
     * @return the startDate
     */
    @Override
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
    public int getMonthlyCost() {
        return biWeeklyCost;
    }

    /**
     * @param biWeeklyCost the biWeeklyCost to set
     */
    public void setMonthlyCost(int biWeeklyCost) {
        this.biWeeklyCost = biWeeklyCost;
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

    /**
     * @return the endDate
     */
    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    @Override
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public iPerson getPerson() {
        return staff;
    }

    @Override
    public int getMorale() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the lastShowDate
     */
    public LocalDate getLastShowDate() {
        return lastShowDate;
    }

    /**
     * @param lastShowDate the lastShowDate to set
     */
    public void setLastShowDate(LocalDate lastShowDate) {
        this.lastShowDate = lastShowDate;
    }

    @Override
    public void setMorale(int morale) {
        this.morale = morale;
    }

}
