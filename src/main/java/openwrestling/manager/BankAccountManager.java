package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.financial.BankAccount;
import openwrestling.model.gameObjects.financial.Transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class BankAccountManager extends GameObjectManager implements Serializable {

    private List<BankAccount> bankAccounts;

    @Override
    public void selectData() {
        bankAccounts = Database.selectAll(BankAccount.class);
    }

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

    private List<Transaction> createTransactions(List<Transaction> transactions) {
        List<Transaction> toInsert = Database.insertList(transactions);
        this.bankAccounts = Database.selectAll(BankAccount.class);
        return toInsert;
    }

    public List<BankAccount> updateBankAccounts(List<BankAccount> bankAccounts) {
        List<Transaction> newTransactions = bankAccounts.stream()
                .flatMap(bankAccount -> bankAccount.getTransactions().stream())
                .filter(transaction -> transaction.getTransactionID() == 0)
                .collect(Collectors.toList());
        List<Transaction> savedTransactions = Database.insertList(newTransactions);
        List<BankAccount> savedBankAccounts = Database.insertList(bankAccounts);
        this.bankAccounts = Database.selectAll(BankAccount.class);
        return savedBankAccounts;
    }

    public List<Transaction> insertTransactions(List<Transaction> transactions) {
        Map<Promotion, BankAccount> bankAccountMap = new HashMap<>();
        transactions.forEach(transaction -> {
            if (bankAccountMap.containsKey(transaction.getPromotion())) {
                BankAccount bankAccount = bankAccountMap.get(transaction.getPromotion());
                bankAccount.setFunds(bankAccount.getFunds() + transaction.getAmount());
            } else {
                BankAccount bankAccount = getBankAccount(transaction.getPromotion());
                bankAccount.setFunds(bankAccount.getFunds() + transaction.getAmount());
                bankAccountMap.put(transaction.getPromotion(), bankAccount);
            }
        });

        Database.insertList(transactions);
        Database.insertList(new ArrayList<>(bankAccountMap.values()));

        bankAccounts = Database.selectAll(BankAccount.class);

        return List.of();
    }
}
