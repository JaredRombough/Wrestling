package openwrestling.manager;

import lombok.Getter;
import openwrestling.Logging;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.TitleReign;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.manager.DateManager;
import openwrestling.model.utility.ModelUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Level;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TitleManager extends Logging implements Serializable {

    @Getter
    private List<Title> titles;

    private final DateManager dateManager;
    private final WorkerManager workerManager;

    public TitleManager(DateManager dateManager, WorkerManager workerManager) {
        this.titles = new ArrayList<>();
        this.dateManager = dateManager;
        this.workerManager = workerManager;
    }

    public void createTitle(Title title) {
        createTitles(List.of(title));
        selectTitles();
    }

    private void selectTitles() {
        this.titles = Database.selectAll(Title.class);
        List<TitleReign> titleReigns = Database.selectAll(TitleReign.class);
        titles.stream().forEach(title -> {
            title.setTitleReigns(
                    titleReigns.stream()
                            .filter(titleReign -> titleReign.getTitle().equals(title))
                            .collect(Collectors.toList())
            );
            title.getTitleReigns()
                    .forEach(titleReign -> titleReign.setWorkers(workerManager.refreshWorkers(titleReign.getWorkers())));
        });
    }

    public void createTitles(List<Title> titles) {
        titles.forEach(title -> {
            if (CollectionUtils.isEmpty(title.getTitleReigns())) {
                TitleReign vacant = TitleReign.builder()
                        .dayWon(dateManager.today())
                        .sequenceNumber(1)
                        .build();
                title.setTitleReigns(List.of(vacant));
            }

            TitleReign titleReign = title.getTitleReigns().get(0);
            Title saved = Database.insertGameObject(title);
            titleReign.setTitle(saved);
            Database.insertGameObject(titleReign);
        });
        selectTitles();
    }

    public void updateTitle(Title title) {
        Database.insertOrUpdateList(List.of(title));
        selectTitles();
    }

    public List<Title> getTitles(Promotion promotion) {
        return titles.stream()
                .filter(title -> title.getPromotion().equals(promotion))
                .collect(Collectors.toList());
    }

    public void stripTitlesForExpiringContract(Contract contract) {
        titles.stream()
                .filter(title -> title.getPromotion().equals(contract.getPromotion()) &&
                        title.getChampionTitleReign().getWorkers().contains(contract.getWorker()))
                .forEach(this::stripTitle);
    }

    public void stripTitle(Title title) {
        titleChange(title, List.of());
    }

    public void titleChange(Title title, List<Worker> winner) {
        logger.log(Level.DEBUG, String.format("TITLE CHANGE! promo %s title %s winner %s loser %s",
                title.getPromotion().getName(),
                title.getName(),
                ModelUtils.slashNames(title.getChampionTitleReign().getWorkers()),
                ModelUtils.slashNames(winner)
        ));
        title.getChampionTitleReign().setDayLost(dateManager.today());
        TitleReign newChamps = TitleReign.builder()
                .dayWon(dateManager.today())
                .sequenceNumber(title.getChampionTitleReign().getSequenceNumber() + 1)
                .workers(winner)
                .title(title)
                .build();

        Database.insertOrUpdateList(List.of(title.getChampionTitleReign(), newChamps));
        selectTitles();
    }

    public List<Title> getEventTitles(Promotion promotion, List<Worker> eventRoster) {
        return titles.stream()
                .filter(title -> title.getPromotion().equals(promotion) &&
                        eventRoster.containsAll(title.getChampions()))
                .collect(Collectors.toList());
    }

    public String getTitleReignStrings(Title title) {

        StringBuilder sb = new StringBuilder();


//        for (TitleReign titleReign : getTitleReigns(title)) {
//            sb.append(titleReignString(titleReign));
//            sb.append("\n");
//        }

        return sb.length() > 0 ? sb.toString() : "No title reigns on record";

    }

    public String titleReignString(TitleReign titleReign) {

        StringBuilder sb = new StringBuilder();
        List<Worker> champWorkers = titleReign.getWorkers();

        sb.append(ModelUtils.slashNames(champWorkers));
        sb.append("\t\t\t");
        sb.append(titleReign.getDayWon() == null ? "????" : titleReign.getDayWon());
        sb.append("\tto\t");
        sb.append(titleReign.getDayLost() == null ? "present" : titleReign.getDayLost());

        return sb.toString();
    }

}
