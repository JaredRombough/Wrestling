package wrestling.view.comparators;

import java.util.Comparator;
import wrestling.model.EventArchive;

public class EventDateComparator implements Comparator<EventArchive> {

    @Override
    public int compare(EventArchive e1, EventArchive e2) {
        
        if (e1 != null && e2 != null) {

            return -Integer.valueOf(e1.getDate()).compareTo(e2.getDate());
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Date";
    }

}
