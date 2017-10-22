package wrestling.model.controller;

import java.util.ArrayList;
import java.util.List;
import wrestling.model.Promotion;

public class PromotionManager {

    private final List<Promotion> promotions;
    private Promotion playerPromotion;

    public PromotionManager() {
        promotions = new ArrayList();
    }

    public List<Promotion> aiPromotions() {
        List<Promotion> aiPromotions = new ArrayList();
        for (Promotion promotion : promotions) {
            if (!promotion.equals(playerPromotion)) {
                aiPromotions.add(promotion);
            }
        }
        return aiPromotions;
    }

    public void setPlayerPromotion(Promotion promotion) {
        playerPromotion = promotion;
    }

    public Promotion playerPromotion() {
        return playerPromotion;
    }

    public void addPromotions(List<Promotion> promotions) {
        for (Promotion promotion : promotions) {
            this.promotions.add(promotion);
        }
    }

    /**
     * @return the promotions
     */
    public List<Promotion> getPromotions() {
        return promotions;
    }

}
