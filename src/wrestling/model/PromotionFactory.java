package wrestling.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/*
for generating promotions in a random game
 */
public class PromotionFactory {

    WorkerFactory workerFactory = new WorkerFactory();

    private GameController gameController;

    public PromotionFactory(GameController gameController) throws IOException {
        this.gameController = gameController;
        numberOfPromotions = 20;
        promotions = new ArrayList<>();
        allWorkers = new ArrayList<>();

        startingFunds = 10000;

    }

    private int numberOfPromotions;
    double[] levelRatios = {0.3, 0.2, 0.2, 0.2, 0.1};
    private int startingFunds;

    private final List<Promotion> promotions;
    private final List<Worker> allWorkers;

    public void preparePromotions() {

        for (int i = 0; i < levelRatios.length; i++) {

            double target = numberOfPromotions * levelRatios[i];
            double currentPromotions = 0;

            int currentLevel = i;

            List<Promotion> currentLevelPromotions = new ArrayList<>();

            while (currentPromotions < target) {

                Promotion newPromotion = new Promotion();

                newPromotion.setLevel(currentLevel);

                currentLevelPromotions.add(newPromotion);

                currentPromotions++;

            }

            int rosterSize = 10 + (currentLevel * 10);

            for (Promotion promotion : currentLevelPromotions) {

                //add funds (this could be based on promotion level)
                promotion.addFunds(startingFunds * promotion.getLevel());

                //assign workers based on promotion level
                do {

                    Worker worker = workerFactory.randomWorker(randomRange(promotion.getLevel() - 1, promotion.getLevel() + 1));

                    gameController.contractFactory.createContract(worker, promotion);

                } while (promotion.getRoster().size() < rosterSize);

                allWorkers.addAll(promotion.getRoster());
            }

            //add all the workers and promotions we have generated for this
            //level to the main lists
            promotions.addAll(currentLevelPromotions);

        }

    }

    private int randomRange(int min, int max) {
        Random r = new Random();
        return r.nextInt(max - min) + min;
    }

    /**
     * @param numberOfPromotions the numberOfPromotions to set
     */
    public void setNumberOfPromotions(int numberOfPromotions) {
        this.numberOfPromotions = numberOfPromotions;
    }

    /**
     * @param startingFunds the startingFunds to set
     */
    public void setStartingFunds(int startingFunds) {
        this.startingFunds = startingFunds;
    }

    /**
     * @return the promotions
     */
    public List<Promotion> getPromotions() {
        return promotions;
    }

    /**
     * @return the allWorkers
     */
    public List<Worker> getAllWorkers() {
        return allWorkers;
    }
}
