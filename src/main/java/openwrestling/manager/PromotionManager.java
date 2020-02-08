package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.financial.BankAccount;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static openwrestling.model.constants.SettingKeys.PLAYER_PROMOTION;

@Getter
public class PromotionManager extends GameObjectManager implements Serializable {

    private List<Promotion> promotions;
    private Promotion playerPromotion;
    private BankAccountManager bankAccountManager;

    public PromotionManager(BankAccountManager bankAccountManager) {
        promotions = new ArrayList();
        this.bankAccountManager = bankAccountManager;
    }

    @Override
    public void selectData() {
        promotions = Database.selectAll(Promotion.class);
        long playerPromotionID = GameSettingManager.getGameSettingLong(PLAYER_PROMOTION);
        playerPromotion = promotions.stream()
                .filter(promotion -> playerPromotionID == promotion.getPromotionID())
                .findFirst()
                .orElseThrow();
    }

    public void setPlayerPromotion(Promotion promotion) {
        playerPromotion = promotion;
        GameSettingManager.setGameSettingLong(PLAYER_PROMOTION, promotion.getPromotionID());
    }

    public List<Promotion> createPromotions(List<Promotion> promotions) {
        List<Promotion> saved = Database.insertList(promotions);
        bankAccountManager.createBankAccounts(
                saved.stream()
                        .map(promotion -> BankAccount.builder().promotion(promotion).build())
                        .collect(Collectors.toList())
        );
        this.promotions.addAll(saved);
        return saved;
    }

    public List<Promotion> updatePromotions(List<Promotion> promotions) {
        List<Promotion> saved = Database.insertList(promotions);
        saved.addAll(
                this.promotions.stream()
                        .filter(promotion -> saved.stream().noneMatch(savedPromotion -> savedPromotion.getPromotionID() == promotion.getPromotionID()))
                        .collect(Collectors.toList())
        );
        this.promotions = saved;
        return saved;
    }

}
