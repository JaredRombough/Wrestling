package openwrestling.model.factory;

import openwrestling.manager.BankAccountManager;
import openwrestling.manager.ContractManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.StaffManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.financial.BankAccount;
import openwrestling.model.manager.DateManager;
import openwrestling.model.segmentEnum.StaffType;
import openwrestling.model.utility.StaffUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
for generating promotions in a random game
 */
public class PromotionFactory {
    private final static List<String> PROMOTION_NAMES = Arrays.asList(("Superb Wrestling Alliance, International Combat Order, Big Boss Pro Wrestling, Shocking Wrestle Union, Advanced Incorrigible Wrestling, Excellent Organization Of Wrestling, Extremely International Wrestling Organization, Big Fat Wrestling, Unparalleled Wrestling Execution, Regional Wrestling Superalliance, Desperate Wrestling Coalition, Confederation Of Absolute Wrestling Masters, Splendid Wrestling Pact, Impressive Allies Of Wrestling, Tremendous Combat Federation, Glorious Fighting Series, Sterling Wrestling Battlefield, Fabulous Warfare Association, Amzaing Wrestling Artistic Exhibition, Great Wrestling Group, Perpetual Wrestling Struggle, Competitive Pro Wrestling, Pro Wrestling Crusade, War Of Wrestlers International, Exquisite Wrestling Confrontation, Supreme Pro Wrestling Engagement, Fundamental Wrestling Experience, Quest For Wrestling Mastery, Global Touring Wrestling Exhibition").split(","));

    private final ContractFactory contractFactory;

    private final DateManager dateManager;
    private final PromotionManager promotionManager;
    private final WorkerManager workerManager;
    private final StaffManager staffManager;
    private final BankAccountManager bankAccountManager;
    private final ContractManager contractManager;

    public PromotionFactory(
            ContractFactory contractFactory,
            DateManager dateManager,
            PromotionManager promotionManager,
            WorkerManager workerManager,
            StaffManager staffManager,
            BankAccountManager bankAccountManager,
            ContractManager contractManager) {
        this.contractFactory = contractFactory;
        this.dateManager = dateManager;
        this.promotionManager = promotionManager;
        this.workerManager = workerManager;
        this.staffManager = staffManager;
        this.bankAccountManager = bankAccountManager;
        this.contractManager = contractManager;
    }

    public void preparePromotions() throws IOException {
        int numberOfPromotions = 20;
        int startingFunds = 10000;
        double[] levelRatios = {0.3, 0.2, 0.2, 0.2, 0.1};

        List<Promotion> promotionsToAdd = new ArrayList<>();
        List<Contract> contractsToAdd = new ArrayList<>();
        List<Worker> workersToAdd = new ArrayList<>();

        List<String> promotionNames = new ArrayList<>();
        promotionNames.addAll(PROMOTION_NAMES);
        Collections.shuffle(promotionNames);
        int name = 0;
        for (double ratio : levelRatios) {
            double target = numberOfPromotions * ratio;
            //levels are 1 to 5
            int currentLevel = 5 - ArrayUtils.indexOf(levelRatios, ratio);

            for (int i = 0; i < target; i++) {
                Promotion promotion = newPromotion();
                promotion.setLevel(currentLevel);
                promotion.setName(promotionNames.get(name).trim());
                String[] words = promotion.getName().split(" ");
                String shortName = "";
                for (String word : words) {
                    shortName += StringUtils.upperCase(word.substring(0, 1));
                }
                promotion.setShortName(shortName);
                name++;

                int rosterSize = 10 + (currentLevel * 10);

                //add funds (this could be based on promotion level)
                bankAccountManager.getBankAccount(promotion).setFunds(startingFunds * promotion.getLevel());

                //assign workers based on promotion level


                for (int j = 0; j < rosterSize; j++) {
                    Worker worker = PersonFactory.randomWorker(RandomUtils.nextInt(promotion.getLevel() - 1, promotion.getLevel() + 1));
                    contractsToAdd.add(contractFactory.createContract(worker, promotion, dateManager.today()));
                    if (j < rosterSize / 2) {
                        workersToAdd.add(PersonFactory.randomWorker(promotion.getLevel()));
                    }
                }


                for (StaffType staffType : StaffType.values()) {
                    int ideal = StaffUtils.idealStaffCount(promotion, staffType, workerManager.selectRoster(promotion));
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
                //workerManager.createWorkers(promotion.getFullRoster());
                promotionsToAdd.add(promotion);
            }
        }
        workerManager.createWorkers(workersToAdd);
        promotionManager.createPromotions(promotionsToAdd);
        contractManager.createContracts(contractsToAdd);
    }

    public Promotion newPromotion() {
        Promotion promotion = new Promotion();
        BankAccount bankAccount = new BankAccount(promotion);
        bankAccount.setFunds(1000000);
        bankAccountManager.addBankAccount(bankAccount);
        return promotion;
    }

}
