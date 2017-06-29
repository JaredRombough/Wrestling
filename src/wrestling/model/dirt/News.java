package wrestling.model.dirt;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import wrestling.model.Promotion;
import wrestling.model.Worker;

public class News implements Dirt {

    private LocalDate date;
    private final String string;
    private final List<Worker> workers;
    private final Promotion promotion;

    public News(String string, List<Worker> workers, Promotion promotion) {
        this.string = string;
        this.workers = workers;
        this.promotion = promotion;
    }

    public News(String string, Worker worker, Promotion promotion) {
        this.string = string;
        this.workers = Arrays.asList(worker);
        this.promotion = promotion;
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
    public String toString() {
        return string;
    }

    @Override
    public List<Worker> getWorkers() {
        return workers;
    }

}
