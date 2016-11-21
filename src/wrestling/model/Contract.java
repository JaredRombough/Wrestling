package wrestling.model;

import java.io.Serializable;

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

    //total number of days/appearances left
    private int duration;

    private int startDate;

    private boolean monthly;

    private boolean exclusive;

    private int unitCost;
    
    

    public Contract(Worker worker, Promotion promotion, boolean monthly, boolean exclusive, int duration, int cost) {
        this.worker = worker;
        this.promotion = promotion;
        this.monthly = monthly;
        this.exclusive = exclusive;
        this.duration = duration;
        this.unitCost = cost;

    }

    public Contract(Worker worker, Promotion promotion, boolean monthly, boolean exclusive, int duration) {
        this.worker = worker;
        this.promotion = promotion;
        this.monthly = monthly;
        this.exclusive = exclusive;
        this.duration = duration;
        calculateCost();

    }
    
    //basic default contract
    public Contract(Worker worker, Promotion promotion) {
        this.worker = worker;
        this.promotion = promotion;
        this.monthly = true;
        this.exclusive = false;
        this.duration = 30;
        
        
        for(int i = 0; i < promotion.getLevel(); i++) {
            duration += 30;
            
            if (i == 4) {
                exclusive = true;
            }
        }
        
        calculateCost();
        
    }

    private void calculateCost() {

        unitCost = 0;
        
        
        for (int i = 0; i < worker.getPopularity(); i++) {
            if (i < 50) {
                unitCost += 5;
            } else {
                unitCost += 10;
            }
        }


        if (exclusive) {
            unitCost *= 1.5;
        }


    }

    //depreciates monthly contracts
    public void nextDay() {
        if (monthly) {
            duration--;
        }

        if (duration <= 0) {
            terminateContract();
        }
    }

    //depreciates appearance contracts
    public void appearance() {
        if (!monthly) {
            duration--;
        }

        if (duration <= 0) {
            terminateContract();
        }
    }

    private void terminateContract() {
        
        this.worker.removeContract(this);
        this.promotion.removeContract(this);
    }

    public String getTerms() {
        String string = getPromotion() + " Length: " + getDuration()
                + " Appearance Cost: $" + getUnitCost();

        return string;
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
     * @return the startDate
     */
    public int getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the written
     */
    public boolean isMonthly() {
        return monthly;
    }

    /**
     * @param monthly the written to set
     */
    public void setMonthly(boolean monthly) {
        this.monthly = monthly;
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
    public int getUnitCost() {
        return unitCost;
    }

    /**
     * @param unitCost the appearanceCost to set
     */
    public void setUnitCost(int unitCost) {
        this.unitCost = unitCost;
    }

}
