package wrestling.model;

import wrestling.model.factory.PromotionFactory;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * game controller handles game stuff
 */
public final class GameController implements Serializable {

    public GameController() throws IOException {

        //set the initial date here
        date = 1;

        //initialize the main lists
        workers = new ArrayList<>();
        promotions = new ArrayList<>();

        eventFactory = new EventFactory(this);

        PromotionFactory.preparePromotions(this);

    }

    private Integer date;

    public void setDate(Integer newDate) {
        date = newDate;
    }

    //only called by MainApp
    public void nextDay() {

        //iterate through all promotions
        for (Promotion promotion : promotions) {

            //update all the contracts associated with the current promotion
            List<Contract> contractList = new ArrayList<>(promotion.getContracts());
            for (Contract contract : contractList) {
                contract.nextDay(date);

            }

            //if the promotion is controlled by ai, do the daily update
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

    public EventFactory eventFactory;

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

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;

        for (Promotion promotion : promotions) {
            promotion.addFunds(10000);
        }
    }

    public void setWorkers(List<Worker> workers) {
        this.workers = workers;
    }

    private void setAi() {
        //add ai where necessary
        for (Promotion promotion : promotions) {
            if (!promotion.equals(playerPromotion)) {
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
