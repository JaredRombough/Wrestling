package wrestling.model.utility;

import org.apache.commons.lang3.RandomUtils;
import wrestling.model.constants.GameConstants;
import wrestling.model.modelView.PromotionView;
import wrestling.model.segmentEnum.StaffType;

public final class StaffUtils {

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

    public static int getInjuryDurationBonusDays(PromotionView promotion) {
        int averageSkill = promotion.getStaffSkillAverage(StaffType.MEDICAL);
        return (int) Math.round(averageSkill * 0.01 * 15);
    }

    public static double getCoverageMatchRatingModifier(PromotionView promotion) {
        return getModifer(getStaffCoverage(promotion, StaffType.ROAD_AGENT),
                -0.2,
                0.1);
    }

    public static double getCrowdReactionModifer(PromotionView promotion) {
        return getModifer(getStaffCoverage(promotion, StaffType.CREATIVE),
                -0.2,
                0.1);
    }

    public static double getTrainerSuccessRate(PromotionView promotion) {
        double modifier = getModifer(getStaffCoverage(promotion, StaffType.TRAINER),
                -0.3,
                0.1);
        double baseRate = 0.05;
        return baseRate + (baseRate * modifier);
    }

    public static boolean trainerSuccess(PromotionView promotion) {
        return RandomUtils.nextInt(0, 1000) <= (1000 * StaffUtils.getTrainerSuccessRate(promotion));
    }

    private static double getModifer(int coverage, double minimum, double maximum) {
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

    public static double getMatchRatingModifier(PromotionView promotion) {
        int averageSkill = promotion.getStaffSkillAverage(StaffType.ROAD_AGENT);
        return 0.1 * averageSkill * 0.01;
    }

    public static double getAngleRatingModifier(PromotionView promotion) {
        int averageSkill = promotion.getStaffSkillAverage(StaffType.CREATIVE);
        return 0.1 * averageSkill * 0.01;
    }

    public static int getModifiedMatchRating(PromotionView promotion, int rating) {
        return (int) (rating + (rating * getCoverageMatchRatingModifier(promotion)) + (rating * getMatchRatingModifier(promotion)));
    }

    public static int getModifiedCrowdRating(PromotionView promotion, int rating) {
        return (int) (rating + (rating * getCrowdReactionModifer(promotion)));
    }

    public static int getModifiedAngleRating(PromotionView promotion, int rating) {
        return (int) (rating + (rating * getAngleRatingModifier(promotion)));
    }

    public static int getInjuryDuration(PromotionView promotion) {
        int min = 7;
        int max = 160;
        return RandomUtils.nextInt(min, max) - getInjuryDurationBonusDays(promotion);
    }

    public static int getStaffCoverage(PromotionView promotion, StaffType staffType) {
        float staffCount = promotion.getStaff(staffType).size();
        float staffCoverage = staffCount * staffType.workerRatio();
        float ratio = staffCoverage / promotion.getFullRoster().size();
        return Math.round(ratio * 100);
    }
}
