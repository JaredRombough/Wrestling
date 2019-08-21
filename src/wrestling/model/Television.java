package wrestling.model;

import java.time.DayOfWeek;
import wrestling.model.modelView.PromotionView;

public class Television {

    private DayOfWeek day;
    private String name;
    private PromotionView promotion;
    private int duration;

    /**
     * @return the day
     */
    public DayOfWeek getDay() {
        return day;
    }

    /**
     * @param day the day to set
     */
    public void setDay(DayOfWeek day) {
        this.day = day;
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

    /**
     * @return the promotion
     */
    public PromotionView getPromotion() {
        return promotion;
    }

    /**
     * @param promotion the promotion to set
     */
    public void setPromotion(PromotionView promotion) {
        this.promotion = promotion;
    }

    /**
     * @return the duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

}
