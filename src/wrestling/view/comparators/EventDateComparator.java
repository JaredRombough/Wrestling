package wrestling.view.comparators;

import java.util.Comparator;
import wrestling.model.EventArchive;

public class EventDateComparator implements Comparator<EventArchive> {

    @Override
    public int compare(EventArchive e1, EventArchive e2) {

        if (e1 != null && e2 != null) {

            int cmp = (e1.getDate().getYear() - e2.getDate().getYear());
            if (cmp == 0) {
                cmp = (e1.getDate().getDayOfYear() - e2.getDate().getDayOfYear());

            }
            return cmp;

        }

        return 0;
    }

    @Override
    public String toString() {
        return "Date";
    }

}
