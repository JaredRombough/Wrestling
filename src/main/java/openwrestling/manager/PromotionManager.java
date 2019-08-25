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

    public List<Promotion> createPromotions(List<Promotion> promotions) {
        List saved = Database.insertList(promotions);
        this.promotions.addAll(saved);
        return saved;
    }

}
