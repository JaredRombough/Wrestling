package wrestling.model.manager;

import java.util.ArrayList;
import java.util.List;
import wrestling.model.Promotion;
import wrestling.model.financial.BankAccount;

public class PromotionManager {

    private final List<Promotion> promotions;
    private final List<BankAccount> bankAccounts;
    private Promotion playerPromotion;

    public PromotionManager() {
        promotions = new ArrayList();
        bankAccounts = new ArrayList();
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

    public void addBankAccount(BankAccount bankAccount) {
        this.bankAccounts.add(bankAccount);
    }

    public BankAccount getBankAccount(Promotion promotion) {
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
    public List<Promotion> getPromotions() {
        return promotions;
    }

}
