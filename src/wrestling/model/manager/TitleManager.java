package wrestling.model.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.Contract;
import wrestling.model.Promotion;
import wrestling.model.Title;
import wrestling.model.TitleWorker;
import wrestling.model.Worker;
import wrestling.model.modelView.SegmentView;
import wrestling.model.modelView.TitleReign;
import wrestling.model.modelView.TitleView;
import wrestling.model.utility.ModelUtilityFunctions;

public class TitleManager {

    private final List<Title> titles;
    private final List<TitleWorker> titleWorkers;
    private final List<TitleView> titleViews;

    private final DateManager dateManager;

    public TitleManager(DateManager dateManager) {
        titles = new ArrayList<>();
        titleWorkers = new ArrayList<>();
        this.titleViews = new ArrayList<>();
        this.dateManager = dateManager;
    }

    public void addTitle(Title title) {
        titles.add(title);
    }

    public void addTitleView(TitleView titleView) {
        titleViews.add(titleView);
    }

    public List<Title> getTitles(Promotion promotion) {
        List<Title> promotionTitles = new ArrayList();
        for (Title title : titles) {
            if (title.getPromotion().equals(promotion)) {
                promotionTitles.add(title);
            }
        }

        return promotionTitles;
    }

    public List<Worker> getCurrentChampionWorkers(Title title) {
        List<Worker> workers = new ArrayList<>();
        for (TitleWorker titleWorker : titleWorkers) {
            if (titleWorker.getTitle().equals(title) && titleWorker.getDayLost() == null) {
                workers.add(titleWorker.getWorker());
            }
        }
        return workers;
    }

    public List<TitleWorker> getCurrentChampionTitleWorkers(Title title) {
        List<TitleWorker> workers = new ArrayList<>();
        for (TitleWorker titleWorker : titleWorkers) {
            if (titleWorker.getTitle().equals(title) && titleWorker.getDayLost() == null) {
                workers.add(titleWorker);
            }
        }
        return workers;
    }

    public boolean isVacant(Title title) {
        return getCurrentChampionTitleWorkers(title).isEmpty();
    }

    //check if we have any outstanding titles from expired contracts
    public void stripTitles(Promotion promotion, Contract contract) {
        for (Title title : getTitles(promotion)) {
            for (Worker worker : getCurrentChampionWorkers(title)) {
                if (worker.equals(contract.getWorker())) {
                    stripTitle(title);
                }
            }
        }
    }

    public void stripTitle(Title title) {
        List<TitleWorker> currentChamps = getCurrentChampionTitleWorkers(title);
        for (TitleWorker titleWorker : currentChamps) {
            titleWorker.setDayLost(dateManager.today());
        }
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
        for (Worker worker : winner) {
            TitleWorker titleWorker = new TitleWorker(title, worker, dateManager.today());
            titleWorkers.add(titleWorker);

        }
        getTitleView(title).addReign(winner, dateManager.today());
    }

    private TitleView getTitleView(Title title) {
        for (TitleView titleView : titleViews) {
            if (titleView.getTitle().equals(title)) {
                return titleView;
            }
        }
        return null;
    }

    //returns a list of titles available for an event
    public List<Title> getEventTitles(Promotion promotion, List<Worker> eventRoster) {

        List<Title> eventTitles = new ArrayList<>();

        for (Title title : getTitles(promotion)) {
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

        TitleView titleView = getTitleView(title);

        for (TitleReign titleReign : titleView.getTitleReigns()) {
                sb.append(titleReignString(titleReign));
                sb.append("\n");
        }

        return sb.length() > 0 ? sb.toString() : "No title reigns on record";

    }

    public String titleReignString(TitleReign titleReign) {

        StringBuilder sb = new StringBuilder();
        List<Worker> champWorkers = titleReign.getWorkers();

        sb.append(ModelUtilityFunctions.slashNames(champWorkers));
        sb.append("\t\t\t");
        sb.append(titleReign.getDayWon() == null ? "????" : titleReign.getDayWon());
        sb.append("\tto\t");
        sb.append(titleReign.getDayLost() == null ? "present" : titleReign.getDayLost());

        return sb.toString();
    }
    /*
    champions
    dayown
    daylost
    
     */
}
