package wrestling.view.utility.comparators;

import java.util.Comparator;
import wrestling.model.interfaces.iNewsItem;

public class NewsItemComparator implements Comparator<iNewsItem> {

    @Override
    public int compare(iNewsItem e2, iNewsItem e1) {

        if (e1 != null && e2 != null) {

            int cmp = (e1.getDate().getYear() - e2.getDate().getYear());
            if (cmp == 0) {
                cmp = (e1.getDate().getDayOfYear() - e2.getDate().getDayOfYear());

            }
            return cmp;

        }

        return 0;
    }
}
