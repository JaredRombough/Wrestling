package openwrestling.manager;

import lombok.Getter;
import openwrestling.file.Database;
import openwrestling.model.gameObjects.Promotion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PromotionManager implements Serializable {

    private final List<Promotion> promotions;
    private Promotion playerPromotion;

    public PromotionManager() {
        promotions = new ArrayList();
    }

    public void setPlayerPromotion(Promotion promotion) {
        playerPromotion = promotion;
    }

    public void createPromotions(List<Promotion> promotions) {
        for (Promotion promotion : promotions) {
            this.promotions.add(promotion);
        }
        Database.insertList(promotions);
    }

}
