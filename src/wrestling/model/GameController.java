package wrestling.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * game controller handles game stuff
 */
public class GameController implements Serializable {

    private WorkerFactory workerFactory;

    private Integer date;

    public void setDate(Integer newDate) {
        date = newDate;
    }

    public void nextDay() {
        for (Promotion promotion : promotions) {
            if (promotion.getAi() != null) {
                promotion.getAi().dailyUpdate();
            }

        }
        date++;
    }

    public Integer date() {
        return date;
    }

    public List<Promotion> promotions;

    private Promotion playerPromotion;

    public void setPlayerPromotion(Promotion promotion) {
        playerPromotion = promotion;
        //set the Ai for non-player promotions
        setAi();
    }

    public Promotion playerPromotion() {
        return playerPromotion;
    }

    private List<Worker> workers;

    public GameController() {

        Integer numberOfPromotions = 10;
        Integer rosterSize = 15;
        Integer startingFunds = 10000;

        date = 1;

        //create a worker factory
        workerFactory = new WorkerFactory();
        workers = new ArrayList<Worker>();

        //create the workers
        workers = workerFactory.createRoster(1000);

        promotions = new ArrayList<Promotion>();

        //create the promotions
        for (int i = 0; i < numberOfPromotions; i++) {
            Promotion newPromotion = new Promotion();

            promotions.add(newPromotion);
        }

        for (Promotion current : promotions) {

            //add funds
            current.addFunds(startingFunds);

            //assign workers
            do {
                Worker worker = getRandomFromList(workers);
                if (!current.roster.contains(worker)) {
                    current.roster.add(worker);
                    new Contract(worker, current, 90, date);
                }

            } while (current.roster.size() < rosterSize);
        }

        //keep track of all workers as thier own 'promotion' roster for now
        Promotion freeAgents = new Promotion();
        freeAgents.setName("All Workers");
        freeAgents.roster = workers;
        promotions.add(freeAgents);

    }

    private void setAi() {
        //add ai where necessary
        for (Promotion promotion : promotions) {
            if (!promotion.equals(playerPromotion) && !promotion.getName().equals("All Workers")) {
                promotion.setAi(new PromotionAi(promotion, this));
            }

        }
    }

    /*
    returns a random worker from a list of workers
     */
    public static Worker getRandomFromList(List<Worker> list) {
        Random randomizer = new Random();

        Worker randomWorker = list.get(randomizer.nextInt(list.size()));
        return randomWorker;
    }

}
