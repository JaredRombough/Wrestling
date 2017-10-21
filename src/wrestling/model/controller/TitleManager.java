package wrestling.model.controller;

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
    
    
    //check if we have any outstanding titles from expired contracts
    public void stripTitles(Promotion promotion, Contract contract, LocalDate date) {
        for (Title title : promotion.getTitles()) {
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

        for (Worker worker : title.getWorkers()) {
            worker.removeTitle(title);
        }

        title.vacateTitle();
        title.setDayWon(date);

    }


}
