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
    private Map<Long, List<Transaction>> transactionMap = new HashMap<>();

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

    public List<Transaction> getTransactions(Promotion promotion) {
        if (!transactionMap.containsKey(promotion.getPromotionID())) {
            return new ArrayList<>();
        }
        return transactionMap.get(promotion.getPromotionID());
    }

    public List<BankAccount> createBankAccounts(List<BankAccount> bankAccounts) {
        List<BankAccount> saved = Database.insertList(bankAccounts);
        this.bankAccounts.addAll(saved);
        return saved;
    }

    public List<Transaction> insertTransactions(List<Transaction> transactions) {
        Map<Promotion, BankAccount> bankAccountMap = new HashMap<>();
        transactions.forEach(transaction -> {
            if(TransactionType.WORKER.equals(transaction.getType())) {
                int i = 0;
            }
            if (bankAccountMap.containsKey(transaction.getPromotion())) {
                BankAccount bankAccount = bankAccountMap.get(transaction.getPromotion());
                bankAccount.setFunds(bankAccount.getFunds() + transaction.getAmount());
            } else {
                BankAccount bankAccount = getBankAccount(transaction.getPromotion());
                bankAccount.setFunds(bankAccount.getFunds() + transaction.getAmount());
                bankAccountMap.put(transaction.getPromotion(), bankAccount);
            }
        });

        List<Transaction> saved = Database.insertList(transactions);
        saved.forEach(this::putTransactionInMap);
        Database.insertList(new ArrayList<>(bankAccountMap.values()));

        bankAccounts = Database.selectAll(BankAccount.class);

        return List.of();
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
        if (!transactionMap.containsKey(promotion.getPromotionID())) {
            return List.of();
        }

        List<Transaction> transactionSet = new ArrayList<>();

        for (Transaction transaction : transactionMap.get(promotion.getPromotionID())) {

            if (transaction.getType().equals(type) && sameMonth(transaction.getDate(), startDate)) {
                transactionSet.add(transaction);
            }
        }

        return transactionSet;
    }


    public int getMonthlyNet(Promotion promotion, LocalDate startDate) {
        if (!transactionMap.containsKey(promotion.getPromotionID())) {
            return 0;
        }
        int total = 0;

        for (Transaction transaction : transactionMap.get(promotion.getPromotionID())) {
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
        if (!transactionMap.containsKey(transaction.getPromotion().getPromotionID())) {
            transactionMap.put(transaction.getPromotion().getPromotionID(), new ArrayList<>());
        }
        transactionMap.get(transaction.getPromotion().getPromotionID()).add(transaction);
    }

}
