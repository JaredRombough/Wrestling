package wrestling.model.manager;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.RandomUtils;
import wrestling.model.Injury;
import wrestling.model.NewsItem;
import static wrestling.model.constants.GameConstants.MAX_INJURY_DAYS;
import static wrestling.model.constants.Words.ACTIVITIES;
import static wrestling.model.constants.Words.BODY_PARTS;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.SegmentView;
import wrestling.model.modelView.WorkerView;

public class InjuryManager implements Serializable {

    private final NewsManager newsManager;
    private final List<Injury> injuries = new ArrayList();

    public InjuryManager(NewsManager newsManager) {
        this.newsManager = newsManager;
    }

    public void dailyUpdate(LocalDate date, PromotionView promotion) {
        for (Injury injury : injuries) {
            if (injury.getPromotion().equals(promotion) && injury.getExpiryDate().equals(date)) {
                WorkerView worker = injury.getWorkerView();
                worker.setInjury(null);
            }
        }
        injuries.removeIf(injury -> injury.getExpiryDate().equals(date));

        if (randomInjury()) {
            int workerIndex = RandomUtils.nextInt(0, promotion.getFullRoster().size());
            createRandomInjury(promotion.getFullRoster().get(workerIndex), date, promotion);
        }
    }

    private boolean randomInjury() {
        return RandomUtils.nextInt(0, 1000) == 1;
    }

    /**
     * @return the injuries
     */
    public List<Injury> getInjuries() {
        return injuries;
    }

    public void createInjury(LocalDate startDate, int duration, WorkerView worker, SegmentView segmentView) {
        Injury injury = new Injury(startDate, startDate.plusDays(duration), worker, segmentView.getPromotion());
        addInjury(injury);
        NewsItem newsItem = new NewsItem(String.format("%s injured", injury.getWorkerView().getLongName()),
                String.format("%s was injured in a match at %s on %s. They are expected to be out until %s.",
                        injury.getWorkerView().getLongName(),
                        segmentView.getEventView().toString(),
                        injury.getStartDate().toString(),
                        injury.getExpiryDate().toString()),
                injury.getStartDate(),
                injury.getPromotion());
        newsManager.addNews(newsItem);
    }

    public void createRandomInjury(WorkerView worker, LocalDate date, PromotionView promotion) {

        int index1 = RandomUtils.nextInt(0, ACTIVITIES.size());
        int index2 = RandomUtils.nextInt(0, BODY_PARTS.size());

        int injuryDays = RandomUtils.nextInt(0, MAX_INJURY_DAYS);

        String headline = String.format("%s injured", worker.getName());

        Injury injury = new Injury(date, date.plusDays(injuryDays), worker, promotion);

        String body = String.format("%s injured their %s while %s today. They are expected to be out until %s.",
                worker.getLongName(),
                BODY_PARTS.get(index2),
                ACTIVITIES.get(index1).toLowerCase(),
                injury.getExpiryDate());

        newsManager.addNews(new NewsItem(headline, body, date, promotion));
        addInjury(injury);
    }

    private void addInjury(Injury injury) {
        injuries.add(injury);
        injury.getWorkerView().setInjury(injury);
    }

}
