package openwrestling.model;

import java.io.Serializable;
import java.time.LocalDate;
import openwrestling.model.interfaces.iContract;
import openwrestling.model.interfaces.iPerson;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.modelView.StaffView;
import openwrestling.model.gameObjects.Worker;

/**
 *
 * A contract is a link between a worker and a promotion that also knows the
 * conditions (pay for appearance, length, etc)
 *
 *
 */
public class Contract implements Serializable, iContract {

    private final Promotion promotion;
    private final Worker worker;

    private boolean active;

    private final LocalDate startDate;

    private LocalDate endDate;

    private LocalDate lastShowDate;

    private boolean exclusive;

    private boolean pushed;

    private int appearanceCost;

    private int monthlyCost;

    public Contract(LocalDate startDate, Worker worker, Promotion promotion) {
        active = true;
        lastShowDate = startDate;
        this.startDate = startDate;
        this.worker = worker;
        this.promotion = promotion;
    }

    /**
     * @return the promotion
     */
    @Override
    public Promotion getPromotion() {
        return promotion;
    }

    /**
     * @return the worker
     */
    @Override
    public Worker getWorker() {
        return worker;
    }

    /**
     * @return the exclusive
     */
    @Override
    public boolean isExclusive() {
        return exclusive;
    }

    /**
     * @param exclusive the exclusive to set
     */
    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    /**
     * @return the appearanceCost
     */
    @Override
    public int getAppearanceCost() {
        return appearanceCost;
    }

    /**
     * @param unitCost the appearanceCost to set
     */
    public void setAppearanceCost(int unitCost) {
        this.appearanceCost = unitCost;
    }

    /**
     * @return the startDate
     */
    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * @return the biWeeklyCost
     */
    @Override
    public int getMonthlyCost() {
        return monthlyCost;
    }

    /**
     * @param monthlyCost the monthlyCost to set
     */
    public void setMonthlyCost(int monthlyCost) {
        this.monthlyCost = monthlyCost;
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
     * @return the pushed
     */
    public boolean isPushed() {
        return pushed;
    }

    /**
     * @param pushed the pushed to set
     */
    public void setPushed(boolean pushed) {
        this.pushed = pushed;
    }

    @Override
    public StaffView getStaff() {
        return null;
    }

    @Override
    public iPerson getPerson() {
        return worker;
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
