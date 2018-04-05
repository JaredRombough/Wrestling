package wrestling.view.utility.comparators;

import java.util.Comparator;
import wrestling.model.Worker;
import wrestling.model.factory.MatchFactory;

public class WorkerPotentialComparator implements Comparator<Worker> {

    private final MatchFactory matchFactory;

    public WorkerPotentialComparator(MatchFactory matchFactory) {
        this.matchFactory = matchFactory;
    }

    @Override
    public int compare(Worker w1, Worker w2) {
        if (w1 != null && w2 != null) {

            return -Integer.valueOf(matchFactory.getMatchWorkRating(w1)).compareTo(matchFactory.getMatchWorkRating(w2));
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Potential";
    }

}
