package openwrestling.model.manager;

import openwrestling.file.Database;
import openwrestling.model.financial.BankAccount;
import openwrestling.model.gameObjects.Promotion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

}
