package wrestling.model.factory;

import java.time.LocalDate;
import org.apache.commons.lang3.RandomUtils;
import wrestling.model.Contract;
import wrestling.model.StaffContract;
import wrestling.model.interfaces.iContract;
import wrestling.model.manager.ContractManager;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.utility.ContractUtils;
import wrestling.model.utility.StaffUtils;

public class ContractFactory {

    private final ContractManager contractManager;

    public ContractFactory(ContractManager contractManager) {
        this.contractManager = contractManager;
    }

    public void createContract(StaffView staff, PromotionView promotion, LocalDate startDate) {
        System.out.println("createContract(StaffView staff, PromotionView promotion, LocalDate startDate) {");
        int duration = 1 + RandomUtils.nextInt(0, 2);
        for (int i = 0; i < promotion.getLevel(); i++) {
            duration += 1;
        }
        createContract(staff, promotion, startDate, ContractUtils.contractEndDate(startDate, duration));
    }

    public void createContract(StaffView staff, PromotionView promotion, LocalDate startDate, int duration) {
        createContract(staff, promotion, startDate, ContractUtils.contractEndDate(startDate, duration));
    }

    public void createContract(WorkerView worker, PromotionView promotion, LocalDate startDate, boolean exclusive) {
        createContract(worker, promotion, exclusive, RandomUtils.nextInt(0, 12), startDate);
    }

    public void createContract(WorkerView worker, PromotionView promotion, LocalDate startDate) {
        createContract(worker, promotion, startDate, promotion.getLevel() == 5);
    }

    public iContract createContract(WorkerView worker, PromotionView promotion, boolean exclusive, int duration, LocalDate startDate) {
        Contract contract = new Contract();
        contract.setWorker(worker);
        contract.setPromotion(promotion);
        contract.setExclusive(exclusive);
        contract.setStartDate(startDate);
        contract.setEndDate(ContractUtils.contractEndDate(startDate, duration));

        if (exclusive) {
            contract.setMonthlyCost(ContractUtils.calculateWorkerContractCost(worker, true));
            contractManager.buyOutContracts(worker, promotion, startDate);
        } else {
            contract.setAppearanceCost(ContractUtils.calculateWorkerContractCost(worker, false));
        }

        contractManager.addContract(contract);
        promotion.addToRoster(worker);

        return contract;
    }

    private void createContract(StaffView staff, PromotionView promotion, LocalDate startDate, LocalDate endDate) {
        StaffContract contract = new StaffContract();
        contract.setStaff(staff);
        contract.setPromotion(promotion);
        contract.setMonthlyCost(ContractUtils.calculateStaffContractCost(staff));
        contract.setEndDate(endDate);
        contract.setStartDate(startDate);
        promotion.addToStaff(staff);
        contractManager.addContract(contract);
        contractManager.buyOutContracts(staff, promotion, startDate);
        staff.setStaffContract(contract);
    }
}
