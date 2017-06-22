package wrestling.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DirtSheet {
    
    private List<Dirt> reports;
    private transient Logger logger;

    public DirtSheet() {
        reports = new ArrayList<>();
        logger = LogManager.getLogger(this.getClass());
    }
    
    public void newDirt(String string, LocalDate date)
    {
        Dirt dirt = new Dirt(string, date);
        reports.add(dirt);
        logger.log(Level.INFO, date + " " + string);
    }
    
    public void newDirt(Dirt dirt)
    {
        reports.add(dirt);
        logger.log(Level.INFO, dirt.getDate() + " " + dirt.toString());
    }

}
