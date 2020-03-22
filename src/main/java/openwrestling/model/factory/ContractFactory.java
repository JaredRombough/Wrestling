package openwrestling.model.factory;

import openwrestling.manager.ContractManager;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.StaffContract;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.utility.ContractUtils;
import org.apache.commons.lang3.RandomUtils;

import java.time.LocalDate;

public class ContractFactory {

    private final ContractManager contractManager;

    public ContractFactory(ContractManager contractManager) {
        this.contractManager = contractManager;
    }

    public StaffContract createContract(StaffMember staff, Promotion promotion, LocalDate startDate) {
        int duration = 1 + RandomUtils.nextInt(0, 2);
        for (int i = 0; i < promotion.getLevel(); i++) {
            duration += 1;
        }
        return createContract(staff, promotion, startDate, ContractUtils.contractEndDate(startDate, duration));
    }

    public StaffContract createContract(StaffMember staff, Promotion promotion, LocalDate startDate, int duration) {
        return createContract(staff, promotion, startDate, ContractUtils.contractEndDate(startDate, duration));
    }

    public Contract createContract(Worker worker, Promotion promotion, LocalDate startDate, boolean exclusive) {
        return createContract(worker, promotion, exclusive, RandomUtils.nextInt(0, 12), startDate);
    }

    public Contract createContract(Worker worker, Promotion promotion, LocalDate startDate) {
        return createContract(worker, promotion, startDate, promotion.getLevel() == 5);
    }

    public Contract createContract(Worker worker, Promotion promotion, boolean exclusive, int duration, LocalDate startDate) {
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

        worker.addContract(contract);

        return contract;
    }

    public Contract contractForNextDay(Worker worker, Promotion promotion, LocalDate startDate) {
        boolean exclusive = promotion.getLevel() == 5;

        Contract contract = new Contract(startDate, worker, promotion);
        contract.setExclusive(exclusive);
        contract.setEndDate(startDate.plusDays(90));

        if (exclusive) {
            contract.setMonthlyCost(ContractUtils.calculateWorkerContractCost(worker, true));
        } else {
            contract.setAppearanceCost(ContractUtils.calculateWorkerContractCost(worker, false));
        }

        return contract;
    }

    private StaffContract createContract(StaffMember staff, Promotion promotion, LocalDate startDate, LocalDate
            endDate) {
        StaffContract contract = new StaffContract(startDate, staff, promotion);
        contract.setMonthlyCost(ContractUtils.calculateStaffContractCost(staff));
        contract.setEndDate(endDate);
        return contract;
    }
}
