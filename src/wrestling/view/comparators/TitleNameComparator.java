
package wrestling.view.comparators;


import java.util.Comparator;
import wrestling.model.Title;

public class TitleNameComparator implements Comparator<Title> {

    @Override
    public int compare(Title o1, Title o2) {
        if (o1 != null && o2 != null) {
            if (null != o1.getName()&& null != o2.getName()) {
                return o1.getName().compareTo(o2.getName());
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Name";
    }

}
