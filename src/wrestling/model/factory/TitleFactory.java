package wrestling.model.factory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.controller.GameController;
import wrestling.model.dirt.News;
import wrestling.model.Promotion;
import wrestling.model.Title;
import wrestling.model.dirt.TitleRecord;
import wrestling.model.Worker;

/**
 * called whenever a new title is to be created
 *
 */
public class TitleFactory {

    //create a title with predetermined attributes
    public void createTitle(Promotion promotion, Worker worker, String name) {

        List<Worker> workers = new ArrayList<>();
        workers.add(worker);

        Title title = new Title(promotion, workers, name);

        promotion.addTitle(title);
        worker.addTitle(title);
    }

    //create a title with predetermined attributes
    public void createTitle(Promotion promotion, List<Worker> workers, String name) {
        Title title = new Title(promotion, workers, name);
        promotion.addTitle(title);
        for (Worker worker : workers) {
            worker.addTitle(title);
        }

    }

    //here we would update the title's tracker of reigns also        
    public void titleChange(Title title, List<Worker> winner, LocalDate date) {

        stripTitle(title, date);
        awardTitle(title, winner, date);

    }

    public void stripTitle(Title title, LocalDate date) {

        StringBuilder sb = new StringBuilder();
        sb.append(title.getName());
        sb.append("dropped on ").append(date).append(" by ");
        for (Worker worker : title.getWorkers()) {
            sb.append(worker.getName());
        }
        gc.getDirtSheet().newDirt(new News(sb.toString(), title.getWorkers(), title.getPromotion()));
        gc.getDirtSheet().newDirt(new TitleRecord(title));

        for (Worker worker : title.getWorkers()) {
            worker.removeTitle(title);
        }

        title.vacateTitle();
        title.setDayWon(date);

    }

    public void awardTitle(Title title, List<Worker> winner, LocalDate date) {

        title.setWorkers(winner);
        for (Worker worker : winner) {
            worker.addTitle(title);
        }
        title.setDayWon(date);

    }

    private GameController gc;

    public TitleFactory(GameController gc) {
        this.gc = gc;
    }

}
