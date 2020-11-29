package openwrestling.manager;

import openwrestling.database.Database;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.StaffContract;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.interfaces.iContract;
import openwrestling.model.interfaces.iPerson;
import openwrestling.model.segment.constants.TransactionType;
import openwrestling.model.utility.ContractUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContractManager extends GameObjectManager implements Serializable {


    private final BankAccountManager bankAccountManager;
    private Map<Long, Contract> contractMap = new HashMap<>();
    private Map<Long, StaffContract> staffContractMap = new HashMap<>();

    public ContractManager(Database database,
                           BankAccountManager bankAccountManager) {
        super(database);
        this.bankAccountManager = bankAccountManager;
    }

    @Override
    public void selectData() {
        List<Contract> contracts = getDatabase().selectAll(Contract.class);
        contracts.forEach(contract -> contractMap.put(contract.getContractID(), contract));
        List<StaffContract> staffContracts = getDatabase().selectAll(StaffContract.class);
        staffContracts.forEach(contract -> staffContractMap.put(contract.getStaffContractID(), contract));
    }

    public List<Contract> getContracts() {
        return new ArrayList<>(contractMap.values());
    }

    public List<Contract> getActiveContracts() {
        return contractMap.values().stream()
                .filter(Contract::isActive)
                .collect(Collectors.toList());
    }

    public List<Contract> getExpiredContracts() {
        return contractMap.values().stream()
                .filter(contract -> !contract.isActive())
                .collect(Collectors.toList());
    }

    public List<StaffContract> getStaffContracts() {
        return new ArrayList<>(staffContractMap.values());
    }

    public List<Contract> createContracts(List<Contract> contracts) {
        List<Contract> saved = getDatabase().insertList(contracts);
        saved.forEach(contract -> contractMap.put(contract.getContractID(), contract));
        return saved;
    }

    public void updateContracts(List<Contract> contracts) {
        getDatabase().updateList(contracts);
        contracts.forEach(contract -> contractMap.put(contract.getContractID(), contract));
    }

    public void updateStaffContracts(List<StaffContract> staffContracts) {
        getDatabase().updateList(staffContracts);
        staffContracts.forEach(contract -> staffContractMap.put(contract.getStaffContractID(), contract));
    }

    public List<StaffContract> createStaffContracts(List<StaffContract> contracts) {
        List<StaffContract> saved = getDatabase().insertList(contracts);
        saved.forEach(contract -> staffContractMap.put(contract.getStaffContractID(), contract));
        return saved;
    }

    public List<Contract> getContracts(Promotion promotion) {
        List<Contract> promotionContracts = new ArrayList<>();
        for (Contract contract : contractMap.values()) {
            if (contract.isActive() && contract.getPromotion().equals(promotion)) {
                promotionContracts.add(contract);
            }
        }

        return promotionContracts;
    }


    public List<? extends iContract> getContracts(iPerson person) {
        return person instanceof Worker
                ? getContracts((Worker) person)
                : getContracts((StaffMember) person);
    }

    public List<Contract> getContracts(Worker worker) {
        List<Contract> workerContracts = new ArrayList<>();
        for (Contract contract : contractMap.values()) {
            if (contract.isActive() && contract.getWorker().equals(worker)) {
                workerContracts.add(contract);
            }
        }
        return workerContracts;
    }

    public List<StaffContract> getContracts(StaffMember staff) {
        List<StaffContract> contractsForStaff = new ArrayList<>();
        for (StaffContract contract : staffContractMap.values()) {
            if (contract.isActive() && contract.getStaff().equals(staff)) {
                contractsForStaff.add(contract);
            }
        }

        return contractsForStaff;
    }

    public iContract getContract(iPerson person, Promotion promotion) {
        if (person instanceof Worker) {
            return getContract((Worker) person, promotion);
        }
        return getStaffContract((StaffMember) person, promotion);
    }

    public StaffContract getStaffContract(StaffMember staffMember, Promotion promotion) {
        return getStaffContracts().stream()
                .filter(StaffContract::isActive)
                .filter(staffContract -> staffContract.getStaff().equals(staffMember))
                .filter(staffContract -> staffContract.getPromotion().equals(promotion))
                .findFirst()
                .orElse(null);
    }

    public Contract getContract(Worker worker, Promotion promotion) {
        Contract workerContract = null;
        for (Contract contract : contractMap.values()) {
            if (contract.isActive() && contract.getWorker().equals(worker)
                    && contract.getPromotion().equals(promotion)) {
                workerContract = contract;
                break;
            }
        }

        return workerContract;
    }

    public List<Worker> getPushed(Promotion promotion) {
        List<Worker> roster = new ArrayList<>();
        for (Contract contract : contractMap.values()) {
            if (contract.isActive() && contract.getPromotion().equals(promotion)
                    && contract.isPushed()) {
                roster.add(contract.getWorker());
            }

        }

        return roster;
    }

    public void nextDay(StaffContract contract, LocalDate today) {
        if (contract.getEndDate().isBefore(today)) {
            terminateStaffContract(contract, today);
        }
    }

    public void paySigningFee(LocalDate date, iContract contract) {
        TransactionType type;
        if (contract.getPerson() instanceof Worker) {
            type = TransactionType.WORKER_MONTHLY;
        } else {
            type = TransactionType.STAFF;
        }

        bankAccountManager.removeFunds(contract.getPromotion(),
                ContractUtils.calculateSigningFee(contract.getPerson(), date),
                type,
                date);
    }

    public void buyOutContracts(Worker worker, Promotion newExclusivePromotion, LocalDate buyOutDate) {
        //'buy out' any the other contracts the worker has
        for (Contract c : getContracts(worker)) {
            if (!c.getPromotion().equals(newExclusivePromotion)) {
                c.setEndDate(buyOutDate);
            }
        }
    }

    public void buyOutContracts(StaffMember staff, Promotion newExclusivePromotion, LocalDate buyOutDate) {
        //'buy out' any the other contracts the worker has
        for (StaffContract c : getContracts(staff)) {
            if (!c.getPromotion().equals(newExclusivePromotion)) {
                c.setEndDate(buyOutDate);
            }
        }
    }

    public void terminateContract(Contract contract, LocalDate date) {
        int fee = ContractUtils.calculateTerminationFee(contract, date);

        contract.setActive(false);
        contract.setEndDate(date);
        updateContracts(List.of(contract));

        if (fee > 0) {
            bankAccountManager.removeFunds(contract.getPromotion(),
                    fee,
                    TransactionType.WORKER_MONTHLY,
                    date);
        }
    }

    public void terminateStaffContract(StaffContract staffContract, LocalDate date) {
        int fee = ContractUtils.calculateTerminationFee(staffContract, date);

        staffContract.setActive(false);
        staffContract.setEndDate(date);
        updateStaffContracts(List.of(staffContract));

        if (fee > 0) {
            bankAccountManager.removeFunds(staffContract.getPromotion(),
                    fee,
                    TransactionType.STAFF,
                    date);
        }
    }

    public String getTerms(iContract contract) {
        String string = String.format("%s ending %s", contract.getPromotion().getShortName(), contract.getEndDate());

        if (contract.isExclusive()) {
            string += " $" + contract.getMonthlyCost() + " Monthly.";
        } else {
            string += " $" + contract.getAppearanceCost() + " per appearance.";
        }

        return string;
    }

    public boolean canNegotiate(iPerson person, Promotion promotion) {
        boolean canNegotiate = true;

        for (iContract contract : getContracts(person)) {
            if (contract.isExclusive() || contract.getPromotion().equals(promotion)) {
                canNegotiate = false;
            }
        }

        return canNegotiate;
    }

    public boolean canNegotiate(Worker worker, Promotion promotion) {
        boolean canNegotiate = true;

        for (iContract contract : getContracts(worker)) {
            if (contract.isExclusive() || contract.getPromotion().equals(promotion)) {
                canNegotiate = false;
            }
        }

        return canNegotiate;
    }

    public String contractPromotionsString(iPerson person) {
        StringBuilder bld = new StringBuilder();
        for (iContract current : getContracts(person)) {
            if (!bld.toString().isEmpty()) {
                bld.append("/");
            }
            bld.append(current.getPromotion().getShortName());
        }
        return bld.toString();
    }

}
