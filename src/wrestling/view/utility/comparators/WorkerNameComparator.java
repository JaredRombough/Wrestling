
package wrestling.view.utility.comparators;


import java.util.Comparator;
import wrestling.model.Worker;

public class WorkerNameComparator implements Comparator<Worker> {

    @Override
    public int compare(Worker o1, Worker o2) {
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
