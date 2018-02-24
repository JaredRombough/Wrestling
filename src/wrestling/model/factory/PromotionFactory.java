package wrestling.model.factory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.Promotion;
import wrestling.model.Worker;
import wrestling.model.financial.BankAccount;
import wrestling.model.manager.ContractManager;
import wrestling.model.manager.DateManager;
import wrestling.model.manager.PromotionManager;
import wrestling.model.manager.WorkerManager;
import wrestling.model.utility.ModelUtilityFunctions;

/*
for generating promotions in a random game
 */
public class PromotionFactory {

    private final ContractFactory contractFactory;
    private final WorkerFactory workerFactory;

    private final ContractManager contractManager;
    private final DateManager dateManager;
    private final PromotionManager promotionManager;
    private final WorkerManager workerManager;

    public PromotionFactory(
            ContractFactory contractFactory,
            WorkerFactory workerFactory,
            ContractManager contractManager,
            DateManager dateManager,
            PromotionManager promotionManager,
            WorkerManager workerManager) {
        this.contractFactory = contractFactory;
        this.workerFactory = workerFactory;
        this.contractManager = contractManager;
        this.dateManager = dateManager;
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
            int currentLevel = 5 - i;

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
                promotionManager.getBankAccount(promotion).addFunds(startingFunds * promotion.getLevel());
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
        BankAccount bankAccount = new BankAccount(promotion);
        bankAccount.addFunds(10000);
        promotionManager.addBankAccount(bankAccount);
        return promotion;
    }

}
