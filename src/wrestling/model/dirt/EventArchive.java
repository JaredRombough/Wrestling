package wrestling.model.dirt;

import java.time.LocalDate;
import java.util.List;
import wrestling.model.Promotion;
import wrestling.model.Television;
import wrestling.model.Worker;

/**
 *
 * for storing a completed event
 *
 *
 */
public class EventArchive implements Dirt {

    private LocalDate date;
    private final List<Worker> workers;
    private final Promotion promotion;
    private final EventType eventType;
    private final int cost;
    private final int gate;
    private final int attendance;
    private Television television;

    public EventArchive(List<Worker> workers, Promotion promotion, EventType eventType, int cost, int gate, int attendance) {
        this.workers = workers;
        this.promotion = promotion;
        this.eventType = eventType;
        this.cost = cost;
        this.gate = gate;
        this.attendance = attendance;
    }

    @Override
    public String toString() {
        return promotion.getName() + " event " + date.toString();
    }

    @Override
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public List<Worker> getWorkers() {
        return workers;
    }

    @Override
    public Promotion getPromotion() {
        return promotion;
    }

    /**
     * @return the eventType
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * @return the cost
     */
    public int getCost() {
        return cost;
    }

    /**
     * @return the gate
     */
    public int getGate() {
        return gate;
    }

    /**
     * @return the attendance
     */
    public int getAttendance() {
        return attendance;
    }

    /**
     * @return the television
     */
    public Television getTelevision() {
        return television;
    }

    /**
     * @param television the television to set
     */
    public void setTelevision(Television television) {
        this.television = television;
    }

}
