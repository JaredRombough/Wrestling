package openwrestling.view.utility.comparators;

import java.util.Comparator;
import openwrestling.model.SegmentItem;

public class SegmentItemBehaviourComparator implements Comparator<SegmentItem> {

    @Override
    public int compare(SegmentItem segmentItem1, SegmentItem segmentItem2) {
        if (segmentItem1 != null && segmentItem2 != null) {

            return -Integer.valueOf(segmentItem1.getBehaviour()).compareTo(segmentItem2.getBehaviour());
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Behaviour";
    }

}
