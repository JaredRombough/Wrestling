package openwrestling.model;

import java.io.Serializable;
import java.time.LocalDate;
import openwrestling.model.interfaces.iContract;
import openwrestling.model.interfaces.iPerson;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.modelView.StaffView;
import openwrestling.model.gameObjects.Worker;

public class StaffContract implements Serializable, iContract {

    private final StaffView staff;
    private final Promotion promotion;
    private final LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastShowDate;
    private boolean active;
    private int biWeeklyCost;

    public StaffContract(LocalDate startDate, StaffView staff, Promotion promotion) {
        active = true;
        this.startDate = startDate;
        this.staff = staff;
        this.promotion = promotion;
    }

    /**
     * @return the staff
     */
    @Override
    public StaffView getStaff() {
        return staff;
    }

    /**
     * @return the promotion
     */
    @Override
    public Promotion getPromotion() {
        return promotion;
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
    public Worker getWorker() {
        return null;
    }

    /**
     * @return the endDate
     */
    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public iPerson getPerson() {
        return staff;
    }

    /**
     * @return the lastShowDate
     */
    @Override
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
    public void setEndDate(LocalDate date) {
        this.endDate = date;
    }

}
