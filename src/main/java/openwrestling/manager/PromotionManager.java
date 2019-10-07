package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.Promotion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PromotionManager implements Serializable {

    private List<Promotion> promotions;
    private Promotion playerPromotion;

    public PromotionManager() {
        promotions = new ArrayList();
    }

    public void setPlayerPromotion(Promotion promotion) {
        playerPromotion = promotion;
    }

    public List<Promotion> createPromotions(List<Promotion> promotions) {
        List<Promotion> saved = Database.insertList(promotions);
        this.promotions.addAll(saved);
        return saved;
    }

    public List<Promotion> updatePromotions(List<Promotion> promotions) {
        List<Promotion> saved = Database.updateList(promotions);
        saved.addAll(
                this.promotions.stream()
                        .filter(promotion -> saved.stream().noneMatch(savedPromotion -> savedPromotion.getPromotionID() == promotion.getPromotionID()))
                        .collect(Collectors.toList())
        );
        this.promotions = saved;
        return saved;
    }

}
