package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.TitleReign;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.utility.ModelUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Level;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TitleManager extends GameObjectManager implements Serializable {

    private final DateManager dateManager;
    private final WorkerManager workerManager;
    @Getter
    private List<Title> titles;

    public TitleManager(Database database, DateManager dateManager, WorkerManager workerManager) {
        super(database);
        this.titles = new ArrayList<>();
        this.dateManager = dateManager;
        this.workerManager = workerManager;
    }

    @Override
    public void selectData() {
        selectTitles();
    }

    public Title createTitle(Title title) {
        Title savedTitle = createTitles(List.of(title)).get(0);
        selectTitles();
        return savedTitle;
    }

    private void selectTitles() {
        this.titles = getDatabase().selectAll(Title.class);
        List<TitleReign> titleReigns = getDatabase().selectAll(TitleReign.class);
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

    public List<Title> createTitles(List<Title> titles) {
        List<TitleReign> titleReigns = new ArrayList<>();
        List<Long> savedIDs = new ArrayList<>();
        titles.forEach(title -> {
            if (CollectionUtils.isEmpty(title.getTitleReigns())) {
                TitleReign vacant = TitleReign.builder()
                        .dayWon(dateManager.today())
                        .sequenceNumber(1)
                        .build();
                title.setTitleReigns(List.of(vacant));
            }

            TitleReign titleReign = title.getTitleReigns().get(0);
            Title saved = getDatabase().insertGameObject(title);
            savedIDs.add(saved.getTitleID());
            titleReign.setTitle(saved);
            titleReigns.add(titleReign);
        });
        getDatabase().insertList(titleReigns);
        selectTitles();

        return this.titles.stream()
                .filter(title -> savedIDs.contains(title.getTitleID()))
                .collect(Collectors.toList());
    }

    public void updateTitle(Title title) {
        getDatabase().insertList(List.of(title));
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
                ModelUtils.slashNames(winner),
                ModelUtils.slashNames(title.getChampionTitleReign().getWorkers())
        ));
        title.getChampionTitleReign().setDayLost(dateManager.today());
        TitleReign newChamps = TitleReign.builder()
                .dayWon(dateManager.today())
                .sequenceNumber(title.getChampionTitleReign().getSequenceNumber() + 1)
                .workers(winner)
                .title(title)
                .build();

        getDatabase().insertList(List.of(title.getChampionTitleReign(), newChamps));
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
