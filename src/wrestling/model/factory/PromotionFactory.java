package wrestling.model.factory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import wrestling.model.financial.BankAccount;
import wrestling.model.manager.ContractManager;
import wrestling.model.manager.DateManager;
import wrestling.model.manager.EventManager;
import wrestling.model.manager.PromotionManager;
import wrestling.model.manager.WorkerManager;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.WorkerView;

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
    private final EventManager eventManager;

    public PromotionFactory(
            ContractFactory contractFactory,
            WorkerFactory workerFactory,
            ContractManager contractManager,
            DateManager dateManager,
            PromotionManager promotionManager,
            WorkerManager workerManager,
            EventManager eventManager) {
        this.contractFactory = contractFactory;
        this.workerFactory = workerFactory;
        this.contractManager = contractManager;
        this.dateManager = dateManager;
        this.promotionManager = promotionManager;
        this.workerManager = workerManager;
        this.eventManager = eventManager;
    }

    public void preparePromotions() throws IOException {
        int numberOfPromotions = 20;
        int startingFunds = 10000;
        double[] levelRatios = {0.3, 0.2, 0.2, 0.2, 0.1};

        for (double ratio : levelRatios) {
            double target = numberOfPromotions * ratio;
            //levels are 1 to 5
            int currentLevel = 5 - ArrayUtils.indexOf(levelRatios, ratio);

            for (int i = 0; i < target; i++) {
                PromotionView promotion = newPromotion();

                promotion.setLevel(currentLevel);

                int rosterSize = 10 + (currentLevel * 10);

                //add funds (this could be based on promotion level)
                promotionManager.getBankAccount(promotion).addFunds(startingFunds * promotion.getLevel());

                //assign workers based on promotion level
                for (int j = 0; j < rosterSize; j++) {
                    WorkerView worker = workerFactory.randomWorker(RandomUtils.nextInt(promotion.getLevel() - 1, promotion.getLevel() + 1));
                    contractFactory.createContract(worker, promotion, dateManager.today());
                }
                workerManager.addWorkers(promotion.getFullRoster());
                promotionManager.addPromotion(promotion);
            }
        }
    }

    public PromotionView newPromotion() {
        PromotionView promotion = new PromotionView();
        BankAccount bankAccount = new BankAccount(promotion);
        bankAccount.addFunds(1000000);
        promotionManager.addBankAccount(bankAccount);
        return promotion;
    }

}
