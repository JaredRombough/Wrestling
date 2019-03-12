package wrestling.model.utility;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomUtils;
import wrestling.model.SegmentItem;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.WorkerView;
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

    public static int getInjuryDurationBonusDays(PromotionView promotion) {
        int averageSkill = getStaffSkillAverage(StaffType.MEDICAL, promotion);
        return (int) Math.round(averageSkill * 0.01 * 15);
    }

    public static double getCoverageAttendanceModifier(PromotionView promotion) {
        return getCoverageModifier(getStaffCoverage(promotion, StaffType.PRODUCTION),
                -0.2,
                0.1);
    }

    public static double getTrainerSuccessRate(PromotionView promotion) {
        double modifier = getCoverageModifier(getStaffCoverage(promotion, StaffType.TRAINER),
                -0.3,
                0.1);
        double baseRate = 0.05;
        return baseRate + (baseRate * modifier);
    }

    public static boolean trainerSuccess(PromotionView promotion) {
        return RandomUtils.nextInt(0, 1000) <= (1000 * StaffUtils.getTrainerSuccessRate(promotion));
    }

    private static double getCoverageModifier(int coverage, double minimum, double maximum) {
        double rate;
        if (coverage <= 100) {
            rate = minimum * (100 - coverage) * 0.01;
        } else if (coverage - 100 > 100) {
            rate = maximum;
        } else {
            rate = maximum * (coverage - 100) * 0.01;
        }
        return rate;
    }

    public static double getInjuryRate(PromotionView promotion) {
        int coverage = getStaffCoverage(promotion, StaffType.MEDICAL);
        double rate = 0.01;
        if (coverage <= 100) {
            rate += 0.01 * (100 - coverage) * 0.01;
        } else if (coverage - 100 > 100) {
            rate = .005;
        } else {
            rate -= 0.005 * (coverage - 100) * 0.01;
        }
        return rate;
    }

    public static double getProductionCrowdRatingModifier(PromotionView promotion) {
        int averageSkill = getStaffSkillAverage(StaffType.PRODUCTION, promotion);
        return 0.1 * averageSkill * 0.01;
    }

    public static double getBroadcastTeamMatchRatingModifier(List<? extends SegmentItem> broadcastTeam) {
        int totalSkill = 0;
        for (SegmentItem broadcaster : broadcastTeam) {
            if (broadcaster instanceof StaffView) {
                totalSkill += ((StaffView) broadcaster).getSkill();
            } else if (broadcaster instanceof WorkerView) {
                totalSkill += ((WorkerView) broadcaster).getCharisma();
            }
        }
        int averageSkill = totalSkill / broadcastTeam.size();
        return 0.1 * averageSkill * 0.01;
    }

    public static double getCombinedCrowdRatingModifier(PromotionView promotion) {
        return getProductionCrowdRatingModifier(promotion);
    }

    public static int getInjuryDuration(PromotionView promotion) {
        int min = 7;
        int max = 160;
        return RandomUtils.nextInt(min, max) - getInjuryDurationBonusDays(promotion);
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
