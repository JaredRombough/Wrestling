package wrestling.model.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.Booking;
import wrestling.model.Promotion;
import wrestling.model.Worker;

public class BookingManager {

    private final List<Booking> bookings;

    public BookingManager() {
        bookings = new ArrayList();
    }

    //checks if a worker is booked at all on a given date
    public boolean isBooked(Worker worker, LocalDate date) {
        boolean isBooked = false;

        for (Booking booking : bookings) {
            if (booking.getEvent().getLocalDate().equals(date)
                    && booking.getWorker().equals(worker)) {
                isBooked = true;
                break;
            }
        }

        return isBooked;
    }

    private Booking getBooking(Worker worker, LocalDate date) {
        Booking workerBooking = null;
        for (Booking booking : bookings) {
            if (booking.getEvent().getLocalDate().equals(date)
                    && booking.getWorker().equals(worker)) {
                workerBooking = booking;
                break;
            }
        }
        return workerBooking;
    }

    //checks if a worker is booked on a certain date
    //returns false if the booking is with the given promotion
    public boolean isAvailable(Worker worker, LocalDate date, Promotion promotion) {
        boolean isAvailable = true;
        Booking booking = getBooking(worker, date);
        if (booking != null && !booking.getEvent().getPromotion().equals(promotion)) {
            isAvailable = false;
        }
        return isAvailable;
    }

}
