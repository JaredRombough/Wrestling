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
    private GameSettingManager gameSettingManager;

    public PromotionManager(Database database, BankAccountManager bankAccountManager, GameSettingManager gameSettingManager) {
        super(database);
        promotions = new ArrayList();
        this.bankAccountManager = bankAccountManager;
        this.gameSettingManager = gameSettingManager;
    }

    @Override
    public void selectData() {
        promotions = getDatabase().selectAll(Promotion.class);
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

    public List<Promotion> createPromotions(List<Promotion> promotions) {
        List<Promotion> saved = getDatabase().insertList(promotions);
        bankAccountManager.createBankAccounts(
                saved.stream()
                        .map(promotion -> BankAccount.builder().promotion(promotion).build())
                        .collect(Collectors.toList())
        );
        this.promotions.addAll(saved);
        return saved;
    }

    public List<Promotion> updatePromotions(List<Promotion> promotions) {
        List<Promotion> saved = getDatabase().insertList(promotions);
        saved.addAll(
                this.promotions.stream()
                        .filter(promotion -> saved.stream().noneMatch(savedPromotion -> savedPromotion.getPromotionID() == promotion.getPromotionID()))
                        .collect(Collectors.toList())
        );
        this.promotions = saved;
        return saved;
    }

}
