package wrestling.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DirtSheet {

    private List<Dirt> reports;
    private GameController gc;
    private transient Logger logger;

    public DirtSheet(GameController gameController) {
        reports = new ArrayList<>();
        gc = gameController;
        logger = LogManager.getLogger(this.getClass());
    }

    public void newDirt(String string, LocalDate date) {
        Dirt dirt = new Dirt(string, date);
        getReports().add(dirt);
        logger.log(Level.INFO, date + " " + string);
    }

    public void newDirt(Dirt dirt) {
        reports.add(dirt);
        logger.log(Level.INFO, dirt.getDate() + " " + dirt.toString());
    }

    public void newDirt(String string) {
        newDirt(string, gc.date());
    }

    public void newDirt(String string, Worker worker, Promotion promotion) {
        newDirt(string, Arrays.asList(worker), Arrays.asList(promotion));
    }

    public void newDirt(String string, List<Worker> workers, List<Promotion> promotions) {
        Dirt dirt = new Dirt(string, gc.date(), workers, promotions, null);
        newDirt(dirt);
    }

    public void newDirt(String string, List<Worker> workers, Promotion promotion, EventArchive eventArchive) {
        newDirt(string, workers, Arrays.asList(promotion), eventArchive);
    }

    public void newDirt(String string, List<Worker> workers, List<Promotion> promotions, EventArchive eventArchive) {
        Dirt dirt = new Dirt(string, gc.date(), workers, promotions, eventArchive);
        newDirt(dirt);
    }

    /**
     * @return the reports
     */
    public List<Dirt> getReports() {
        return reports;
    }

}
