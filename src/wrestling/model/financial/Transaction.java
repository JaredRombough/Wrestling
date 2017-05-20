package wrestling.model.financial;

import java.time.LocalDate;

public class Transaction {


    private final int amount;
    private final char type;
    private final LocalDate date;
    public Transaction(int amount, char type, LocalDate date) {
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
    public char getType() {
        return type;
    }

    /**
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }
}
