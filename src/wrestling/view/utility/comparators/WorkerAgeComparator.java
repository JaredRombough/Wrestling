package wrestling.view.utility.comparators;

import java.util.Comparator;
import wrestling.model.modelView.WorkerView;

public class WorkerAgeComparator implements Comparator<WorkerView> {

    @Override
    public int compare(WorkerView w1, WorkerView w2) {
        if (w1 != null && w2 != null) {

            return -Integer.valueOf(w1.getAge()).compareTo(w2.getAge());
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Age";
    }

}
