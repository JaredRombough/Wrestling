package wrestling.model.utility;

import java.util.List;
import java.util.stream.Collectors;
import wrestling.model.SegmentItem;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.StaffView;
import wrestling.model.segmentEnum.StaffType;

public final class StaffUtils {

    public static int getStaffSkillAverage(StaffType staffType, PromotionView promotion) {
        double total = 0;
        List<StaffView> staffOfType = getStaff(staffType, promotion);
        for (StaffView staff : staffOfType) {
            total += staff.getSkill();
        }
        return (int) Math.ceil(total / staffOfType.size());
    }

    public static int getStaffSkillModifier(StaffType staffType, PromotionView promotion) {
        double coverage = getStaffCoverage(promotion, staffType);
        double averageSkill = getStaffSkillAverage(staffType, promotion);
        if (coverage > 100) {
            return (int) averageSkill;
        }
        return (int) (coverage / 100 * averageSkill);
    }

    public static List<StaffView> getStaff(StaffType staffType, PromotionView promotion) {
        return promotion.getAllStaff().stream().filter(staff -> staff.getStaffType().equals(staffType)).collect(Collectors.toList());
    }

    public static int getStaffCoverage(PromotionView promotion, StaffType staffType) {

        float staffCount = getStaff(staffType, promotion).size();
        float ratio;
        if (staffType.equals(StaffType.PRODUCTION)) {
            ratio = staffCount / (promotion.getLevel() * 2);
        } else {
            float staffCoverage = staffCount * staffType.workerRatio();
            ratio = staffCoverage / promotion.getFullRoster().size();
        }
        return Math.round(ratio * 100);
    }

    public static int idealStaffCount(PromotionView promotion, StaffType staffType) {
        if (staffType.equals(StaffType.PRODUCTION)) {
            return promotion.getLevel() * 2;
        }
        if (staffType.workerRatio() == 0) {
            return 0;
        }
        float ideal = promotion.getFullRoster().size() / staffType.workerRatio();
        return (int) Math.ceil(ideal);
    }

    public static boolean isRef(SegmentItem segmentItem) {
        return segmentItem instanceof StaffView && ((StaffView) segmentItem).getStaffType().equals(StaffType.REFEREE);
    }
}
