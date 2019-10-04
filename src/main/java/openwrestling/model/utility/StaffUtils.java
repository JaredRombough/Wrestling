package openwrestling.model.utility;

import java.util.List;
import java.util.stream.Collectors;

import openwrestling.model.SegmentItem;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.segmentEnum.StaffType;

public final class StaffUtils {

    public static int getStaffSkillAverage(StaffType staffType, Promotion promotion) {
        double total = 0;
        List<StaffMember> staffOfType = getStaff(staffType, promotion);
        for (StaffMember staff : staffOfType) {
            total += staff.getSkill();
        }
        return (int) Math.ceil(total / staffOfType.size());
    }

    public static int getStaffSkillModifier(StaffType staffType, Promotion promotion, List<Worker> roster) {
        double coverage = getStaffCoverage(promotion, staffType, roster);
        double averageSkill = getStaffSkillAverage(staffType, promotion);
        if (coverage > 100) {
            return (int) averageSkill;
        }
        return (int) (coverage / 100 * averageSkill);
    }

    public static List<StaffMember> getStaff(StaffType staffType, Promotion promotion) {
        return promotion.getAllStaff().stream().filter(staff -> staff.getStaffType().equals(staffType)).collect(Collectors.toList());
    }

    public static int getStaffCoverage(Promotion promotion, StaffType staffType, List<Worker> roster) {

        float staffCount = getStaff(staffType, promotion).size();
        float ratio;
        if (staffType.equals(StaffType.PRODUCTION)) {
            ratio = staffCount / (promotion.getLevel() * 2);
        } else {
            float staffCoverage = staffCount * staffType.workerRatio();
            ratio = staffCoverage / roster.size();
        }
        return Math.round(ratio * 100);
    }

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
