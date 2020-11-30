package openwrestling.view.utility.comparators;

import openwrestling.model.gameObjects.Segment;

import java.util.Comparator;

public class MatchRatingComparator implements Comparator<Segment> {

    @Override
    public int compare(Segment segment1, Segment segment2) {
        if (segment1 != null && segment2 != null) {

            return -Integer.compare(segment1.getWorkRating(), segment2.getWorkRating());
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Rating";
    }

}
