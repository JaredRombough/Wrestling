package wrestling.model.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.Contract;
import wrestling.model.Promotion;
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.model.dirt.DirtSheet;
import wrestling.model.dirt.News;
import wrestling.model.dirt.TitleRecord;

public class TitleManager {

    private final List<Title> titles;
    private final DirtSheet dirtSheet;

    public TitleManager(DirtSheet dirtSheet) {
        titles = new ArrayList();
        this.dirtSheet = dirtSheet;
    }

    public void addTitle(Title title) {
        titles.add(title);
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

    //check if we have any outstanding titles from expired contracts
    public void stripTitles(Promotion promotion, Contract contract, LocalDate date) {
        for (Title title : getTitles(promotion)) {
            for (Worker worker : title.getWorkers()) {
                if (worker.equals(contract.getWorker())) {
                    stripTitle(title, date);
                }
            }
        }
    }

    public void stripTitle(Title title, LocalDate date) {

        StringBuilder sb = new StringBuilder();
        sb.append(title.getName());
        sb.append("dropped on ").append(date).append(" by ");
        for (Worker worker : title.getWorkers()) {
            sb.append(worker.getName());
        }
        dirtSheet.newDirt(new News(sb.toString(), title.getWorkers(), title.getPromotion()));
        dirtSheet.newDirt(new TitleRecord(title));

        title.vacateTitle();
        title.setDayWon(date);
    }

    //here we would update the title's tracker of reigns also        
    public void titleChange(Title title, List<Worker> winner, LocalDate date) {
        stripTitle(title, date);
        awardTitle(title, winner, date);
    }

    public void awardTitle(Title title, List<Worker> winner, LocalDate date) {
        title.setWorkers(winner);
        title.setDayWon(date);
    }

    //returns a list of titles available for an event
    public List<Title> getEventTitles(Promotion promotion, List<Worker> eventRoster) {

        List<Title> eventTitles = new ArrayList<>();

        for (Title title : getTitles(promotion)) {
            if (title.getWorkers().isEmpty()) {
                eventTitles.add(title);
            } else {
                boolean titleWorkersPresent = true;

                for (Worker worker : title.getWorkers()) {
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

}
