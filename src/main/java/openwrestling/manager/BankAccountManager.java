package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.financial.BankAccount;
import openwrestling.model.gameObjects.financial.Transaction;
import openwrestling.model.segmentEnum.TransactionType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class BankAccountManager extends GameObjectManager implements Serializable {

    private List<BankAccount> bankAccounts;
    private Map<Long, List<Transaction>> promotionTransactionMap = new HashMap<>();

    public BankAccountManager(Database database) {
        super(database);
        bankAccounts = new ArrayList();
    }

    @Override
    public void selectData() {
        List<Transaction> transactions = getDatabase().selectAll(Transaction.class);
        transactions.forEach(this::putTransactionInMap);
        bankAccounts = getDatabase().selectAll(BankAccount.class);
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

    public List<Transaction> getTransactions(Promotion promotion) {
        if (!promotionTransactionMap.containsKey(promotion.getPromotionID())) {
            return new ArrayList<>();
        }
        return promotionTransactionMap.get(promotion.getPromotionID());
    }

    public List<BankAccount> createBankAccounts(List<BankAccount> bankAccounts) {
        List<BankAccount> saved = getDatabase().insertList(bankAccounts);
        this.bankAccounts.addAll(saved);
        return saved;
    }

    public void insertTransactions(List<Transaction> transactions) {
        Map<Promotion, BankAccount> bankAccountMap = new HashMap<>();
        transactions.forEach(transaction -> {
            long amount = transaction.getType().isExpense() ? -transaction.getAmount() : transaction.getAmount();
            if (bankAccountMap.containsKey(transaction.getPromotion())) {
                BankAccount bankAccount = bankAccountMap.get(transaction.getPromotion());
                bankAccount.setFunds(bankAccount.getFunds() + amount);
            } else {
                BankAccount bankAccount = getBankAccount(transaction.getPromotion());
                bankAccount.setFunds(bankAccount.getFunds() + amount);
                bankAccountMap.put(transaction.getPromotion(), bankAccount);
            }
        });

        List<Transaction> saved = getDatabase().insertList(transactions);
        saved.forEach(this::putTransactionInMap);
        getDatabase().insertList(new ArrayList<>(bankAccountMap.values()));

        bankAccounts = getDatabase().selectAll(BankAccount.class);
    }

    public int getTransactionTotal(Promotion promotion, TransactionType type, LocalDate startDate) {

        int total = 0;

        List<Transaction> transactionSet = getTransactions(promotion, type, startDate);

        for (Transaction t : transactionSet) {
            total += t.getAmount();
        }

        return total;
    }


    public List<Transaction> getTransactions(Promotion promotion, TransactionType type, LocalDate startDate) {
        if (!promotionTransactionMap.containsKey(promotion.getPromotionID())) {
            return List.of();
        }

        List<Transaction> transactionSet = new ArrayList<>();

        for (Transaction transaction : promotionTransactionMap.get(promotion.getPromotionID())) {

            if (transaction.getType().equals(type) && sameMonth(transaction.getDate(), startDate)) {
                transactionSet.add(transaction);
            }
        }

        return transactionSet;
    }


    public int getMonthlyNet(Promotion promotion, LocalDate startDate) {
        if (!promotionTransactionMap.containsKey(promotion.getPromotionID())) {
            return 0;
        }
        int total = 0;

        for (Transaction transaction : promotionTransactionMap.get(promotion.getPromotionID())) {
            if (sameMonth(transaction.getDate(), startDate)) {
                if (transaction.getType().isExpense()) {
                    total -= transaction.getAmount();
                } else {
                    total += transaction.getAmount();
                }
            }
        }
        return total;
    }

    public void removeFunds(Promotion promotion, int expense, TransactionType type, LocalDate date) {
        addTransaction(promotion, expense, type, date);
    }

    private void addTransaction(Promotion promotion, int amount, TransactionType type, LocalDate date) {
        Transaction transaction = Transaction.builder()
                .amount(amount)
                .type(type)
                .date(date)
                .promotion(promotion)
                .build();
        insertTransactions(List.of(transaction));
    }


    private boolean sameMonth(LocalDate transactionDate, LocalDate date) {
        return transactionDate.isEqual(date.withDayOfMonth(1))
                || transactionDate.isAfter(date.withDayOfMonth(1))
                && transactionDate.isBefore(date.plusMonths(1).withDayOfMonth(1));
    }

    private void putTransactionInMap(Transaction transaction) {
        if (!promotionTransactionMap.containsKey(transaction.getPromotion().getPromotionID())) {
            promotionTransactionMap.put(transaction.getPromotion().getPromotionID(), new ArrayList<>());
        }
        promotionTransactionMap.get(transaction.getPromotion().getPromotionID()).add(transaction);
    }

}
