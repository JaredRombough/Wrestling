package openwrestling.model.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import openwrestling.model.financial.BankAccount;
import openwrestling.model.modelView.PromotionView;

public class PromotionManager implements Serializable {

    private final List<PromotionView> promotions;
    private final List<BankAccount> bankAccounts;
    private PromotionView playerPromotion;

    public PromotionManager() {
        promotions = new ArrayList();
        bankAccounts = new ArrayList();
    }

    public List<PromotionView> aiPromotions() {
        List<PromotionView> aiPromotions = new ArrayList();
        for (PromotionView promotion : promotions) {
            if (!promotion.equals(playerPromotion)) {
                aiPromotions.add(promotion);
            }
        }
        return aiPromotions;
    }

    public void setPlayerPromotion(PromotionView promotion) {
        playerPromotion = promotion;
    }

    public PromotionView playerPromotion() {
        return playerPromotion;
    }

    public void addPromotions(List<PromotionView> promotions) {
        for (PromotionView promotion : promotions) {
            this.promotions.add(promotion);
        }
    }

    public void addPromotion(PromotionView promotion) {
        promotions.add(promotion);
    }

    public void addBankAccount(BankAccount bankAccount) {
        this.bankAccounts.add(bankAccount);
    }

    public BankAccount getBankAccount(PromotionView promotion) {
        for (BankAccount account : bankAccounts) {
            if (account.getPromotion().equals(promotion)) {
                return account;
            }
        }
        return null;
    }

    /**
     * @return the promotions
     */
    public List<PromotionView> getPromotions() {
        return promotions;
    }

}
