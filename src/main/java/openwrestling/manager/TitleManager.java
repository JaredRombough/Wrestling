package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.TitleReign;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.manager.DateManager;
import openwrestling.model.utility.ModelUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TitleManager implements Serializable {

    @Getter
    private final List<Title> titles;
    @Getter
    private final List<TitleReign> titleReigns;

    private final DateManager dateManager;

    public TitleManager(DateManager dateManager) {
        this.titles = new ArrayList<>();
        this.titleReigns = new ArrayList<>();
        this.dateManager = dateManager;
    }

    public void createTitle(Title title) {
        createTitles(List.of(title));
    }

    public List<Title> createTitles(List<Title> titles) {
        List saved = Database.insertOrUpdateList(titles);
        List<TitleReign> titleReigns = new ArrayList<>();

        saved.forEach(obj -> {
            Title title = (Title) obj;
            if (title.getChampionTitleReign() != null) {
                title.getChampionTitleReign().setTitle(title);
                titleReigns.add(title.getChampionTitleReign());
            } else {
                TitleReign titleReign = TitleReign.builder()
                        .dayWon(dateManager.today())
                        .sequenceNumber(1)
                        .title(title)
                        .build();
                titleReigns.add(titleReign);
            }
        });

        List savedReigns = Database.insertOrUpdateList(titleReigns);

        saved.forEach(obj -> {
            Title title = (Title) obj;
            title.setChampionTitleReign(
                    (TitleReign) savedReigns.stream()
                            .filter(tileReign -> ((TitleReign) tileReign).getTitle().getTitleID() == title.getTitleID())
                            .findFirst().orElse(null)
            );
        });
        this.titleReigns.addAll(savedReigns);
        this.titles.addAll(saved);
        return saved;
    }

    public List<Title> getTitleViews(Promotion promotion) {
        List<Title> promotionTitles = new ArrayList<>();
        for (Title title : titles) {
            if (title.getPromotion().equals(promotion)) {
                promotionTitles.add(title);
            }
        }

        return promotionTitles;
    }

    public List<Worker> getCurrentChampionWorkers(Title title) {
        List<Worker> workers = new ArrayList<>();
        Optional<TitleReign> current = titleReigns.stream()
                .filter(titleReign -> titleReign.getSequenceNumber() == title.getSequenceNumber())
                .findFirst();

        current.ifPresent(titleReign -> workers.addAll(titleReign.getWorkers()));

        return workers;
    }

    public boolean isVacant(Title title) {
        return getCurrentChampionWorkers(title).isEmpty();
    }

    //check if we have any outstanding titles from expired contracts
    public void stripTitles(Contract contract) {
        //TODO
//        for (Title title : getTitleViews(contract.getPromotion())) {
//            for (Worker worker : getCurrentChampionWorkers(title)) {
//                if (worker.equals(contract.getWorker())) {
//                    stripTitle(title);
//                }
//            }
//        }
    }

    public void stripTitle(Title title) {
        //TODO
//        List<TitleWorker> currentChamps = getCurrentChampionTitleWorkers(title);
//        for (TitleWorker titleWorker : currentChamps) {
//            titleWorker.setDayLost(dateManager.today());
//        }
    }

    //here we would update the title's tracker of reigns also        
    public void titleChange(Title title, List<Worker> winner) {
        stripTitle(title);
        awardTitle(title, winner);
    }

    public void awardTitle(Title title, Worker winner) {
        List<Worker> workerAsList = new ArrayList<>();
        workerAsList.add(winner);
        awardTitle(title, workerAsList);
    }

    public void awardTitle(Title title, List<Worker> winner) {
        //TODO
        for (Worker worker : winner) {
//            TitleWorker titleWorker = new TitleWorker(title, worker, dateManager.today());
//            titleWorkers.add(titleWorker);

        }
        title.addReign(winner, dateManager.today());
    }

    //returns a list of titles available for an event
    public List<Title> getEventTitles(Promotion promotion, List<Worker> eventRoster) {

        List<Title> eventTitles = new ArrayList<>();

        for (Title title : getTitleViews(promotion)) {
            List<Worker> champs = getCurrentChampionWorkers(title);
            if (champs.isEmpty()) {
                eventTitles.add(title);
            } else {
                boolean titleWorkersPresent = true;

                for (Worker worker : champs) {
                    if (!eventRoster.contains(worker)) {
                        titleWorkersPresent = false;
                    }
                }
                if (titleWorkersPresent) {
                    eventTitles.add(title);
                }
            }
        }
        return eventTitles;
    }

    public String getTitleReignStrings(Title title) {

        StringBuilder sb = new StringBuilder();


        for (TitleReign titleReign : title.getTitleReigns()) {
            sb.append(titleReignString(titleReign));
            sb.append("\n");
        }

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
