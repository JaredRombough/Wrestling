package openwrestling.model;

import java.time.LocalDate;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;

public class Injury {

    private final LocalDate expiryDate;
    private final LocalDate startDate;
    private final Worker worker;
    private final Promotion promotion;

    public Injury(LocalDate starDate, LocalDate expiryDate, Worker worker, Promotion promotion) {
        this.startDate = starDate;
        this.expiryDate = expiryDate;
        this.worker = worker;
        this.promotion = promotion;
    }

    /**
     * @return the expiryDate
     */
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    /**
     * @return the worker
     */
    public Worker getWorker() {
        return worker;
    }

    /**
     * @return the startDate
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getDate() {
        return startDate;
    }

    public Promotion getPromotion() {
        return promotion;
    }
}
