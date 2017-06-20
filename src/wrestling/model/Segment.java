
package wrestling.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import wrestling.model.factory.TitleFactory;

/*
generic segment class to be extended by more specific segments like matches
*/
public abstract class Segment implements Serializable {
    
    public abstract List<Worker> allWorkers();
    public abstract int segmentRating();
    public abstract boolean isComplete();
    public abstract String processSegment(LocalDate date, TitleFactory titleFactory);
    
}
