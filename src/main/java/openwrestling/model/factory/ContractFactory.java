package openwrestling.model.factory;

import java.time.LocalDate;
import org.apache.commons.lang3.RandomUtils;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.StaffContract;
import openwrestling.model.interfaces.iContract;
import openwrestling.model.manager.ContractManager;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.modelView.StaffView;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.utility.ContractUtils;

public class ContractFactory {

    private final ContractManager contractManager;

    public ContractFactory(ContractManager contractManager) {
        this.contractManager = contractManager;
    }

    public void createContract(StaffView staff, Promotion promotion, LocalDate startDate) {
        int duration = 1 + RandomUtils.nextInt(0, 2);
        for (int i = 0; i < promotion.getLevel(); i++) {
            duration += 1;
        }
        createContract(staff, promotion, startDate, ContractUtils.contractEndDate(startDate, duration));
    }

    public void createContract(StaffView staff, Promotion promotion, LocalDate startDate, int duration) {
        createContract(staff, promotion, startDate, ContractUtils.contractEndDate(startDate, duration));
    }

    public void createContract(Worker worker, Promotion promotion, LocalDate startDate, boolean exclusive) {
        createContract(worker, promotion, exclusive, RandomUtils.nextInt(0, 12), startDate);
    }

    public void createContract(Worker worker, Promotion promotion, LocalDate startDate) {
        createContract(worker, promotion, startDate, promotion.getLevel() == 5);
    }

    public iContract createContract(Worker worker, Promotion promotion, boolean exclusive, int duration, LocalDate startDate) {
        Contract contract = new Contract(startDate, worker, promotion);
        contract.setExclusive(exclusive);
        contract.setEndDate(ContractUtils.contractEndDate(startDate, duration));

        if (exclusive) {
            contract.setMonthlyCost(ContractUtils.calculateWorkerContractCost(worker, true));
            contractManager.buyOutContracts(worker, promotion, startDate);
            contractManager.paySigningFee(startDate, contract);
        } else {
            contract.setAppearanceCost(ContractUtils.calculateWorkerContractCost(worker, false));
        }

        contractManager.addContract(contract);
        promotion.addToRoster(worker);
        worker.addContract(contract);

        return contract;
    }

    private void createContract(StaffView staff, Promotion promotion, LocalDate startDate, LocalDate endDate) {
        StaffContract contract = new StaffContract(startDate, staff, promotion);
        contract.setMonthlyCost(ContractUtils.calculateStaffContractCost(staff));
        contract.setEndDate(endDate);
        promotion.addToStaff(staff);
        contractManager.addContract(contract);
        contractManager.buyOutContracts(staff, promotion, startDate);
        contractManager.paySigningFee(startDate, contract);
        staff.setStaffContract(contract);
    }
}
