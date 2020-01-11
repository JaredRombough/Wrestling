package openwrestling.model.manager;

import lombok.NoArgsConstructor;
import openwrestling.model.gameObjects.Injury;
import openwrestling.model.NewsItem;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.interfaces.iContract;
import openwrestling.model.utility.ModelUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static openwrestling.model.constants.Words.ACTIVITIES;
import static openwrestling.model.constants.Words.BODY_PARTS;

@NoArgsConstructor
public class NewsManager implements Serializable {

    private final List<NewsItem> newsItems = new ArrayList<>();
    private final Map<LocalDate, NewsItem> newsItemByDateMap = new HashMap();
    private final Map<Object, Map<LocalDate, List<NewsItem>>> newsItemBySegmentItemMap = new HashMap();

    /**
     * @return the newsItems
     */
    public List<NewsItem> getNewsItems() {
        return newsItems;
    }

    public void addWelcomeNewsItem(Promotion promotion) {
        NewsItem newsItem = new NewsItem(
                "Welcome to Open Wrestling",
                "Have fun!",
                LocalDate.MIN,
                promotion);
        addNews(newsItem);
    }

    public void addJobComplaintNewsItem(Worker worker, List<Worker> winners, Promotion promotion, LocalDate date) {
        List<Worker> workers = new ArrayList<>(winners);
        workers.add(worker);
        NewsItem newsItem = new NewsItem(
                String.format("%s unhappy with loss", worker.getShortName()),
                String.format("%s is unhappy with %s after their loss to %s",
                        worker.getLongName(),
                        promotion,
                        ModelUtils.andItemsLongName(winners)),
                date,
                promotion,
                workers);
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
                contract.getPromotion(),
                contract.getWorker()
        );
        addNews(newsItem);
    }

    public void addTrainingNewsItem(Worker worker, StaffMember trainer, Promotion promotion, String stat, LocalDate date) {
        NewsItem newsItem = new NewsItem(
                String.format("%s training", worker.getLongName()),
                String.format("%s increased %s working with %s trainer %s.",
                        worker.toString(), stat, promotion.getShortName(), trainer.toString()),
                date,
                promotion,
                worker);
        addNews(newsItem);
    }

    public void addMatchInjuryNewsItem(Injury injury, Event event) {
        NewsItem newsItem = new NewsItem(String.format("%s injured", injury.getWorker().getLongName()),
                String.format("%s was injured in a match at %s on %s. They are expected to be out until %s.",
                        injury.getWorker().getLongName(),
                        event.toString(),
                        injury.getStartDate().toString(),
                        injury.getExpiryDate().toString()),
                injury.getStartDate(),
                injury.getPromotion(),
                injury.getWorker());
        addNews(newsItem);
    }

    public void addRandomInjuryNewsItem(Injury injury) {
        int index1 = RandomUtils.nextInt(0, ACTIVITIES.size());
        int index2 = RandomUtils.nextInt(0, BODY_PARTS.size());
        String headline = String.format("%s injured", injury.getWorker().getName());
        String body = String.format("%s injured their %s while %s today. They are expected to be out until %s.",
                injury.getWorker().getLongName(),
                BODY_PARTS.get(index2),
                ACTIVITIES.get(index1).toLowerCase(),
                injury.getExpiryDate(),
                injury.getWorker());
        NewsItem newsItem = new NewsItem(headline, body, injury.getStartDate(), injury.getPromotion());
        addNews(newsItem);
    }

    public List<NewsItem> getNews(Object key, LocalDate startDate, LocalDate endDate) {
        List<NewsItem> items = new ArrayList<>();
        if (newsItemBySegmentItemMap.containsKey(key)) {
            Map<LocalDate, List<NewsItem>> subMap = newsItemBySegmentItemMap.get(key);
            for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
                if (subMap.containsKey(date)) {
                    items.addAll(subMap.get(date));
                }
            }
        }
        return items;
    }

    private void addNews(NewsItem newsItem) {
        newsItems.add(newsItem);
        newsItem.getPromotions().forEach(promotion -> addSegmentItemNews(promotion, newsItem));
        newsItem.getWorkers().forEach(promotion -> addSegmentItemNews(promotion, newsItem));
    }

    private void addSegmentItemNews(Object key, NewsItem newsItem) {
        if (newsItemBySegmentItemMap.containsKey(key)) {
            if (newsItemBySegmentItemMap.get(key).containsKey(newsItem.getDate())) {
                newsItemBySegmentItemMap.get(key).get(newsItem.getDate()).add(newsItem);
            } else {
                newsItemBySegmentItemMap.get(key).put(newsItem.getDate(), new ArrayList<>(Arrays.asList(newsItem)));
            }
        } else {
            Map<LocalDate, List<NewsItem>> map = new HashMap<>();
            List<NewsItem> items = new ArrayList<>(Arrays.asList(newsItem));
            map.put(newsItem.getDate(), items);
            newsItemBySegmentItemMap.put(key, map);
        }
    }
}
