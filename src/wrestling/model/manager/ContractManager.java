package wrestling.model.manager;

import java.io.Serializable;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.Contract;
import wrestling.model.StaffContract;
import wrestling.model.interfaces.iContract;
import wrestling.model.interfaces.iPerson;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.segmentEnum.TransactionType;
import wrestling.model.utility.ContractUtils;

public class ContractManager implements Serializable {

    private final List<Contract> contracts;
    private final List<StaffContract> staffContracts;

    private final PromotionManager promotionManager;
    private final TitleManager titleManager;

    public ContractManager(PromotionManager promotionManager, TitleManager titleManager) {
        contracts = new ArrayList<>();
        staffContracts = new ArrayList<>();
        this.promotionManager = promotionManager;
        this.titleManager = titleManager;
    }

    public void dailyUpdate(LocalDate date) {
        for (Contract contract : contracts) {
            if (!nextDay(contract, date)) {
                titleManager.stripTitles(contract);
            }
        }

        for (StaffContract contract : staffContracts) {
            nextDay(contract, date);
        }
    }

    public void addContract(Contract contract) {
        contracts.add(contract);
    }

    public void addContract(StaffContract contract) {
        staffContracts.add(contract);
    }

    public List<Contract> getContracts(PromotionView promotion) {
        List<Contract> promotionContracts = new ArrayList<>();
        for (Contract contract : contracts) {
            if (contract.isActive() && contract.getPromotion().equals(promotion)) {
                promotionContracts.add(contract);
            }
        }

        return promotionContracts;
    }

    public List<StaffContract> getStaffContracts(PromotionView promotion) {
        List<StaffContract> promotionStaffContracts = new ArrayList<>();
        for (StaffContract contract : staffContracts) {
            if (contract.isActive() && contract.getPromotion().equals(promotion)) {
                promotionStaffContracts.add(contract);
            }
        }
        return promotionStaffContracts;
    }

    public List<iContract> allContracts() {
        List<iContract> allContracts = new ArrayList<>();
        allContracts.addAll(contracts);
        allContracts.addAll(staffContracts);
        return allContracts;
    }

    public List<? extends iContract> getContracts(iPerson person) {
        return person instanceof WorkerView
                ? getContracts((WorkerView) person)
                : getContracts((StaffView) person);
    }

    public List<Contract> getContracts(WorkerView worker) {
        List<Contract> workerContracts = new ArrayList<>();
        for (Contract contract : contracts) {
            if (contract.isActive() && contract.getWorker().equals(worker)) {
                workerContracts.add(contract);
            }
        }

        return workerContracts;
    }

    public List<StaffContract> getContracts(StaffView staff) {
        List<StaffContract> contractsForStaff = new ArrayList<>();
        for (StaffContract contract : staffContracts) {
            if (contract.isActive() && contract.getStaff().equals(staff)) {
                contractsForStaff.add(contract);
            }
        }

        return contractsForStaff;
    }

    public StaffContract getContract(StaffView staff) {
        StaffContract staffContract = null;
        for (StaffContract contract : staffContracts) {
            if (contract.isActive() && contract.getStaff().equals(staff)) {
                staffContract = contract;
                break;
            }
        }

        return staffContract;
    }

    public Contract getContract(WorkerView worker, PromotionView promotion) {
        Contract workerContract = null;
        for (Contract contract : contracts) {
            if (contract.isActive() && contract.getWorker().equals(worker)
                    && contract.getPromotion().equals(promotion)) {
                workerContract = contract;
                break;
            }
        }

        return workerContract;
    }

    public List<WorkerView> getActiveRoster(PromotionView promotion) {

        List<WorkerView> roster = new ArrayList<>();
        for (Contract contract : contracts) {
            if (contract.isActive() && contract.getPromotion().equals(promotion)
                    && contract.getWorker().isFullTime() && !contract.getWorker().isManager()) {
                roster.add(contract.getWorker());
            }
        }

        return roster;
    }

    public List<WorkerView> getPushed(PromotionView promotion) {
        List<WorkerView> roster = new ArrayList<>();
        for (Contract contract : contracts) {
            if (contract.isActive() && contract.getPromotion().equals(promotion)
                    && contract.isPushed()) {
                roster.add(contract.getWorker());
            }

        }

        return roster;
    }

