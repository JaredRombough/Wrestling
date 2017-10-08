package wrestling.model.dirt;

import java.time.LocalDate;
import java.util.List;
import wrestling.model.Promotion;
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.model.utility.ModelUtilityFunctions;

public class TitleRecord implements Dirt {

    private LocalDate date;
    private final Title title;
    private final LocalDate startDate;
    private final List<Worker> workers;
    private final Promotion promotion;

    public TitleRecord(Title title) {
        this.title = title;
        this.startDate = title.getDayWon();
        this.promotion = title.getPromotion();
        this.workers = title.getWorkers();

    }

    @Override
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    /**
     * @return the title
     */
    public Title getTitle() {
        return title;
    }

    @Override
    public List<Worker> getWorkers() {
        return workers;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(ModelUtilityFunctions.slashNames(workers));

        sb.append("\t\t\t");
        sb.append(startDate == null ? "????" : startDate);
        sb.append("\tto\t");
        sb.append(date);

        return sb.toString();
    }

    @Override
    public Promotion getPromotion() {
        return promotion;
    }
}
