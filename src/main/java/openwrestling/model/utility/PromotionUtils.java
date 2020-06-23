package openwrestling.model.utility;

import openwrestling.model.gameObjects.Promotion;

public class PromotionUtils {

    public static void gainPopularity(Promotion promotion) {
        modifyPopularity(promotion, 1);
    }

    public static void losePopularity(Promotion promotion) {
        modifyPopularity(promotion, -1);
    }

    public static int getEventTargetScore(Promotion promotion) {
        return ((promotion.getLevel() - 1) * 20) + (promotion.getPopularity() / 20);
    }

    private static void modifyPopularity(Promotion promotion, int amount) {
        int maxPop = 100;
        int minPop = 1;
        int maxLevel = 5;
        int minLevel = 1;
        int basePop = 10;
        promotion.setPopularity(promotion.getPopularity() + amount);

        if (promotion.getPopularity() >= maxPop) {
            if (promotion.getLevel() != maxLevel) {
                promotion.setLevel(promotion.getLevel() + 1);
                promotion.setPopularity(basePop);
            } else {
                promotion.setPopularity(maxPop);
            }
        } else if (promotion.getPopularity() < minPop) {
            if (promotion.getLevel() != minLevel) {
                promotion.setLevel(promotion.getLevel() - 1);
                promotion.setPopularity(maxPop);
            } else {
                promotion.setPopularity(minPop);
            }
        }
    }

}
