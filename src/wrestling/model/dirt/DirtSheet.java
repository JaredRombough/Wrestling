package wrestling.model.dirt;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wrestling.model.GameController;

public class DirtSheet {

    private final List<Dirt> reports;
    private final GameController gc;
    private final transient Logger logger;

    public DirtSheet(GameController gameController) {
        reports = new ArrayList<>();
        gc = gameController;
        logger = LogManager.getLogger(this.getClass());
    }

    public void newDirt(Dirt dirt) {
        dirt.setDate(gc.date());
        reports.add(dirt);
        logger.log(Level.INFO, dirt.getDate() + " " + dirt.toString());
    }

    /**
     * @return the reports
     */
    public List<Dirt> getReports() {
        return reports;
    }

}
