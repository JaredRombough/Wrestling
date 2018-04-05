package wrestling.view.utility.comparators;

import java.util.Comparator;
import wrestling.model.Worker;

public class WorkerBehaviourComparator implements Comparator<Worker> {

    @Override
    public int compare(Worker w1, Worker w2) {
        if (w1 != null && w2 != null) {

            return -Integer.valueOf(w1.getBehaviour()).compareTo(w2.getBehaviour());
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Behaviour";
    }

}
