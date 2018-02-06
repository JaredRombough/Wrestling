package wrestling.model;

import java.time.Month;

public class EventName {

    private String name;
    private Promotion promotion;
    private Month month;

    public EventName(String name, Promotion promotion, Month month) {
        this.name = name;
        this.promotion = promotion;
        this.month = month;
    }

    @Override
    public String toString() {
        return getName() + " " + getPromotion() + " " + getMonth();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the promotion
     */
    public Promotion getPromotion() {
        return promotion;
    }

    /**
     * @return the month
     */
    public Month getMonth() {
        return month;
    }

}
