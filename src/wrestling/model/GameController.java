package wrestling.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * game controller handles game stuff
 */
public class GameController implements Serializable {

    private final WorkerFactory workerFactory;

    private Integer date;

    public void setDate(Integer newDate) {
        date = newDate;
    }

    //only called by MainApp
    public void nextDay() {

        for (Promotion promotion : promotions) {

            if (promotion.getAi() != null) {
                promotion.getAi().dailyUpdate();
            }

            //see if the promotion has an event scheduled today, if so process it
            if (promotion.getEventByDate(date) != null) {

                promotion.getEventByDate(date).processEvent();
            }

            List<Contract> contractList = new ArrayList(promotion.getContracts());
            for (Contract contract : contractList) {
                contract.nextDay();
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

    public List<Worker> allWorkers() {
        return workers;
    }

    public List<Worker> freeAgents(Promotion promotion) {
        List<Worker> freeAgents = new ArrayList<>();
        for (Worker worker : workers) {
            if (worker.canNegotiate(promotion)) {
                freeAgents.add(worker);
            }
        }

        return freeAgents;
    }
    
    public List<Worker> freeAgents() {
        List<Worker> freeAgents = new ArrayList<>();
        for (Worker worker : workers) {
            if (worker.canNegotiate()) {
                freeAgents.add(worker);
            }
        }

        return freeAgents;
    }

    public GameController() {

        //set the initial date here
        date = 1;

        //create a worker factory
        workerFactory = new WorkerFactory();

        //initialize the main lists
        workers = new ArrayList<Worker>();
        promotions = new ArrayList<Promotion>();

        //prepare the promotions (and workers)
        preparePromotions();

    }

    private void preparePromotions() {
        int numberOfPromotions = 20;
        int rosterSize = 15;
        int startingFunds = 10000;
        int workersPerPromotion = 50;

        //keeps track of what proportion of our promotions
        //should be at each level
        List<Double> levelRatios = Arrays.asList(
                0.3, //level 0
                0.2, //level 1
                0.2, //level 2
                0.2, //level 3
                0.1 //level 4
        );

        for (Double ratio : levelRatios) {

            double promotionCount = 0;
            double currentRatio;
            int currentLevel = levelRatios.indexOf(ratio);
            List<Worker> currentLevelWorkers = new ArrayList<Worker>();
            List<Promotion> currentLevelPromotions = new ArrayList<Promotion>();

            do {

                currentRatio = (double) promotionCount / numberOfPromotions;
                promotionCount++;

                Promotion newPromotion = new Promotion();

                newPromotion.setLevel(currentLevel);

                currentLevelPromotions.add(newPromotion);

                //add workers to the game in proportion to the current promotion
                currentLevelWorkers.addAll(workerFactory.createRoster(workersPerPromotion, currentLevel));

            } while (currentRatio < ratio);

            for (Promotion promotion : currentLevelPromotions) {

                //add funds (this could be based on promotion level)
                promotion.addFunds(startingFunds);

                //assign workers based on promotion level
                do {
                    Worker worker = getRandomFromList(currentLevelWorkers);
                    if (!promotion.getRoster().contains(worker)) {
                        //current.getRoster().add(worker);
                        Contract contract = new Contract(worker, promotion, true, true, 9, (worker.getPopularity() * 10));
                        worker.addContract(contract);
                        promotion.addContract(contract);
                    }

                } while (promotion.getRoster().size() < rosterSize);
            }

            //add all the workers and promotions we have generated for this
            //level to the main lists
            workers.addAll(currentLevelWorkers);
            promotions.addAll(currentLevelPromotions);
        }

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
