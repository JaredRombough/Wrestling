package wrestling.model.manager;

import java.util.ArrayList;
import java.util.List;
import wrestling.model.Contract;
import wrestling.model.modelView.PromotionView;
import wrestling.model.Title;
import wrestling.model.TitleWorker;
import wrestling.model.modelView.TitleReign;
import wrestling.model.modelView.TitleView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.utility.ModelUtils;

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

    public List<Title> getTitles(PromotionView promotion) {
        List<Title> promotionTitles = new ArrayList();
        for (Title title : titles) {
            if (title.getPromotion().equals(promotion)) {
                promotionTitles.add(title);
            }
        }

        return promotionTitles;
    }

    public List<TitleView> getTitleViews(PromotionView promotion) {
        List<TitleView> promotionTitleViews = new ArrayList();
        for (TitleView titleView : titleViews) {
            if (titleView.getTitle().getPromotion().equals(promotion)) {
                promotionTitleViews.add(titleView);
            }
        }

        return promotionTitleViews;
    }

    public List<WorkerView> getCurrentChampionWorkers(Title title) {
        List<WorkerView> workers = new ArrayList<>();
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
    public void stripTitles(Contract contract) {
        for (Title title : getTitles(contract.getPromotion())) {
            for (WorkerView worker : getCurrentChampionWorkers(title)) {
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
    public void titleChange(Title title, List<WorkerView> winner) {
        stripTitle(title);
        awardTitle(title, winner);
    }

    public void awardTitle(Title title, WorkerView winner) {
        List<WorkerView> workerAsList = new ArrayList<>();
        workerAsList.add(winner);
        awardTitle(title, workerAsList);
    }

    public void awardTitle(Title title, List<WorkerView> winner) {
        for (WorkerView worker : winner) {
            TitleWorker titleWorker = new TitleWorker(title, worker, dateManager.today());
            titleWorkers.add(titleWorker);

        }
        getTitleView(title).addReign(winner, dateManager.today());
    }

    public TitleView getTitleView(Title title) {
        for (TitleView titleView : titleViews) {
            if (titleView.getTitle().equals(title)) {
                return titleView;
            }
        }
        return null;
    }

    //returns a list of titles available for an event
    public List<Title> getEventTitles(PromotionView promotion, List<WorkerView> eventRoster) {

        List<Title> eventTitles = new ArrayList<>();

        for (Title title : getTitles(promotion)) {
            List<WorkerView> champs = getCurrentChampionWorkers(title);
            if (champs.isEmpty()) {
                eventTitles.add(title);
            } else {
                boolean titleWorkersPresent = true;

                for (WorkerView worker : champs) {
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
        List<WorkerView> champWorkers = titleReign.getWorkers();

        sb.append(ModelUtils.slashNames(champWorkers));
        sb.append("\t\t\t");
        sb.append(titleReign.getDayWon() == null ? "????" : titleReign.getDayWon());
        sb.append("\tto\t");
        sb.append(titleReign.getDateLost() == null ? "present" : titleReign.getDateLost());

        return sb.toString();
    }

}
