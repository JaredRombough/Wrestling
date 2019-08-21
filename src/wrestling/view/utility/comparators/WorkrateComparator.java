package wrestling.view.utility.comparators;

import java.util.Comparator;
import wrestling.model.modelView.WorkerView;
import wrestling.model.utility.ModelUtils;

public class WorkrateComparator implements Comparator<WorkerView> {

    @Override
    public int compare(WorkerView w1, WorkerView w2) {
        if (w1 != null && w2 != null) {

            return -Integer.valueOf(ModelUtils.getMatchWorkRating(w1)).compareTo(ModelUtils.getMatchWorkRating(w2));
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Workrate";
    }

}
