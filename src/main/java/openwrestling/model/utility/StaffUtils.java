package openwrestling.model.utility;

import openwrestling.model.SegmentItem;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segmentEnum.StaffType;

import java.util.List;

public final class StaffUtils {


    public static int idealStaffCount(Promotion promotion, StaffType staffType, List<Worker> roster) {
        if (staffType.equals(StaffType.PRODUCTION)) {
            return promotion.getLevel() * 2;
        }
        if (staffType.workerRatio() == 0) {
            return 0;
        }
        float ideal = roster.size() / staffType.workerRatio();
        return (int) Math.ceil(ideal);
    }

    public static boolean isRef(SegmentItem segmentItem) {
        return segmentItem instanceof StaffMember && ((StaffMember) segmentItem).getStaffType().equals(StaffType.REFEREE);
    }
}
