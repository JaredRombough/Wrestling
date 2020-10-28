package openwrestling.model;

import openwrestling.model.segment.constants.ActiveType;
import openwrestling.model.segment.constants.Gender;

import java.util.ArrayList;
import java.util.List;

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

    default String getImageFileName() {
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
