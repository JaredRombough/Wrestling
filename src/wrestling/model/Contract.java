package wrestling.model;

import java.io.Serializable;
import java.time.LocalDate;
import wrestling.model.interfaces.iContract;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.WorkerView;

/**
 *
 * A contract is a link between a worker and a promotion that also knows the
 * conditions (pay for appearance, length, etc)
 *
 *
 */
public class Contract implements Serializable, iContract {

    private PromotionView promotion;
    private WorkerView worker;

    private boolean active;

    //total number of days/appearances left
    private int duration;

    private LocalDate startDate;

    private boolean exclusive;

    private boolean pushed;

    private int appearanceCost;

    private int biWeeklyCost;

    //default constructor is empty, values must be set by contractFactory
    public Contract() {
        active = true;
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
     * @return the worker
     */
    @Override
    public WorkerView getWorker() {
        return worker;
    }

    /**
     * @param worker the worker to set
     */
    public void setWorker(WorkerView worker) {
        this.worker = worker;
    }

    /**
     * @return the length
     */
    @Override
    public int getDuration() {
        return duration;
    }

    /**
     * @param duration the length to set
     */
    @Override
    public void setDuration(int duration) {

        this.duration = duration;
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
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public LocalDate getEndDate() {
        return startDate.plusDays(duration);
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
}
