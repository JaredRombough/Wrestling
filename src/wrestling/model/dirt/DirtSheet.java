package wrestling.model.dirt;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wrestling.model.controller.GameController;
import wrestling.model.Promotion;
import wrestling.model.controller.DateManager;

public class DirtSheet {

    private final List<Dirt> reports;
    private final DateManager dateManager;
    private final transient Logger logger;

    public DirtSheet(DateManager dateManager) {
        reports = new ArrayList<>();
        this.dateManager = dateManager;
        logger = LogManager.getLogger(this.getClass());
    }

    public void newDirt(Dirt dirt) {
        dirt.setDate(dateManager.today());
        reports.add(dirt);
        logger.log(Level.INFO, dirt.getDate() + " " + dirt.toString());
    }

    /**
     * @return the reports
     */
    public List<Dirt> getReports() {
        return reports;
    }

    public List<EventArchive> promotionEvents(Promotion promotion) {
        List<EventArchive> events = new ArrayList<>();
        reports.stream().filter((dirt) -> (dirt instanceof EventArchive && dirt.getPromotion().equals(promotion))).forEach((dirt) -> {
            events.add((EventArchive) dirt);
        });

        return events;
    }

}
