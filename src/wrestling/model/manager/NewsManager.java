package wrestling.model.manager;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.RandomUtils;
import wrestling.model.Injury;
import wrestling.model.NewsItem;
import static wrestling.model.constants.Words.ACTIVITIES;
import static wrestling.model.constants.Words.BODY_PARTS;
import wrestling.model.interfaces.iContract;
import wrestling.model.modelView.EventView;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.utility.ModelUtils;

public class NewsManager implements Serializable {

    private final List<NewsItem> newsItems = new ArrayList<>();

    /**
     * @return the newsItems
     */
    public List<NewsItem> getNewsItems() {
        return newsItems;
    }

    public void addWelcomeNewsItem(PromotionView promotion) {
        NewsItem newsItem = new NewsItem(
                "Welcome to Open Wrestling",
                "Have fun!",
                LocalDate.MIN,
                promotion);
        addNews(newsItem);
    }

    public void addJobComplaintNewsItem(WorkerView worker, List<WorkerView> winners, PromotionView promotion, LocalDate date) {
        NewsItem newsItem = new NewsItem(
                String.format("%s unhappy with loss", worker.getShortName()),
                String.format("%s is unhappy with %s after their loss to %s",
                        worker.getLongName(),
                        promotion,
                        ModelUtils.andItemsLongName(winners)),
                date,
                promotion);
        addNews(newsItem);
    }

    public void addMoraleNewsItem(iContract contract, long daysBetween, int penalty, LocalDate date) {
        NewsItem newsItem = new NewsItem(
                String.format("%s loses morale", contract.getWorker().getShortName()),
                String.format("%s has not worked a show for %s in %d days, and loses %d morale.",
                        contract.getWorker().getLongName(),
                        contract.getPromotion().getName(),
                        daysBetween,
                        penalty),
                date,
                contract.getPromotion()
        );
        addNews(newsItem);
    }

    public void addTrainingNewsItem(WorkerView worker, StaffView trainer, PromotionView promotion, String stat, LocalDate date) {
        NewsItem newsItem = new NewsItem(
                String.format("%s training", worker.getLongName()),
                String.format("%s increased %s working with %s trainer %s.",
                        worker.toString(), stat, promotion.getShortName(), trainer.toString()),
                date,
                promotion);
        addNews(newsItem);
    }

    public void addMatchInjuryNewsItem(Injury injury, EventView event) {
        NewsItem newsItem = new NewsItem(String.format("%s injured", injury.getWorkerView().getLongName()),
                String.format("%s was injured in a match at %s on %s. They are expected to be out until %s.",
                        injury.getWorkerView().getLongName(),
                        event.toString(),
                        injury.getStartDate().toString(),
                        injury.getExpiryDate().toString()),
                injury.getStartDate(),
                injury.getPromotion());
        addNews(newsItem);
    }

    public void addRandomInjuryNewsItem(Injury injury) {
        int index1 = RandomUtils.nextInt(0, ACTIVITIES.size());
        int index2 = RandomUtils.nextInt(0, BODY_PARTS.size());
        String headline = String.format("%s injured", injury.getWorkerView().getName());
        String body = String.format("%s injured their %s while %s today. They are expected to be out until %s.",
                injury.getWorkerView().getLongName(),
                BODY_PARTS.get(index2),
                ACTIVITIES.get(index1).toLowerCase(),
                injury.getExpiryDate());
        NewsItem newsItem = new NewsItem(headline, body, injury.getStartDate(), injury.getPromotion());
        addNews(newsItem);
    }

    private void addNews(NewsItem newsItem) {
        newsItems.add(newsItem);
    }
}
