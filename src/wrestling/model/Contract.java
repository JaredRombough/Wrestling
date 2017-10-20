package wrestling.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.controller.ContractManager;

/**
 *
 * A contract is a link between a worker and a promotion that also knows the
 * conditions (pay for appearance, length, etc)
 *
 *
 */
public class Contract implements Serializable {

    private Promotion promotion;
    private Worker worker;
    
    private boolean active;

    //total number of days/appearances left
    private int duration;

    private LocalDate startDate;

    private boolean exclusive;

    private int appearanceCost;

    private int biWeeklyCost;
    private List<LocalDate> bookedDates = new ArrayList<>();

    //default constructor is empty, values must be set by contractFactory
    public Contract() {
        active = true;
    }

    public void bookDate(LocalDate date) {
        getBookedDates().add(date);
    }

    /**
     * @return the promotion
     */
    public Promotion getPromotion() {
        return promotion;
    }

    /**
     * @param promotion the promotion to set
     */
    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    /**
     * @return the worker
     */
    public Worker getWorker() {
        return worker;
    }

    /**
     * @param worker the worker to set
     */
    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    /**
     * @return the length
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @param duration the length to set
     */
    public void setDuration(int duration) {

        this.duration = duration;
    }

    /**
     * @return the exclusive
     */
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

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the bookedDates
     */
    public List<LocalDate> getBookedDates() {
        return bookedDates;
    }

    /**
     * @return the biWeeklyCost
     */
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
    public void setActive(boolean active) {
        this.active = active;
    }
}
