package openwrestling.model.utility;

import openwrestling.model.gameObjects.MonthlyReview;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class MonthlyReviewUtils {

    public static String popularityString(List<MonthlyReview> recentReviews, String ownerName) {
        if (CollectionUtils.isEmpty(recentReviews) || recentReviews.size() == 1) {
            return "";
        }

        MonthlyReview thisReview = recentReviews.get(0);
        MonthlyReview lastReview = recentReviews.get(1);
        boolean popUp = popularityDecreased(thisReview, lastReview);

        if (popUp) {
            return ownerName + " is happy popularity increased this month.";
        }

        int count = 0;
        MonthlyReview lastMonth = thisReview;
        for (MonthlyReview recentReview : recentReviews) {
            if (popularityDecreased(lastMonth, recentReview)) {
                count++;
                lastMonth = recentReview;
            } else {
                break;
            }
        }

        return String.format(ownerName + " is not happy about popularity going down for %d month(s).", count);
    }

    public static String fundsString(List<MonthlyReview> recentReviews, String ownerName) {
        if (CollectionUtils.isEmpty(recentReviews) || recentReviews.size() == 1) {
            return "";
        }

        MonthlyReview thisReview = recentReviews.get(0);
        MonthlyReview lastReview = recentReviews.get(1);
        boolean fundsIncreased = thisReview.getFunds() > lastReview.getFunds();

        if (fundsIncreased) {
            return ownerName + " is happy funds increased this month.";
        }

        int count = 0;
        long lastFunds = thisReview.getFunds();
        for (MonthlyReview recentReview : recentReviews) {
            if (lastFunds < recentReview.getFunds()) {
                count++;
                lastFunds = recentReview.getFunds();
            } else {
                break;
            }
        }

        return String.format(ownerName + " not happy about funds going down for %d month(s).", count);
    }

    private static boolean popularityDecreased(MonthlyReview thisMonth, MonthlyReview lastMonth) {
        return thisMonth.getPopularity() > lastMonth.getPopularity() &&
                thisMonth.getLevel() >= lastMonth.getLevel();
    }


}
