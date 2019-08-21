package wrestling.view.utility.comparators;

import java.util.Comparator;

public class NameComparator implements Comparator<Object> {

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 != null && o2 != null) {
            if (null != o1.toString() && null != o2.toString()) {
                return o1.toString().compareTo(o2.toString());
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Name";
    }

}
