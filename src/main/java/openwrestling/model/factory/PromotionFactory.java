package openwrestling.model.factory;

import openwrestling.manager.BankAccountManager;
import openwrestling.manager.ContractManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.StaffManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.StaffContract;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.financial.BankAccount;
import openwrestling.model.manager.DateManager;
import openwrestling.model.segmentEnum.StaffType;
import openwrestling.model.utility.StaffUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public void preparePromotions() {

        promotionManager.createPromotions(getInitialPromotions());
        setStartingFunds();
        List<Contract> initialContracts = initialContracts();
        List<Worker> initialContractWorkers = initialContracts.stream()
                .map(Contract::getWorker)
                .collect(Collectors.toList());

        List<Worker> savedWorkers = workerManager.createWorkers(initialContractWorkers);

        initialContracts
                .forEach(contract -> {
                    contract.setWorker(savedWorkers.stream().filter(
                            worker -> worker.getName().equals(contract.getWorker().getName())
                    ).findFirst().orElse(null));
                });

        contractManager.createContracts(initialContracts);
        workerManager.createWorkers(initialFreeAgents());


        List<StaffContract> initialStaffContracts = initialStaffContracts();
        List<StaffMember> initialContractStaff = initialStaffContracts.stream()
                .map(StaffContract::getStaff)
                .collect(Collectors.toList());

        List<StaffMember> savedStaff = staffManager.createStaffMembers(initialContractStaff);

        initialStaffContracts
                .forEach(contract -> {
                    contract.setStaff(savedStaff.stream().filter(
                            staffMember -> staffMember.getName().equals(contract.getStaff().getName())
                    ).findFirst().orElse(null));
                });

        contractManager.createStaffContracts(initialStaffContracts);
        staffManager.createStaffMembers(initialFreeAgentStaff());
    }

    List<Promotion> getInitialPromotions() {
        int numberOfPromotions = 20;
        List<Double> levelRatios = List.of(0.3, 0.2, 0.2, 0.2, 0.1);
        List<String> promotionNames = new ArrayList<>(PROMOTION_NAMES);
        Collections.shuffle(promotionNames);

        return levelRatios.stream()
                .flatMap(ratio -> {
                    double target = numberOfPromotions * ratio;
                    //levels are 1 to 5
                    int currentLevel = 5 - levelRatios.indexOf(ratio);
                    List<Promotion> promotions = new ArrayList<>();
                    for (int i = 0; i < target; i++) {
                        Promotion promotion = newPromotion();
                        promotion.setLevel(currentLevel);
                        promotion.setName(promotionNames.get(i).trim());
                        String[] words = promotion.getName().split(" ");
                        String shortName = "";
                        for (String word : words) {
                            shortName += StringUtils.upperCase(word.substring(0, 1));
                        }
                        promotion.setShortName(shortName);
                        promotions.add(promotion);
                    }
                    return promotions.stream();
                }).collect(Collectors.toList());
    }

    void setStartingFunds() {
        List<BankAccount> bankAccounts = bankAccountManager.getBankAccounts();
        int startingFunds = 10000;
        bankAccounts.forEach(bankAccount -> {
            bankAccount.setFunds(bankAccount.getPromotion().getLevel() * startingFunds);
        });
        bankAccountManager.createBankAccounts(bankAccounts);
    }

    List<Contract> initialContracts() {
        return promotionManager.getPromotions().stream()
                .flatMap(promotion -> {
                    int rosterSize = 10 + (promotion.getLevel() * 10);
                    List<Contract> contracts = new ArrayList<>();
                    for (int i = 0; i < rosterSize; i++) {
                        Worker worker = PersonFactory.randomWorker(RandomUtils.nextInt(promotion.getLevel() - 1, promotion.getLevel() + 1));
                        contracts.add(contractFactory.createContract(worker, promotion, dateManager.today()));
                    }
                    return contracts.stream();
                }).collect(Collectors.toList());
    }

    List<Worker> initialFreeAgents() {
        return promotionManager.getPromotions().stream()
                .flatMap(promotion -> {
                    int rosterSize = 10 + (promotion.getLevel() * 10);
                    List<Worker> workers = new ArrayList<>();
                    for (int j = 0; j < rosterSize / 2; j++) {
                        if (j < rosterSize / 2) {
                            workers.add(PersonFactory.randomWorker(promotion.getLevel()));
                        }
                    }
                    return workers.stream();
                }).collect(Collectors.toList());
    }

    List<StaffMember> initialFreeAgentStaff() {
        return promotionManager.getPromotions().stream()
                .flatMap(promotion ->
                        List.of(StaffType.values()).stream()
                                .flatMap(staffType -> {
                                    List<StaffMember> staffMembers = new ArrayList<>();
                                    int ideal = StaffUtils.idealStaffCount(promotion, staffType, workerManager.selectRoster(promotion));
                                    int rand = RandomUtils.nextInt(0, 6);
                                    if (rand == 1) {
                                        ideal += 1;
                                    } else if (rand == 2) {
                                        ideal -= 1;
                                    }
                                    for (int j = 0; j < ideal; j++) {
                                        staffMembers.add(PersonFactory.randomStaff(promotion.getLevel(), staffType));
                                    }
                                    return staffMembers.stream();
                                }).collect(Collectors.toList()).stream()
                )
                .collect(Collectors.toList());

    }

    List<StaffContract> initialStaffContracts() {
        return promotionManager.getPromotions().stream()
                .flatMap(promotion ->
                        List.of(StaffType.values()).stream()
                                .flatMap(staffType -> {
                                    List<StaffContract> staffContracts = new ArrayList<>();
                                    int ideal = StaffUtils.idealStaffCount(promotion, staffType, workerManager.selectRoster(promotion));
                                    int rand = RandomUtils.nextInt(0, 6);
                                    if (rand == 1) {
                                        ideal += 1;
                                    } else if (rand == 2) {
                                        ideal -= 1;
                                    }
                                    for (int j = 0; j < ideal; j++) {
                                        staffContracts.add(contractFactory.createContract(PersonFactory.randomStaff(promotion.getLevel(), staffType), promotion, dateManager.today()));
                                    }
                                    return staffContracts.stream();
                                }).collect(Collectors.toList()).stream()
                )
                .collect(Collectors.toList());
    }

    public Promotion newPromotion() {
        Promotion promotion = new Promotion();
        BankAccount bankAccount = new BankAccount(promotion);
        bankAccount.setFunds(1000000);
        bankAccountManager.addBankAccount(bankAccount);
        return promotion;
    }

}
