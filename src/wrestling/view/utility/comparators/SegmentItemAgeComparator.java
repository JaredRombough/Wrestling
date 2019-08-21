package wrestling.view.utility.comparators;

import java.util.Comparator;
import wrestling.model.SegmentItem;

public class SegmentItemAgeComparator implements Comparator<SegmentItem> {

    @Override
    public int compare(SegmentItem segmentItem1, SegmentItem segmentItem2) {
        if (segmentItem1 != null && segmentItem2 != null) {

            return -Integer.valueOf(segmentItem1.getAge()).compareTo(segmentItem2.getAge());
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Age";
    }

}
