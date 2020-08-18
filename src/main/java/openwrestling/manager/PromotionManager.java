package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.financial.BankAccount;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static openwrestling.model.constants.SettingKeys.PLAYER_PROMOTION;

@Getter
public class PromotionManager extends GameObjectManager implements Serializable {

    private Map<Long, Promotion> promotionMap = new HashMap<>();
    private Promotion playerPromotion;
    private BankAccountManager bankAccountManager;
    private GameSettingManager gameSettingManager;

    public PromotionManager(Database database, BankAccountManager bankAccountManager, GameSettingManager gameSettingManager) {
        super(database);
        this.bankAccountManager = bankAccountManager;
        this.gameSettingManager = gameSettingManager;
    }

    @Override
    public void selectData() {
        List<Promotion> promotions = getDatabase().selectAll(Promotion.class);
        promotions.forEach(promotion -> promotionMap.put(promotion.getPromotionID(), promotion));
        long playerPromotionID = gameSettingManager.getGameSettingLong(PLAYER_PROMOTION);
        playerPromotion = promotions.stream()
                .filter(promotion -> playerPromotionID == promotion.getPromotionID())
                .findFirst()
                .orElseThrow();
    }

    public void setPlayerPromotion(Promotion promotion) {
        playerPromotion = promotion;
        gameSettingManager.setGameSettingLong(PLAYER_PROMOTION, promotion.getPromotionID());
    }

    public Promotion getPromotion(Long promotionID) {
        return promotionMap.get(promotionID);
    }

    public List<Promotion> getPromotions() {
        return new ArrayList<>(promotionMap.values());
    }

    public List<Promotion> getAiPromotions() {
        return new ArrayList<>(promotionMap.values()).stream()
                .filter(promotion -> !playerPromotion.equals(promotion))
                .collect(Collectors.toList());
    }

    public Promotion refreshPromotion(Promotion promotion) {
        return promotionMap.get(promotion.getPromotionID());
    }

    public List<Promotion> createPromotions(List<Promotion> promotions) {
        List<Promotion> saved = getDatabase().insertList(promotions);
        bankAccountManager.createBankAccounts(
                saved.stream()
                        .map(promotion -> BankAccount.builder().promotion(promotion).build())
                        .collect(Collectors.toList())
        );
        saved.forEach(promotion -> promotionMap.put(promotion.getPromotionID(), promotion));
        return saved;
    }

    public List<Promotion> updatePromotions(List<Promotion> promotions) {
        List<Promotion> saved = getDatabase().insertList(promotions);
        saved.forEach(promotion -> promotionMap.put(promotion.getPromotionID(), promotion));
        return saved;
    }

}
