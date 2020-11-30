package openwrestling.view.utility.comparators;

import openwrestling.model.gameObjects.Segment;

import java.util.Comparator;

public class MatchPromotionComparator implements Comparator<Segment> {

    @Override
    public int compare(Segment segment1, Segment segment2) {
        if (segment1 != null && segment2 != null) {
            return segment1.getEvent().getPromotion().getName().compareTo(segment2.getEvent().getPromotion().getName());
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Promotion";
    }

}
