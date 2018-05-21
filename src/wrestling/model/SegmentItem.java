package wrestling.model;

import java.util.ArrayList;
import java.util.List;

public interface SegmentItem {

    default List<SegmentItem> getSegmentItems() {
        List<SegmentItem> items = new ArrayList<>();
        items.add(this);
        return items;
    }

    default String getShortName() {
        return this.toString();
    }
}
