package wrestling.model;

import java.util.ArrayList;
import java.util.List;
import wrestling.model.segmentEnum.ActiveType;
import wrestling.model.segmentEnum.Gender;

public interface SegmentItem {

    default List<? extends SegmentItem> getSegmentItems() {
        List<SegmentItem> items = new ArrayList<>();
        items.add(this);
        return items;
    }

    default String getShortName() {
        return this.toString();
    }

    default String getLongName() {
        return this.toString();
    }

    default String getImageString() {
        return "";
    }

    default Gender getGender() {
        return Gender.ALL;
    }

    default ActiveType getActiveType() {
        return ActiveType.ALL;
    }

    default int getBehaviour() {
        return 100;
    }

    default int getAge() {
        return 0;
    }
}
