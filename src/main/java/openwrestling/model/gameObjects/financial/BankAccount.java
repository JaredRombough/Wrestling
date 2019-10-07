package openwrestling.model.gameObjects.financial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.gameObjects.GameObject;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.segmentEnum.TransactionType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BankAccount extends GameObject {

    private long bankAccountID;
    private Promotion promotion;
    @Builder.Default
    private int funds = 1000000;
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    public BankAccount(Promotion promotion) {
        this.promotion = promotion;
        funds = 0;
    }

    public int getTransactionTotal(TransactionType type, LocalDate startDate) {

        int total = 0;

        List<Transaction> transactionSet = getTransactions(type, startDate);

        for (Transaction t : transactionSet) {
            total += t.getAmount();
        }

        return total;
    }

    public int getMonthlyNet(LocalDate startDate) {
        int total = 0;

        for (Transaction transaction : transactions) {
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

    public List<Transaction> getTransactions(TransactionType type, LocalDate startDate) {

        List<Transaction> transactionSet = new ArrayList<>();

        for (Transaction transaction : transactions) {

            if (transaction.getType().equals(type) && sameMonth(transaction.getDate(), startDate)) {
                transactionSet.add(transaction);
            }
        }

        return transactionSet;
    }

    private void addTransaction(int amount, TransactionType type, LocalDate date) {
        Transaction transaction = Transaction.builder()
                .amount(amount)
                .type(type)
                .date(date)
                .build();
        transactions.add(transaction);
    }

    public void setFunds(int income) {
        funds = income;
    }

    public void setFunds(int income, TransactionType type, LocalDate date) {
        funds += income;
        addTransaction(income, type, date);
    }

    public void removeFunds(int expense, TransactionType type, LocalDate date) {
        funds -= expense;
        addTransaction(expense, type, date);

    }

    private boolean sameMonth(LocalDate transactionDate, LocalDate date) {
        return transactionDate.isEqual(date.withDayOfMonth(1))
                || transactionDate.isAfter(date.withDayOfMonth(1))
                && transactionDate.isBefore(date.plusMonths(1).withDayOfMonth(1));
    }
}
