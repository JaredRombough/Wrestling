package openwrestling.model.financial;

import java.time.LocalDate;
import openwrestling.model.segmentEnum.TransactionType;

public class Transaction {

    private final int amount;
    private final TransactionType type;
    private final LocalDate date;

    public Transaction(int amount, TransactionType type, LocalDate date) {
        this.amount = amount;
        this.type = type;
        this.date = date;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @return the type
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }
}
