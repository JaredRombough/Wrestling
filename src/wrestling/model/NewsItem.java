package wrestling.model;

import java.time.LocalDate;
import wrestling.model.interfaces.iNewsItem;
import wrestling.model.modelView.PromotionView;

public class NewsItem implements iNewsItem {

    private String summary;
    private String headline;
    private LocalDate date;
    private PromotionView promotion;

    public NewsItem(String headline, String summary) {
        this.summary = summary;
        this.headline = headline;
    }

    @Override
    public String getSummary() {
        return summary;
    }

    @Override
    public String toString() {
        return headline;
    }

    /**
     * @param summary the summary to set
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @param headline the headline to set
     */
    public void setHeadline(String headline) {
        this.headline = headline;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public PromotionView getPromotion() {
        return promotion;
    }

    /**
     * @param date the date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * @param promotion the promotion to set
     */
    public void setPromotion(PromotionView promotion) {
        this.promotion = promotion;
    }

}
