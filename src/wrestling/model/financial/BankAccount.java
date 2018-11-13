package wrestling.model.financial;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.modelView.PromotionView;
import wrestling.model.segmentEnum.TransactionType;

public class BankAccount {
    
    private final PromotionView promotion;
    private int funds;
    
    private List<Transaction> transactions = new ArrayList<>();
    
    public BankAccount(PromotionView promotion) {
        this.promotion = promotion;
        funds = 0;
    }
    
    public int getTransactionTotal(TransactionType type, LocalDate startDate, LocalDate endDate) {
        
        int total = 0;
        
        List<Transaction> transactionSet = getTransactions(type, startDate, endDate);
        
        for (Transaction t : transactionSet) {
            total += t.getAmount();
        }
        
        return total;
    }
    
    public List<Transaction> getTransactions(TransactionType type, LocalDate startDate, LocalDate endDate) {
        
        List<Transaction> transactionSet = new ArrayList<>();
        
        for (Transaction transaction : transactions) {
            
            if (transaction.getType().equals(type) && transaction.getDate().isAfter(startDate.minusDays(1))
                    && (transaction.getDate().isBefore(endDate) || transaction.getDate().isEqual(endDate))) {
                transactionSet.add(transaction);
            }
        }
        
        return transactionSet;
    }
    
    private void addTransaction(int amount, TransactionType type, LocalDate date) {
        Transaction transaction = new Transaction(amount, type, date);
        transactions.add(transaction);
    }

    //for adding funds outside of the game economy
    public void addFunds(int income) {
        funds += income;
    }
    
    public void addFunds(int income, TransactionType type, LocalDate date) {
        funds += income;
        addTransaction(income, type, date);
    }
    
    public void removeFunds(int expense, TransactionType type, LocalDate date) {
        funds -= expense;
        addTransaction(expense, type, date);
        
    }
    
    public Integer getFunds() {
        return funds;
    }

    /**
     * @return the promotion
     */
    public PromotionView getPromotion() {
        return promotion;
    }
}
