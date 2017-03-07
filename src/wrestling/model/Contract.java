package wrestling.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.factory.TitleFactory;

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

    private LocalDate startDate;

    private boolean exclusive;

    private int appearanceCost;

    //default constructor is empty, values must be set by contractFactory
    public Contract() {

    }

    //depreciates monthly contracts
    public void nextDay(LocalDate date) {

        duration--;

        if (duration <= 0) {
            terminateContract(date);
        }
    }

    //handles appearance-based contracts
    public void appearance(LocalDate date) {

        //make the promotion 'pay' the worker for the appearance
        promotion.bankAccount().removeFunds(appearanceCost, 'w', date);

        if (duration <= 0) {
            terminateContract(date);
        }
    }

    //for when a bigger promotion signs a written contract
    //that overrides this open contract
    public void buyOutContract() {

        duration = 0;
    }

    private void terminateContract(LocalDate date) {

        List<Title> toDrop = new ArrayList<>();
        for (Title t : worker.getTitles()) {
            if (t.getPromotion().equals(promotion)) {
                toDrop.add(t);
            }
        }

        for (Title t : toDrop) {

            TitleFactory.stripTitle(t, date);
        }

        worker.removeContract(this);

        promotion.removeContract(this);

    }

    public String getTerms() {
        String string = promotion.getName() + " Length: " + duration
                + " Appearance Cost: $" + getAppearanceCost();

        return string;
    }

    private List<LocalDate> bookedDates = new ArrayList<>();

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

}
