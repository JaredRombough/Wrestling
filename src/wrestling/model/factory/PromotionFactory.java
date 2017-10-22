package wrestling.model.factory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.Promotion;
import wrestling.model.Worker;
import wrestling.model.controller.ContractManager;
import wrestling.model.controller.DateManager;
import wrestling.model.controller.PromotionEventManager;
import wrestling.model.controller.PromotionManager;
import wrestling.model.controller.WorkerManager;
import wrestling.model.utility.ModelUtilityFunctions;

/*
for generating promotions in a random game
 */
public class PromotionFactory {

    private final ContractFactory contractFactory;
    private final WorkerFactory workerFactory;

    private final ContractManager contractManager;
    private final DateManager dateManager;
    private final PromotionEventManager eventManager;
    private final PromotionManager promotionManager;
    private final WorkerManager workerManager;

    public PromotionFactory(
            ContractFactory contractFactory,
            WorkerFactory workerFactory,
            ContractManager contractManager,
            DateManager dateManager,
            PromotionEventManager eventManager,
            PromotionManager promotionManager,
            WorkerManager workerManager) {
        this.contractFactory = contractFactory;
        this.workerFactory = workerFactory;
        this.contractManager = contractManager;
        this.dateManager = dateManager;
        this.eventManager = eventManager;
        this.promotionManager = promotionManager;
        this.workerManager = workerManager;
    }

    public void preparePromotions() throws IOException {

        List<Promotion> promotions = new ArrayList<>();
        List<Worker> allWorkers = new ArrayList<>();

        int numberOfPromotions = 20;
        int startingFunds = 10000;
        double[] levelRatios = {0.3, 0.2, 0.2, 0.2, 0.1};

        for (int i = 0; i < levelRatios.length; i++) {

            double target = numberOfPromotions * levelRatios[i];
            double currentPromotions = 0;

            //levels are 1 to 5
            int currentLevel = i + 1;

            List<Promotion> currentLevelPromotions = new ArrayList<>();

            while (currentPromotions < target) {

                Promotion newPromotion = newPromotion();

                newPromotion.setLevel(currentLevel);

                currentLevelPromotions.add(newPromotion);

                currentPromotions++;

            }

            int rosterSize = 10 + (currentLevel * 10);

            for (Promotion promotion : currentLevelPromotions) {

                //add funds (this could be based on promotion level)
                promotion.bankAccount().addFunds(startingFunds * promotion.getLevel());

                //assign workers based on promotion level
                do {

                    Worker worker = workerFactory.randomWorker(ModelUtilityFunctions.randRange(promotion.getLevel() - 1, promotion.getLevel() + 1));

                    contractFactory.createContract(worker, promotion, dateManager.today());

                } while (contractManager.getFullRoster(promotion).size() < rosterSize);

                allWorkers.addAll(contractManager.getFullRoster(promotion));
            }

            //add all the workers and promotions we have generated for this
            //level to the main lists
            promotions.addAll(currentLevelPromotions);

        }
        promotionManager.addPromotions(promotions);
        workerManager.addWorkers(allWorkers);
    }

    public Promotion newPromotion() {
        Promotion promotion = new Promotion();
        promotion.bankAccount().addFunds(10000);
        eventManager.addEventDate((dateManager.today()).plusDays(ModelUtilityFunctions.randRange(2, 7)), promotion);
        return promotion;
    }

}
