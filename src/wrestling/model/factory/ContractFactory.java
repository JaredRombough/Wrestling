package wrestling.model.factory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.RandomUtils;
import wrestling.model.Contract;
import wrestling.model.StaffContract;
import wrestling.model.interfaces.iContract;
import wrestling.model.manager.ContractManager;
import wrestling.model.modelView.PromotionView;
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

    public void createContract(StaffView staff, PromotionView promotion, LocalDate startDate) {
        int duration = 30 + RandomUtils.nextInt(0, 30);
        for (int i = 0; i < promotion.getLevel(); i++) {
            duration += 30;
        }
        createContract(staff, promotion, startDate, duration);
    }

    public void createContract(StaffView staff, PromotionView promotion, LocalDate startDate, int duration) {
        StaffContract contract = createContract(staff, promotion);
        contract.setBiWeeklyCost(calculateBiWeeklyCost(staff));
        contractManager.buyOutContracts(staff, promotion);
        initializeContract(contract, duration, startDate);
        staff.setStaffContract(contract);
    }

    //create a contract with predetermined attributes
    public iContract createContract(WorkerView worker, PromotionView promotion, boolean exclusive, int duration, LocalDate startDate) {
        //create the contract
        Contract contract = createContract(worker, promotion);

        contract.setDuration(duration);

        if (exclusive) {
            setBiWeeklyCost(contract);
        } else {
            setAppearanceCost(contract);
        }

        contract.setStartDate(startDate);

        return contract;
    }

    //create a contract with set exclusivity (only used by import)
    public void createContract(WorkerView worker, PromotionView promotion, LocalDate startDate, boolean exclusive) {

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
    public void createContract(WorkerView worker, PromotionView promotion, LocalDate startDate) {

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

    private Contract createContract(WorkerView worker, PromotionView promotion) {
        Contract contract = new Contract();
        contract.setWorker(worker);
        contract.setPromotion(promotion);
        contractManager.addContract(contract);
        promotion.addToRoster(worker);
        return contract;
    }

    private StaffContract createContract(StaffView staff, PromotionView promotion) {
        StaffContract contract = new StaffContract();
        contract.setStaff(staff);
        contract.setPromotion(promotion);
        contractManager.addContract(contract);
        promotion.addToStaff(staff);
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

    public int calculateBiWeeklyCost(StaffView staff) {

        int unitCost;

        List<Integer> pricePoints = new ArrayList<>();

        pricePoints.addAll(Arrays.asList(25, 25, 50, 75, 75, 150, 300, 500, 1000, 5000, 10000));

        double nearest10 = staff.getSkill() / 10 * 10;
        double multiplier = (staff.getSkill() - nearest10) / 10;

        int ppIndex = (int) nearest10 / 10;

        unitCost = pricePoints.get(ppIndex);

        if (nearest10 != 100) {
            double extra = (pricePoints.get(ppIndex + 1) - unitCost) * multiplier;
            unitCost += (int) extra;
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

}
