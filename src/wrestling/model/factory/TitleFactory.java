package wrestling.model.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wrestling.model.Promotion;
import wrestling.model.Title;
import wrestling.model.Worker;

/**
 * called whenever a new title is to be created
 *
 */
public final class TitleFactory {

    //create a title with predetermined attributes
    public static void createTitle(Promotion promotion, Worker worker, String name) {

        List<Worker> workers = new ArrayList<>();
        workers.add(worker);

        Title title = new Title(promotion, workers, name);

        promotion.addTitle(title);
        worker.addTitle(title);
    }

    //create a title with predetermined attributes
    public static void createTitle(Promotion promotion, List<Worker> workers, String name) {
        Title title = new Title(promotion, workers, name);
        promotion.addTitle(title);
        for (Worker worker : workers) {
            worker.addTitle(title);
        }

    }

    //here we would update the title's tracker of reigns also        
    public static void titleChange(Title title, List<Worker> winner, int date) {

        stripTitle(title, date);
        awardTitle(title, winner, date);

    }

    public static void stripTitle(Title title, int date) {

        String s = title.getName();
        s += "dropped on " + date + " by ";
        for (Worker worker : title.getWorkers()) {
            s += worker.getName();
        }

        System.out.println(s);

        title.addRecord(date);

        for (Worker worker : title.getWorkers()) {
            worker.removeTitle(title);
        }

        title.vacateTitle();
        title.setDayWon(date);

    }

    public static void awardTitle(Title title, List<Worker> winner, int date) {

        title.addRecord(date);

        title.setWorkers(winner);
        for (Worker worker : winner) {
            worker.addTitle(title);
        }
        title.setDayWon(date);

    }

}
