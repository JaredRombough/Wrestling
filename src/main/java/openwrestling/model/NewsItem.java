package openwrestling.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import openwrestling.model.interfaces.iNewsItem;
import openwrestling.model.modelView.PromotionView;
import openwrestling.model.modelView.WorkerView;

public class NewsItem implements iNewsItem {

    private final String summary;
    private final String headline;
    private final LocalDate date;
    private final List<PromotionView> promotions = new ArrayList();
    private final List<WorkerView> workers = new ArrayList();

    public NewsItem(String headline, String summary, LocalDate date, PromotionView promotion) {
        this.summary = summary;
        this.headline = headline;
        this.date = date;
        this.promotions.add(promotion);
    }

    public NewsItem(String headline, String summary, LocalDate date, PromotionView promotion, WorkerView worker) {
        this(headline, summary, date, promotion);
        this.workers.add(worker);
    }

    public NewsItem(String headline, String summary, LocalDate date, PromotionView promotion, List<WorkerView> workers) {
        this(headline, summary, date, promotion);
        this.workers.addAll(workers);
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
    public List<PromotionView> getPromotions() {
        return promotions;
    }

    /**
     * @return the workers
     */
    public List<WorkerView> getWorkers() {
        return workers;
    }
}
