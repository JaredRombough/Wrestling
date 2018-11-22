package wrestling.model.factory;

import java.io.IOException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import wrestling.model.financial.BankAccount;
import wrestling.model.manager.DateManager;
import wrestling.model.manager.PromotionManager;
import wrestling.model.manager.StaffManager;
import wrestling.model.manager.WorkerManager;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.segmentEnum.StaffType;
import wrestling.model.utility.StaffUtils;

/*
for generating promotions in a random game
 */
public class PromotionFactory {

    private final ContractFactory contractFactory;

    private final DateManager dateManager;
    private final PromotionManager promotionManager;
    private final WorkerManager workerManager;
    private final StaffManager staffManager;

    public PromotionFactory(
            ContractFactory contractFactory,
            DateManager dateManager,
            PromotionManager promotionManager,
            WorkerManager workerManager,
            StaffManager staffManager) {
        this.contractFactory = contractFactory;
        this.dateManager = dateManager;
        this.promotionManager = promotionManager;
        this.workerManager = workerManager;
        this.staffManager = staffManager;
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
                    WorkerView worker = PersonFactory.randomWorker(RandomUtils.nextInt(promotion.getLevel() - 1, promotion.getLevel() + 1));
                    contractFactory.createContract(worker, promotion, dateManager.today());
                    if (j < rosterSize / 2) {
                        workerManager.addWorker(PersonFactory.randomWorker(promotion.getLevel()));
                    }
                }

                for (StaffType staffType : StaffType.values()) {
                    int ideal = StaffUtils.idealStaffCount(promotion, staffType);
                    int rand = RandomUtils.nextInt(0, 6);
                    if (rand == 1) {
                        ideal += 1;
                    } else if (rand == 2) {
                        ideal -= 1;
                    }
                    for (int j = 0; j < ideal; j++) {
                        contractFactory.createContract(PersonFactory.randomStaff(promotion.getLevel(), staffType), promotion, dateManager.today());
                        staffManager.addStaff(PersonFactory.randomStaff(promotion.getLevel(), staffType));
                    }
                }
                staffManager.addStaff(promotion.getAllStaff());
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
