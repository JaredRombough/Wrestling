package openwrestling.manager;

import openwrestling.database.Database;
import openwrestling.model.NewsItem;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.Injury;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.interfaces.iContract;
import openwrestling.model.utility.ModelUtils;
import org.apache.commons.collections4.CollectionUtils;
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

public class NewsManager extends GameObjectManager implements Serializable {

    private final Map<LocalDate, NewsItem> newsItemByDateMap = new HashMap();
    private final Map<Object, Map<LocalDate, List<NewsItem>>> newsItemBySegmentItemMap = new HashMap();
    private List<NewsItem> newsItems = new ArrayList<>();

    public NewsManager(Database database) {
        super(database);
    }

    @Override
    public void selectData() {
        newsItems = getDatabase().selectAll(NewsItem.class);
        newsItems.forEach(newsItem -> {
            if (CollectionUtils.isNotEmpty(newsItem.getPromotions())) {
                newsItem.getPromotions().forEach(promotion -> addSegmentItemNews(promotion, newsItem));
            }
            if (CollectionUtils.isNotEmpty(newsItem.getWorkers())) {
                newsItem.getWorkers().forEach(promotion -> addSegmentItemNews(promotion, newsItem));
            }
        });
    }


    /**
     * @return the newsItems
     */
    public List<NewsItem> getNewsItems() {
        return newsItems;
    }

    public void addWelcomeNewsItem(Promotion promotion) {
        addNews(NewsItem.builder()
                .headline("Welcome to Open Wrestling")
                .summary("Have fun!")
                .date(LocalDate.MIN)
                .promotions(List.of(promotion))
                .build());
    }

    public NewsItem getJobComplaintNewsItem(Worker worker, List<Worker> winners, Promotion promotion, LocalDate date) {
        List<Worker> workers = new ArrayList<>(winners);
        workers.add(worker);
        return NewsItem.builder()
                .headline(String.format("%s unhappy with loss", worker.getShortName()))
                .summary(String.format("%s is unhappy with %s after their loss to %s",
                        worker.getLongName(),
                        promotion,
                        ModelUtils.andItemsLongName(winners)))
                .date(date)
                .promotions(List.of(promotion))
                .workers(workers)
                .build();
    }

    public void addJobComplaintNewsItem(Worker worker, List<Worker> winners, Promotion promotion, LocalDate date) {
        List<Worker> workers = new ArrayList<>(winners);
        workers.add(worker);
        addNews(NewsItem.builder()
                .headline(String.format("%s unhappy with loss", worker.getShortName()))
                .summary(String.format("%s is unhappy with %s after their loss to %s",
                        worker.getLongName(),
                        promotion,
                        ModelUtils.andItemsLongName(winners)))
                .date(date)
                .promotions(List.of(promotion))
                .workers(workers)
                .build());
    }

    public NewsItem getMoraleNewsItem(iContract contract, long daysBetween, int penalty, LocalDate date) {
        return NewsItem.builder()
                .headline(String.format("%s loses morale", contract.getWorker().getShortName()))
                .summary(String.format("%s has not worked a show for %s in %d days, and loses %d morale.",
                        contract.getWorker().getLongName(),
                        contract.getPromotion().getName(),
                        daysBetween,
                        penalty))
                .date(date)
                .promotions(List.of(contract.getPromotion()))
                .workers(List.of(contract.getWorker()))
                .build();
    }

    //TODO list of expired contracts sorted by date
    public NewsItem getExpiringContractNewsItem(iContract contract, LocalDate date) {
        return NewsItem.builder()
                .headline(String.format("%s contract expires", contract.getWorker().getShortName()))
                .summary(String.format("%s is no longer under contract with %s.",
                        contract.getWorker().getLongName(),
                        contract.getPromotion().getName()))
                .date(date)
                .promotions(List.of(contract.getPromotion()))
                .workers(List.of(contract.getWorker()))
                .build();
    }

    //TODO list of new contracts sorted by date
    public NewsItem getNewContractNewsItem(iContract contract, LocalDate date) {
        return NewsItem.builder()
                .headline(String.format("%s signs %s", contract.getPromotion().getShortName(), contract.getWorker().getShortName()))
                .summary(String.format("%s has signed a new contract with %s. It will expire on %s",
                        contract.getWorker().getLongName(),
                        contract.getPromotion().getName(),
                        contract.getEndDate().toString()))
                .date(date)
                .promotions(List.of(contract.getPromotion()))
                .workers(List.of(contract.getWorker()))
                .build();
    }

    //TODO list of injuries by date
    public void addMatchInjuryNewsItem(Injury injury, Event event) {
        addNews(NewsItem.builder()
                .headline(String.format("%s injured", injury.getWorker().getLongName()))
                .summary(String.format("%s was injured in a match at %s on %s. They are expected to be out until %s.",
                        injury.getWorker().getLongName(),
                        event.toString(),
                        injury.getStartDate().toString(),
                        injury.getExpiryDate().toString()))
                .date(injury.getStartDate())
                .promotions(List.of(injury.getPromotion()))
                .workers(List.of(injury.getWorker()))
                .build());
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
        addNews(NewsItem.builder()
                .headline(headline)
                .summary(body)
                .date(injury.getStartDate())
                .promotions(List.of(injury.getPromotion()))
                .workers(List.of(injury.getWorker()))
                .build());
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

    public void addNewsItems(List<NewsItem> newsItemsToAdd) {
        List<NewsItem> inserted = getDatabase().insertList(newsItemsToAdd);
        inserted.forEach(newsItem -> {
            if (CollectionUtils.isNotEmpty(newsItem.getPromotions())) {
                newsItem.getPromotions().forEach(promotion -> addSegmentItemNews(promotion, newsItem));
            }
            if (CollectionUtils.isNotEmpty(newsItem.getWorkers())) {
                newsItem.getWorkers().forEach(promotion -> addSegmentItemNews(promotion, newsItem));
            }
        });
        newsItems.addAll(inserted);
    }

    private void addNews(NewsItem newsItem) {
        NewsItem inserted = getDatabase().insertList(List.of(newsItem)).get(0);
        if (CollectionUtils.isNotEmpty(newsItem.getPromotions())) {
            newsItem.getPromotions().forEach(promotion -> addSegmentItemNews(promotion, inserted));
        }
        if (CollectionUtils.isNotEmpty(newsItem.getWorkers())) {
            newsItem.getWorkers().forEach(promotion -> addSegmentItemNews(promotion, inserted));
        }
        newsItems.addAll(List.of(inserted));
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
