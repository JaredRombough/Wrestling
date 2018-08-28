package wrestling.model.factory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.RandomUtils;
import wrestling.model.Contract;
import wrestling.model.Promotion;
import wrestling.model.StaffContract;
import wrestling.model.interfaces.iContract;
import wrestling.model.manager.ContractManager;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.WorkerView;

/**
 * attached to the gameController, it is called whenever a new contract is to be
 * created
 *
 */
public class ContractFactory {

    private final ContractManager contractManager;

    public ContractFactory(ContractManager contractManager) {
        this.contractManager = contractManager;
    }

    //create a contract with set exclusivity (only used by import)
    public void createContract(StaffView staff, Promotion promotion, LocalDate startDate, boolean exclusive) {

        //create the contract
        StaffContract contract = createContract(staff, promotion);

        setBiWeeklyCost(contract);

        contractManager.buyOutContracts(staff, promotion);

        int duration = 30 + RandomUtils.nextInt(0, 30);

        //scale the duration and exclusivity based on promotion level
        for (int i = 0; i < promotion.getLevel(); i++) {
            duration += 30;
        }

        initializeContract(contract, duration, startDate);
    }

    //create a contract with predetermined attributes
    public void createContract(WorkerView worker, Promotion promotion, boolean exclusive, int duration, LocalDate startDate) {
        //create the contract
        Contract contract = createContract(worker, promotion);

        contract.setDuration(duration);

        if (exclusive) {
            setBiWeeklyCost(contract);
        } else {
            setAppearanceCost(contract);
        }

        contract.setStartDate(startDate);
    }

    //create a contract with set exclusivity (only used by import)
    public void createContract(WorkerView worker, Promotion promotion, LocalDate startDate, boolean exclusive) {

        //create the contract
        Contract contract = createContract(worker, promotion);

        //exclusive contracts are default for top level promotions
        if (exclusive) {

            contract.setExclusive(true);
            setBiWeeklyCost(contract);

            contractManager.buyOutContracts(worker, promotion);

        } else {
            contract.setExclusive(false);
            setAppearanceCost(contract);
        }

        int duration = 30 + RandomUtils.nextInt(0, 30);

        //scale the duration and exclusivity based on promotion level
        for (int i = 0; i < promotion.getLevel(); i++) {
            duration += 30;
        }

        initializeContract(contract, duration, startDate);
    }

    //create a default contract
    public void createContract(WorkerView worker, Promotion promotion, LocalDate startDate) {

        //create the contract
        Contract contract = createContract(worker, promotion);

        //exclusive contracts are default for top level promotions
        if (promotion.getLevel() == 5) {

            contract.setExclusive(true);
            setBiWeeklyCost(contract);

            contractManager.buyOutContracts(worker, promotion);

        } else {
            contract.setExclusive(false);
            setAppearanceCost(contract);
        }

        int duration = 30;

        //scale the duration and exclusivity based on promotion level
        for (int i = 0; i < promotion.getLevel(); i++) {
            duration += 30;
        }

        initializeContract(contract, duration, startDate);
    }

    private Contract createContract(WorkerView worker, Promotion promotion) {
        Contract contract = new Contract();
        contract.setWorker(worker);
        contract.setPromotion(promotion);
        contractManager.addContract(contract);
        return contract;
    }

    private StaffContract createContract(StaffView staff, Promotion promotion) {
        StaffContract contract = new StaffContract();
        contract.setStaff(staff);
        contract.setPromotion(promotion);
        contractManager.addContract(contract);
        return contract;
    }

    private void initializeContract(iContract contract, int duration, LocalDate startDate) {
        contract.setDuration(duration);
        contract.setStartDate(startDate);
    }

    public int calculateAppearanceCost(WorkerView worker, boolean exclusive) {
        int unitCost;

        List<Integer> pricePoints = new ArrayList<>();

        pricePoints.addAll(Arrays.asList(0, 10, 20, 50, 75, 100, 250, 500, 1000, 10000, 100000));

        double nearest10 = worker.getPopularity() / 10 * 10;
        double multiplier = (worker.getPopularity() - nearest10) / 10;

        int ppIndex = (int) nearest10 / 10;

        unitCost = pricePoints.get(ppIndex);

        if (nearest10 != 100) {
            double extra = (pricePoints.get(ppIndex + 1) - unitCost) * multiplier;
            unitCost += (int) extra;
        }

        if (exclusive) {
            unitCost *= 1.5;
        }

        return unitCost;
    }

    /*
    calculate the cost for a contract if not explicitly specified
     */
    private void setAppearanceCost(Contract contract) {

        List<Integer> pricePoints = new ArrayList<>();

        pricePoints.addAll(Arrays.asList(0, 10, 20, 50, 75, 100, 250, 500, 1000, 10000, 100000));

        double nearest10 = contract.getWorker().getPopularity() / 10 * 10;
        double multiplier = (contract.getWorker().getPopularity() - nearest10) / 10;

        int ppIndex = (int) nearest10 / 10;

        int unitCost = pricePoints.get(ppIndex);

        if (nearest10 != 100) {
            double extra = (pricePoints.get(ppIndex + 1) - unitCost) * multiplier;
            unitCost += (int) extra;
        }

        if (contract.isExclusive()) {
            unitCost *= 1.5;
        }

        contract.setAppearanceCost(unitCost);
    }

    /*
    calculate the cost for salaried workers
     */
    private void setBiWeeklyCost(Contract contract) {

        int unitCost;

        List<Integer> pricePoints = new ArrayList<>();

        pricePoints.addAll(Arrays.asList(25, 25, 50, 75, 75, 150, 300, 500, 1000, 5000, 50000));

        double nearest10 = contract.getWorker().getPopularity() / 10 * 10;
        double multiplier = (contract.getWorker().getPopularity() - nearest10) / 10;

        int ppIndex = (int) nearest10 / 10;

        unitCost = pricePoints.get(ppIndex);

        if (nearest10 != 100) {
            double extra = (pricePoints.get(ppIndex + 1) - unitCost) * multiplier;
            unitCost += (int) extra;
        }

        if (contract.isExclusive()) {
            unitCost *= 1.5;
        }

        contract.setBiWeeklyCost(unitCost);
    }

    private void setBiWeeklyCost(StaffContract contract) {

        int unitCost;

        List<Integer> pricePoints = new ArrayList<>();

        pricePoints.addAll(Arrays.asList(25, 25, 50, 75, 75, 150, 300, 500, 1000, 5000, 10000));

        double nearest10 = contract.getStaff().getSkill() / 10 * 10;
        double multiplier = (contract.getStaff().getSkill() - nearest10) / 10;

        int ppIndex = (int) nearest10 / 10;

        unitCost = pricePoints.get(ppIndex);

        if (nearest10 != 100) {
            double extra = (pricePoints.get(ppIndex + 1) - unitCost) * multiplier;
            unitCost += (int) extra;
        }


        contract.setBiWeeklyCost(unitCost);
    }
}
