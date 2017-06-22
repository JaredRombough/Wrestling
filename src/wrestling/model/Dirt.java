package wrestling.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dirt {

    private final String string;
    private final LocalDate date;
    private final List<Worker> workers;
    private final List<Promotion> promotions;
    private final EventArchive eventArchive;
    

    public Dirt(String string, LocalDate date) {
        this.string = string;
        this.date = date;
        this.workers = null;
        this.promotions = null;
        this.eventArchive = null;
    }
    
    public Dirt(String string, LocalDate date, List<Worker> workers, List<Promotion> promotions, EventArchive eventArchive)
    {
        this.string = string;
        this.date = date;
        this.workers = workers;
        this.promotions = promotions;
        this.eventArchive = eventArchive;
    }
    
    public Dirt(String string, LocalDate date, Worker worker, Promotion promotion)
    {
        this.string = string;
        this.date = date;
        this.workers = new ArrayList<>(Arrays.asList(worker));
        this.promotions = new ArrayList<>(Arrays.asList(promotion));
        this.eventArchive = null;
    }
    
    @Override
    public String toString()
    {
        return getString();
    }

    /**
     * @return the string
     */
    public String getString() {
        return string;
    }

    /**
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @return the workers
     */
    public List<Worker> getWorkers() {
        return workers;
    }

    /**
     * @return the promotions
     */
    public List<Promotion> getPromotions() {
        return promotions;
    }

    /**
     * @return the eventArchive
     */
    public EventArchive getEventArchive() {
        return eventArchive;
    }

}