    //depreciates monthly contracts
    public boolean nextDay(iContract contract, LocalDate today) {
        if (contract.getEndDate().isBefore(today)) {
            terminateContract(contract);
            return false;
        }

        return true;
    }

    //handles appearance-based contracts
    public void appearance(LocalDate date, Contract contract) {
        //make the promotion 'pay' the worker for the appearance
        promotionManager.getBankAccount(contract.getPromotion()).removeFunds(contract.getAppearanceCost(), TransactionType.WORKER, date);
    }

    public void payDay(LocalDate date, Contract contract) {
        if (contract.getMonthlyCost() != 0) {
            promotionManager.getBankAccount(contract.getPromotion()).removeFunds(Math.toIntExact(contract.getMonthlyCost()),
                    TransactionType.WORKER, date);
        }
    }

    public void payDay(LocalDate date, StaffContract contract) {
        if (contract.getMonthlyCost() != 0) {
            promotionManager.getBankAccount(contract.getPromotion()).removeFunds(contract.getMonthlyCost(),
                    TransactionType.STAFF, date);
        }
    }

    public void paySigningFee(LocalDate date, iContract contract) {
        promotionManager.getBankAccount(contract.getPromotion()).removeFunds(
                ContractUtils.calculateSigningFee(contract.getPerson(), date),
                contract.getPerson() instanceof WorkerView ? TransactionType.WORKER : TransactionType.STAFF,
                date);
    }

    public void buyOutContracts(WorkerView worker, PromotionView newExclusivePromotion, LocalDate buyOutDate) {
        //'buy out' any the other contracts the worker has
        for (Contract c : getContracts(worker)) {
            if (!c.getPromotion().equals(newExclusivePromotion)) {
                c.setEndDate(buyOutDate);
            }
        }
    }

    public void buyOutContracts(StaffView staff, PromotionView newExclusivePromotion, LocalDate buyOutDate) {
        //'buy out' any the other contracts the worker has
        for (StaffContract c : getContracts(staff)) {
            if (!c.getPromotion().equals(newExclusivePromotion)) {
                c.setEndDate(buyOutDate);
            }
        }
    }

    public void terminateContract(iContract contract) {
        contract.getPromotion().removeFromRoster(contract.getWorker());
        contract.getPromotion().removeFromStaff(contract.getStaff());
        if (contract.getWorker() != null) {
            contract.getWorker().removeContract((Contract) contract);
        } else if (contract.getStaff() != null) {
            contract.getStaff().setStaffContract(null);
        }
        contract.setActive(false);
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

    public boolean canNegotiate(iPerson person, PromotionView promotion) {
        //this would have to be more robust
        //such as checking how much time is left on our contract
        boolean canNegotiate = true;

        for (iContract contract : person.getContracts()) {
            if (contract.isExclusive() || contract.getPromotion().equals(promotion)) {
                canNegotiate = false;
            }
        }

        return canNegotiate;
    }

    public String contractString(WorkerView worker) {

        StringBuilder bld = new StringBuilder();
        for (Contract current : getContracts(worker)) {

            bld.append(getTerms(current));
            bld.append("\n");
        }
        return bld.toString();
    }

    public String contractPromotionsString(iPerson person, LocalDate date) {
        StringBuilder bld = new StringBuilder();
        for (iContract current : getContracts(person)) {
            if (!bld.toString().isEmpty()) {
                bld.append("/");
            }
            bld.append(current.getPromotion().getShortName());
        }
        return bld.toString();
    }

    public String contractTermsString(WorkerView worker, LocalDate date) {
        StringBuilder bld = new StringBuilder();
        for (Contract current : getContracts(worker)) {
            if (!bld.toString().isEmpty()) {
                bld.append("/");
            }
            if (current.isExclusive()) {
                bld.append(String.format("%s (%d day%s)",
                        current.getPromotion().getShortName(),
                        DAYS.between(date, current.getEndDate()),
                        DAYS.between(date, current.getEndDate()) > 1 ? "s" : ""));
            } else {
                bld.append(current.getPromotion().getShortName());
            }
        }
        return bld.toString();
    }

    public int averageWorkerPopularity(PromotionView promotion) {
        int totalPop = 0;
        int averagePop = 0;

        if (!promotion.getFullRoster().isEmpty()) {
            for (WorkerView worker : promotion.getFullRoster()) {
                totalPop += worker.getPopularity();
            }
            averagePop = totalPop / promotion.getFullRoster().size();
        }

        return averagePop;
    }
}
