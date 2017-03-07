package wrestling.model.financial;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BankAccount {

    public BankAccount() {
        funds = 0;
    }

    private Integer funds;

    private List<Transaction> transactions = new ArrayList<>();

    public int getTransactionTotal(char type, LocalDate startDate, LocalDate endDate) {

        int total = 0;

        List<Transaction> transactionSet = getTransactions(type, startDate, endDate);

        for (Transaction t : transactionSet) {
            total += t.getAmount();
        }

        return total;
    }

    public List<Transaction> getTransactions(char type, LocalDate startDate, LocalDate endDate) {

        List<Transaction> transactionSet = new ArrayList<>();

        for (Transaction t : transactions) {

            if (t.getType() == type && t.getDate().isAfter(startDate.minusDays(1)) && t.getDate().isBefore(endDate)) {
                transactionSet.add(t);
            }
        }

        return transactionSet;
    }

    private void addTransaction(int amount, char type, LocalDate date) {
        Transaction transaction = new Transaction(amount, type, date);
        transactions.add(transaction);
    }

    //for adding funds outside of the game economy
    public void addFunds(Integer income) {
        funds += income;
    }

    public void addFunds(Integer income, char type, LocalDate date) {
        funds += income;
        addTransaction(income, type, date);
    }

    public void removeFunds(Integer expense, char type, LocalDate date) {
        funds -= expense;
        addTransaction(expense, type, date);

    }

    public Integer getFunds() {
        return funds;
    }
}
