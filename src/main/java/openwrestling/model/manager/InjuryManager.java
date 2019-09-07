package openwrestling.model.manager;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import openwrestling.manager.WorkerManager;
import org.apache.commons.lang3.RandomUtils;
import openwrestling.model.Injury;

import static openwrestling.model.constants.GameConstants.MAX_INJURY_DAYS;

import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.modelView.SegmentView;
import openwrestling.model.gameObjects.Worker;

public class InjuryManager implements Serializable {

    private final NewsManager newsManager;
    private final WorkerManager workerManager;
    private final List<Injury> injuries = new ArrayList();

    public InjuryManager(NewsManager newsManager, WorkerManager workerManager) {
        this.newsManager = newsManager;
        this.workerManager = workerManager;
    }

    public void dailyUpdate(LocalDate date, Promotion promotion) {
        for (Injury injury : injuries) {
            if (injury.getPromotion().equals(promotion) && injury.getExpiryDate().equals(date)) {
                Worker worker = injury.getWorker();
                worker.setInjury(null);
            }
        }
        injuries.removeIf(injury -> injury.getExpiryDate().equals(date));

        if (randomInjury()) {
            List<Worker> roster = workerManager.selectRoster(promotion);
            int workerIndex = RandomUtils.nextInt(0, roster.size());
            createRandomInjury(roster.get(workerIndex), date, promotion);
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

    public void createInjury(LocalDate startDate, int duration, Worker worker, SegmentView segmentView) {
        Injury injury = new Injury(startDate, startDate.plusDays(duration), worker, segmentView.getPromotion());
        addInjury(injury);
        newsManager.addMatchInjuryNewsItem(injury, segmentView.getEventView());
    }

    public void createRandomInjury(Worker worker, LocalDate date, Promotion promotion) {
        int injuryDays = RandomUtils.nextInt(0, MAX_INJURY_DAYS);
        Injury injury = new Injury(date, date.plusDays(injuryDays), worker, promotion);
        newsManager.addRandomInjuryNewsItem(injury);
        addInjury(injury);
    }

    private void addInjury(Injury injury) {
        injuries.add(injury);
        injury.getWorker().setInjury(injury);
    }

}
