package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.manager.WorkerManager;
import openwrestling.model.gameObjects.Injury;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.manager.DateManager;
import openwrestling.model.manager.NewsManager;
import org.apache.commons.lang3.RandomUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static openwrestling.model.constants.GameConstants.MAX_INJURY_DAYS;

public class InjuryManager implements Serializable {

    private final NewsManager newsManager;
    private final WorkerManager workerManager;
    private final DateManager dateManager;
    @Getter
    private List<Injury> injuries = new ArrayList<>();

    public InjuryManager(NewsManager newsManager, WorkerManager workerManager, DateManager dateManager) {
        this.newsManager = newsManager;
        this.workerManager = workerManager;
        this.dateManager = dateManager;
    }

    public void dailyUpdate(LocalDate date, Promotion promotion) {
        if (randomInjury()) {
            List<Worker> roster = workerManager.selectRoster(promotion);
            int workerIndex = RandomUtils.nextInt(0, roster.size());
            createRandomInjury(roster.get(workerIndex), date, promotion);
        }
    }

    public boolean hasInjury(Worker worker) {
        return injuries.stream().anyMatch(injury -> injury.getWorker().equals(worker));
    }

    public Injury getInjury(Worker worker) {
        return injuries.stream()
                .filter(injury -> injury.getWorker().equals(worker))
                .findFirst()
                .orElse(null);
    }

    public void createInjuries(List<Injury> injuries) {
        Database.insertOrUpdateList(injuries);
        update();
    }


    public void createRandomInjury(Worker worker, LocalDate date, Promotion promotion) {
        int injuryDays = RandomUtils.nextInt(0, MAX_INJURY_DAYS);
        Injury injury = Injury.builder()
                .startDate(date)
                .expiryDate(date.plusDays(injuryDays))
                .worker(worker)
                .promotion(promotion)
                .build();
        createInjuries(List.of(injury));

        newsManager.addRandomInjuryNewsItem(injury);
    }

    private void update() {
        this.injuries = (List<Injury>) Database.selectAll(Injury.class).stream()
                .filter(injury -> ((Injury) injury).getExpiryDate().isAfter(dateManager.today()))
                .collect(Collectors.toList());
    }


    private boolean randomInjury() {
        return RandomUtils.nextInt(0, 1000) == 1;
    }


}
