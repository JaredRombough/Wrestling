package wrestling.model;

import java.time.LocalDate;
import wrestling.model.interfaces.iNewsItem;
import wrestling.model.modelView.PromotionView;

public class NewsItem implements iNewsItem {

    private final String summary;
    private final String headline;
    private final LocalDate date;
    private final PromotionView promotion;

    public NewsItem(String headline, String summary, LocalDate date, PromotionView promotion) {
        this.summary = summary;
        this.headline = headline;
        this.date = date;
        this.promotion = promotion;
    }

    @Override
    public String getSummary() {
        return summary;
    }

    @Override
    public String toString() {
        return headline;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public PromotionView getPromotion() {
        return promotion;
    }
}
