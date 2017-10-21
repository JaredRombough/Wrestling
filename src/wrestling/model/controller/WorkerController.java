package wrestling.model.controller;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wrestling.model.Contract;
import wrestling.model.Promotion;
import wrestling.model.Worker;
import wrestling.model.factory.EventFactory;
import wrestling.model.utility.ModelUtilityFunctions;

public class WorkerController implements Serializable {

    private final Worker worker;
    private final GameController gameController;
    private final ContractManager contractManager;
    private final List<EventFactory> bookings = new ArrayList<>();

    //list of dates on which the worker has events scheduled
    private List<LocalDate> eventDates = new ArrayList<>();

    public WorkerController(Worker worker, GameController gameController) {
        this.worker = worker;
        this.gameController = gameController;
        this.contractManager = gameController.getContractManager();
    }

    private transient Logger log = LogManager.getLogger(this.getClass());

    public Contract getContract(Promotion promotion) {

        Contract thisContract = null;
        for (Contract current : contractManager.getContracts(getWorker())) {
            if (current.getPromotion().equals(promotion)) {
                thisContract = current;
            }
        }

        if (thisContract == null) {
            log.log(Level.ERROR, "NULL CONTRACT\n" + getWorker().getName() + "\n" + promotion.getName());
        }

        return thisContract;
    }

    //checks if a worker is booked at all on a given date
    public boolean isBooked(LocalDate date) {
        boolean isBooked = false;

        for (Contract contract : contractManager.getContracts(getWorker())) {
            if (contract.getBookedDates().contains(date)) {
                isBooked = true;
            }
        }

        return isBooked;

    }

    //checks if a worker is booked on a certain date
    //returns false if the booking is with the given promotion
    public boolean isBooked(LocalDate date, Promotion p) {
        boolean isBooked = isBooked(date);

        if (getContract(p).getBookedDates().contains(date)) {
            isBooked = false;
        }

        return isBooked;

    }

    public void gainPopularity() {

        int maxPopularity = 0;

        for (Contract contract : contractManager.getContracts(getWorker())) {
            if (ModelUtilityFunctions.maxPopularity(contract.getPromotion()) > maxPopularity) {
                maxPopularity = ModelUtilityFunctions.maxPopularity(contract.getPromotion());
            }
        }

        if (getWorker().getPopularity() < maxPopularity
                || ModelUtilityFunctions.randRange(1, 10) == 1
                && getWorker().getPopularity() > 100) {

            int range = 0;

            if (getWorker().getPopularity() >= 90) {
                range = 20;
            } else if (getWorker().getPopularity() < 90 && getWorker().getPopularity() >= 80) {
                range = 10;
            } else if (getWorker().getPopularity() < 80 && getWorker().getPopularity() >= 70) {
                range = 7;
            } else if (getWorker().getPopularity() < 70) {
                range = 5;
            }

            if (ModelUtilityFunctions.randRange(1, range) == 1) {

                addPopularity(1);
            }
        }
    }

    private void addPopularity(int pop) {
        getWorker().setPopularity(getWorker().getPopularity() + pop);
    }

    public void losePopularity() {

        if (ModelUtilityFunctions.randRange(1, 10) == 10
                && getWorker().getPopularity() > 0
                && getWorker().getPopularity() > getWorker().getMinimumPopularity()) {
            addPopularity(-1);
        }
    }

    public void addBooking(EventFactory event) {
        bookings.add(event);
    }

    public List<EventFactory> getBookings() {
        return bookings;
    }

    public String contractString() {

        StringBuilder bld = new StringBuilder();
        for (Contract current : contractManager.getContracts(getWorker())) {

            bld.append(gameController.getContractManager().getTerms(current));
            bld.append("\n");
        }
        return bld.toString();
    }

    /**
     * @return the worker
     */
    public Worker getWorker() {
        return worker;
    }

}
