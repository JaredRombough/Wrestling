package openwrestling.model.utility;

import openwrestling.model.SegmentItem;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.segment.constants.StaffType;

public final class StaffUtils {
    public static boolean isRef(SegmentItem segmentItem) {
        return segmentItem instanceof StaffMember && ((StaffMember) segmentItem).getStaffType().equals(StaffType.REFEREE);
    }
}
