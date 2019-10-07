package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.financial.BankAccount;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class BankAccountManager implements Serializable {

    private final List<BankAccount> bankAccounts;

    public BankAccountManager() {
        bankAccounts = new ArrayList();
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

    public List<BankAccount> createBankAccounts(List<BankAccount> bankAccounts) {
        List<BankAccount> saved = Database.insertList(bankAccounts);
        this.bankAccounts.addAll(saved);
        return saved;
    }
}
