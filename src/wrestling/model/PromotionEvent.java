package wrestling.model;

import java.time.LocalDate;

public class PromotionEvent {

    private final LocalDate localDate;

    private final Promotion promotion;

    private String name;

    public PromotionEvent(LocalDate localDate, Promotion promotion) {
        this.localDate = localDate;
        this.promotion = promotion;
    }

    /**
     * @return the localDate
     */
    public LocalDate getLocalDate() {
        return localDate;
    }

    /**
     * @return the promotion
     */
    public Promotion getPromotion() {
        return promotion;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
