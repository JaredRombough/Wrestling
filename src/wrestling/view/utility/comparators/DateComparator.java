package wrestling.view.utility.comparators;

import java.util.Comparator;
import wrestling.model.interfaces.iEventDate;

public class DateComparator implements Comparator<iEventDate> {

    @Override
    public int compare(iEventDate e1, iEventDate e2) {

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
