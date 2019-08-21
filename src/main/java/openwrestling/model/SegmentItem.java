package openwrestling.model;

import java.util.ArrayList;
import java.util.List;
import openwrestling.model.segmentEnum.ActiveType;
import openwrestling.model.segmentEnum.Gender;

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
