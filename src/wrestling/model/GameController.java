package wrestling.model;

import wrestling.model.factory.PromotionFactory;
import java.io.IOException;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
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
        gameDate = LocalDate.of(2015, 1, 1);

        payDay = gameDate.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));

        //initialize the main lists
        workers = new ArrayList<>();
        promotions = new ArrayList<>();

        //eventFactory = new EventFactory(this);
        PromotionFactory.preparePromotions(this);

    }

    private LocalDate gameDate;
    private LocalDate payDay;

    //is it payday?
    public boolean isPayDay() {
        boolean isPayDay = false;

        if (gameDate.isEqual(payDay)) {

            isPayDay = true;
        }

        return isPayDay;
    }

    //only called by MainApp
    public void nextDay() {

        //iterate through all promotions
        for (Promotion promotion : getPromotions()) {

            //update all the contracts associated with the current promotion
            List<Contract> contractList = new ArrayList<>(promotion.getContracts());
            for (Contract contract : contractList) {
                contract.nextDay(gameDate);

            }

            //if the promotion is controlled by ai, do the daily update
            if (promotion.getAi() != null) {
                promotion.getAi().dailyUpdate();
            }

        }

        //if it is payday, advance next payday to two fridays in the future
        if (isPayDay()) {
            payDay = payDay.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
            payDay = payDay.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));

        }

        //advance the day by one
        gameDate = LocalDate.from(gameDate).plusDays(1);

    }

    public LocalDate date() {
        return gameDate;
    }

    private List<Promotion> promotions;

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

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;

        for (Promotion promotion : promotions) {
            promotion.bankAccount().addFunds(10000);
        }
    }

    public void setWorkers(List<Worker> workers) {
        this.workers = workers;
    }

    private void setAi() {
        //add ai where necessary
        for (Promotion promotion : getPromotions()) {
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

        return list.get(randomizer.nextInt(list.size()));
    }

    /**
     * @return the promotions
     */
    public List<Promotion> getPromotions() {
        return promotions;
    }

}
