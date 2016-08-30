package wrestling.model;

import java.io.Serializable;

/**
 *
 * A contract is a link between a worker and a promotion
 * that also knows the conditions (pay for appearance, length, etc)
 * 
 * 
 */
public class Contract implements Serializable {
    
    private Promotion promotion;
    private Worker worker;
    
    //an int that corresponds to the day that the contract will expire
    private int expirationDate;
    
    //total number of days
    private int length;
    
    private int startDate;
    
    private boolean written;
    
    private boolean exclusive;
    
    private int appearanceCost;
    private int monthlyCost;
    
    public Contract(Worker worker, Promotion promotion, int length, int currentDay) {
        this.worker = worker;
        
        this.promotion = promotion;
        this.length = length;
        this.expirationDate = length + currentDay;
        this.startDate = currentDay;
        this.exclusive = false;
        this.written = false;
        this.monthlyCost = 0;
        this.appearanceCost = worker.getProficiency() * 5;
        
        //we need to give the worker and the promotion a link to the contract
        //but maybe this is not the best place to do it?
        worker.addContract(this);
        promotion.addContract(this);
        
    }
    
    public String getTerms() {
        String string = getPromotion() + " Length: " + getLength() + " days Expires: " + getExpirationDate() 
                + " Appearance Cost: $" + getAppearanceCost();
        
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
     * @return the expirationDate
     */
    public int getExpirationDate() {
        return expirationDate;
    }

    /**
     * @param expirationDate the expirationDate to set
     */
    public void setExpirationDate(int expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
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
    public boolean isWritten() {
        return written;
    }

    /**
     * @param written the written to set
     */
    public void setWritten(boolean written) {
        this.written = written;
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
     * @param appearanceCost the appearanceCost to set
     */
    public void setAppearanceCost(int appearanceCost) {
        this.appearanceCost = appearanceCost;
    }

    /**
     * @return the monthlyCost
     */
    public int getMonthlyCost() {
        return monthlyCost;
    }

    /**
     * @param monthlyCost the monthlyCost to set
     */
    public void setMonthlyCost(int monthlyCost) {
        this.monthlyCost = monthlyCost;
    }
    
}
