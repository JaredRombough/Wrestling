package wrestling.model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import wrestling.model.interfaces.iNewsItem;
import wrestling.model.modelView.PromotionView;

public class NewsItem implements iNewsItem {

    private final String summary;
    private final String headline;
    private final LocalDate date;
    private final List<PromotionView> promotions;

    public NewsItem(String headline, String summary, LocalDate date, PromotionView promotion) {
        this.summary = summary;
        this.headline = headline;
        this.date = date;
        this.promotions = Arrays.asList(promotion);
    }

    public String getSummary() {
        return summary;
    }

    @Override
    public String toString() {
        return headline;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<PromotionView> getPromotions() {
        return promotions;
    }
}
