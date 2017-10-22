package wrestling.model;

public class Booking {

    private final Worker worker;
    private final PromotionEvent event;

    public Booking(Worker worker, PromotionEvent event) {
        this.worker = worker;
        this.event = event;
    }

    /**
     * @return the worker
     */
    public Worker getWorker() {
        return worker;
    }

    /**
     * @return the event
     */
    public PromotionEvent getEvent() {
        return event;
    }
}
