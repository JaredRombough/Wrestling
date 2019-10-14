package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.financial.BankAccount;
import openwrestling.model.gameObjects.financial.Transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BankAccountManager implements Serializable {

    private List<BankAccount> bankAccounts;

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
        List<BankAccount> saved = Database.insertOrUpdateList(bankAccounts);
        this.bankAccounts.addAll(saved);
        return saved;
    }

    private List<Transaction> createTransactions(List<Transaction> transactions) {
        List<Transaction> toInsert = Database.insertOrUpdateList(transactions);
        this.bankAccounts = Database.selectAll(BankAccount.class);
        return toInsert;
    }

    public List<BankAccount> updateBankAccounts(List<BankAccount> bankAccounts) {
        List<Transaction> newTransactions = bankAccounts.stream()
                .flatMap(bankAccount -> bankAccount.getTransactions().stream())
                .filter(transaction -> transaction.getTransactionID() == 0)
                .collect(Collectors.toList());
        List<Transaction> savedTransactions = Database.insertOrUpdateList(newTransactions);
        List<BankAccount> savedBankAccounts = Database.insertOrUpdateList(bankAccounts);
        this.bankAccounts = Database.selectAll(BankAccount.class);
        return savedBankAccounts;
    }
}
