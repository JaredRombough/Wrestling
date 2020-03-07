package openwrestling.model.factory;

import openwrestling.manager.BankAccountManager;
import openwrestling.manager.ContractManager;
import openwrestling.manager.DateManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.StaffManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.StaffContract;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.financial.BankAccount;
import openwrestling.model.segmentEnum.StaffType;
import openwrestling.model.utility.ContractUtils;
import openwrestling.model.utility.StaffUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RandomGameAssetGenerator {
    private final static List<String> PROMOTION_NAMES = Arrays.asList(("Superb Wrestling Alliance, International Combat Order, Big Boss Pro Wrestling, Shocking Wrestle Union, Advanced Incorrigible Wrestling, Excellent Organization Of Wrestling, Extremely International Wrestling Organization, Big Fat Wrestling, Unparalleled Wrestling Execution, Regional Wrestling Superalliance, Desperate Wrestling Coalition, Confederation Of Absolute Wrestling Masters, Splendid Wrestling Pact, Impressive Allies Of Wrestling, Tremendous Combat Federation, Glorious Fighting Series, Sterling Wrestling Battlefield, Fabulous Warfare Association, Amzaing Wrestling Artistic Exhibition, Great Wrestling Group, Perpetual Wrestling Struggle, Competitive Pro Wrestling, Pro Wrestling Crusade, War Of Wrestlers International, Exquisite Wrestling Confrontation, Supreme Pro Wrestling Engagement, Fundamental Wrestling Experience, Quest For Wrestling Mastery, Global Touring Wrestling Exhibition").split(","));
    public static final int MAX_LEVEL = 5;

    private final ContractFactory contractFactory;

    private final DateManager dateManager;
    private final PromotionManager promotionManager;
    private final WorkerManager workerManager;
    private final StaffManager staffManager;
    private final BankAccountManager bankAccountManager;
    private final ContractManager contractManager;

    public RandomGameAssetGenerator(
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
        List<Double> levelRatios = List.of(0.3, 0.2, 0.2, 0.2, 0.1);
        List<String> promotionNames = new ArrayList<>(PROMOTION_NAMES);
        int numberOfPromotions = promotionNames.size();
        Collections.shuffle(promotionNames);

        List<Promotion> promotions = new ArrayList<>();

        for (Double ratio : levelRatios) {
            double target = numberOfPromotions * ratio;
            int currentLevel = MAX_LEVEL - levelRatios.indexOf(ratio);
            for (int i = 0; i < target && promotions.size() < numberOfPromotions; i++) {
                Promotion promotion = newPromotion();
                promotion.setLevel(currentLevel);
                promotion.setName(promotionNames.get(promotions.size()).trim());
                String[] words = promotion.getName().split(" ");
                String shortName = "";
                for (String word : words) {
                    shortName += StringUtils.upperCase(word.substring(0, 1));
                }
                promotion.setShortName(shortName);
                promotions.add(promotion);
            }
        }

        return promotions;
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
                        Contract contract = new Contract(dateManager.today(), worker, promotion);
                        contract.setExclusive(promotion.getLevel() == 5);
                        contract.setEndDate(ContractUtils.contractEndDate(dateManager.today(), RandomUtils.nextInt(0, 12)));

                        if (contract.isExclusive()) {
                            contract.setMonthlyCost(ContractUtils.calculateWorkerContractCost(worker, true));

                        } else {
                            contract.setAppearanceCost(ContractUtils.calculateWorkerContractCost(worker, false));
                        }

                        contracts.add(contract);
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
        BankAccount bankAccount = BankAccount.builder()
                .promotion(promotion)
                .funds(1000000)
                .build();
        bankAccountManager.addBankAccount(bankAccount);
        return promotion;
    }

}
