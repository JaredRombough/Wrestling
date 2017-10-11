package wrestling.model;

import java.io.Serializable;
import java.util.List;

/*
generic segment class to be extended by more specific segments like matches
 */
public abstract class Segment implements Serializable {

    public abstract List<Worker> allWorkers();
    public abstract int segmentRating();
    public abstract boolean isComplete();

}
