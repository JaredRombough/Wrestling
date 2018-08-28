package wrestling.model.manager;

import java.io.Serializable;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import wrestling.model.Contract;
import wrestling.model.Promotion;
import wrestling.model.StaffContract;
import wrestling.model.interfaces.iContract;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.WorkerView;

public class ContractManager implements Serializable {

    private final List<Contract> contracts;
    private final List<StaffContract> staffContracts;

    private final PromotionManager promotionManager;

    public ContractManager(PromotionManager promotionManager) {
        contracts = new ArrayList<>();
        staffContracts = new ArrayList<>();
        this.promotionManager = promotionManager;
    }

    public void addContract(Contract contract) {
        contracts.add(contract);
    }

    public void addContract(StaffContract contract) {
        staffContracts.add(contract);
    }

    public List<Contract> getContracts(Promotion promotion) {
        List<Contract> promotionContracts = new ArrayList<>();
        for (Contract contract : contracts) {
            if (contract.isActive() && contract.getPromotion().equals(promotion)) {
                promotionContracts.add(contract);
            }
        }

        return promotionContracts;
    }

    public List<iContract> allContracts() {
        List<iContract> allContracts = new ArrayList<>();
        allContracts.addAll(contracts);
        allContracts.addAll(staffContracts);
        return allContracts;
    }

    public boolean hasContract(WorkerView worker) {
        boolean hasContract = false;
        for (Contract contract : contracts) {
            if (contract.isActive() && contract.getWorker().equals(worker));
            hasContract = true;
            break;
        }
        return hasContract;
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
        List<StaffContract> workerContracts = new ArrayList<>();
        for (StaffContract contract : staffContracts) {
            if (contract.isActive() && contract.getStaff().equals(staff)) {
                workerContracts.add(contract);
            }
        }

        return workerContracts;
    }

    public Contract getContract(WorkerView worker, Promotion promotion) {
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

    public List<WorkerView> getActiveRoster(Promotion promotion) {

        List<WorkerView> roster = new ArrayList<>();
        for (Contract contract : contracts) {
            if (contract.isActive() && contract.getPromotion().equals(promotion)
                    && contract.getWorker().isFullTime() && !contract.getWorker().isManager()) {
                roster.add(contract.getWorker());
            }
        }

        return roster;
    }

    public List<WorkerView> getFullRoster(Promotion promotion) {

        List<WorkerView> roster = new ArrayList<>();
        for (Contract contract : contracts) {
            if (contract.isActive() && contract.getPromotion().equals(promotion)) {
                roster.add(contract.getWorker());
            }

        }

        return roster;
    }

    public List<WorkerView> getPushed(Promotion promotion) {
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
    public boolean nextDay(Contract contract) {
        boolean stillExists = true;
        contract.setDuration(contract.getDuration() - 1);

        if (contract.getDuration() <= 0) {
            terminateContract(contract);
            stillExists = false;
        }

        return stillExists;
    }

    //handles appearance-based contracts
    public void appearance(LocalDate date, Contract contract) {
        //make the promotion 'pay' the worker for the appearance
        promotionManager.getBankAccount(contract.getPromotion()).removeFunds(contract.getAppearanceCost(), 'w', date);
        if (contract.getDuration() <= 0) {
            terminateContract(contract);
        }
    }

    public void payDay(LocalDate date, Contract contract) {

        if (contract.getBiWeeklyCost() != 0) {

            long daysBetween = DAYS.between(contract.getStartDate(), date);
            long payment = 0;
            if (daysBetween < 14) {
                payment += contract.getBiWeeklyCost() * (daysBetween / 14);
            } else {
                payment = contract.getBiWeeklyCost();
            }

            promotionManager.getBankAccount(contract.getPromotion()).removeFunds(Math.toIntExact(payment), 'w', date);

        }

    }

    //for when a bigger promotion signs a written contract
    //that overrides this open contract
    public void buyOutContract(iContract contract) {
        contract.setDuration(0);
    }

    public void buyOutContracts(WorkerView worker, Promotion newExclusivePromotion) {
        //'buy out' any the other contracts the worker has
        for (Contract c : getContracts(worker)) {
            if (!c.getPromotion().equals(newExclusivePromotion)) {
                buyOutContract(c);
            }
        }
    }

    public void buyOutContracts(StaffView staff, Promotion newExclusivePromotion) {
        //'buy out' any the other contracts the worker has
        for (StaffContract c : getContracts(staff)) {
            if (!c.getPromotion().equals(newExclusivePromotion)) {
                buyOutContract(c);
            }
        }
    }

    private void terminateContract(Contract contract) {
        contract.setActive(false);
    }

    public String getTerms(Contract contract) {
        String string = contract.getPromotion().getName() + " Length: " + contract.getDuration()
                + " days. ";

        if (contract.isExclusive()) {
            string += "$" + contract.getBiWeeklyCost() + " Bi-Weekly.";
        } else {
            string += "$" + contract.getAppearanceCost() + " per appearance.";
        }

        return string;
    }

    public boolean canNegotiate(WorkerView worker, Promotion promotion) {
        //this would have to be more robust
        //such as checking how much time is left on our contract
        boolean canNegotiate = true;

        if (canNegotiate && hasContract(worker)) {
            for (Contract contract : getContracts(worker)) {
                if (contract.isExclusive() || contract.getPromotion().equals(promotion)) {
                    canNegotiate = false;
                }
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

    public int averageWorkerPopularity(Promotion promotion) {
        int totalPop = 0;
        int averagePop = 0;

        if (!getFullRoster(promotion).isEmpty()) {
            for (WorkerView worker : getFullRoster(promotion)) {
                totalPop += worker.getPopularity();
            }
            averagePop = totalPop / getFullRoster(promotion).size();
        }

        return averagePop;
    }
}
