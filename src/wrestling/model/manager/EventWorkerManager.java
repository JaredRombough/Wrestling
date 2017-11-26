package wrestling.model.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.EventWorker;
import wrestling.model.Promotion;
import wrestling.model.Worker;

public class EventWorkerManager {

    private final List<EventWorker> eventWorkers;

    public EventWorkerManager() {
        eventWorkers = new ArrayList();
    }

    //checks if a worker is booked at all on a given date
    public boolean isBooked(Worker worker, LocalDate date) {
        boolean isBooked = false;

        for (EventWorker eventWorker : eventWorkers) {
            if (eventWorker.getEvent().getDate().equals(date)
                    && eventWorker.getWorker().equals(worker)) {
                isBooked = true;
                break;
            }
        }

        return isBooked;
    }

    private EventWorker getBooking(Worker worker, LocalDate date) {
        EventWorker workerBooking = null;
        for (EventWorker eventWorker : eventWorkers) {
            if (eventWorker.getEvent().getDate().equals(date)
                    && eventWorker.getWorker().equals(worker)) {
                workerBooking = eventWorker;
                break;
            }
        }
        return workerBooking;
    }

    //checks if a worker is booked on a certain date
    //returns false if the booking is with the given promotion
    public boolean isAvailable(Worker worker, LocalDate date, Promotion promotion) {
        boolean isAvailable = true;
        EventWorker eventWorker = getBooking(worker, date);
        if (eventWorker != null && !eventWorker.getEvent().getPromotion().equals(promotion)) {
            isAvailable = false;
        }
        return isAvailable;
    }

}
