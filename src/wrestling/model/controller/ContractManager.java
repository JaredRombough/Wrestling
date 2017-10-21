package wrestling.model.controller;

import java.io.Serializable;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.Contract;
import wrestling.model.Promotion;
import wrestling.model.Worker;
import wrestling.model.utility.ModelUtilityFunctions;

public class ContractManager implements Serializable {

    private final List<Contract> contracts;

    public ContractManager() {
        contracts = new ArrayList<>();
    }

    public void addContract(Contract contract) {
        contracts.add(contract);
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

    public boolean hasContract(Worker worker) {
        boolean hasContract = false;
        for (Contract contract : contracts) {
            if (contract.isActive() && contract.getWorker().equals(worker));
            hasContract = true;
            break;
        }
        return hasContract;
    }

    public List<Contract> getContracts(Worker worker) {
        List<Contract> workerContracts = new ArrayList<>();
        for (Contract contract : contracts) {
            if (contract.isActive() && contract.getWorker().equals(worker)) {
                workerContracts.add(contract);
            }
        }

        return workerContracts;
    }

    public Contract getContract(Worker worker, Promotion promotion) {
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

    public List<Worker> getActiveRoster(Promotion promotion) {

        List<Worker> roster = new ArrayList<>();
        for (Contract contract : contracts) {
            if (contract.isActive() && contract.getPromotion().equals(promotion)
                    && contract.getWorker().isFullTime() && !contract.getWorker().isManager()) {
                roster.add(contract.getWorker());
            }
        }

        return roster;
    }

    public List<Worker> getFullRoster(Promotion promotion) {

        List<Worker> roster = new ArrayList<>();
        for (Contract contract : contracts) {
            if (contract.isActive() && contract.getPromotion().equals(promotion)) {
                roster.add(contract.getWorker());
            }

        }

        return roster;
    }

    public List<Worker> getPushed(Promotion promotion) {
        List<Worker> roster = new ArrayList<>();
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
    public boolean appearance(LocalDate date, Contract contract) {
        boolean stillExists = true;
        //make the promotion 'pay' the worker for the appearance
        contract.getPromotion().bankAccount().removeFunds(contract.getAppearanceCost(), 'w', date);

        if (contract.getDuration() <= 0) {
            terminateContract(contract);
            stillExists = false;
        }

        return stillExists;
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

            contract.getPromotion().bankAccount().removeFunds(Math.toIntExact(payment), 'w', date);

        }

    }

    //for when a bigger promotion signs a written contract
    //that overrides this open contract
    public void buyOutContract(Contract contract) {

        contract.setDuration(0);
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
    
    public boolean canNegotiate(Worker worker, Promotion promotion) {
        //this would have to be more robust
        //such as checking how much time is left on our contract
        boolean canNegotiate = true;

        if (worker.getPopularity() > ModelUtilityFunctions.maxPopularity(promotion)) {
            canNegotiate = false;
        }

        if (hasContract(worker)) {
            for (Contract contract : getContracts(worker)) {
                if (contract.isExclusive() || contract.getPromotion().equals(promotion)) {
                    canNegotiate = false;
                }
            }
        }

        return canNegotiate;
    }
    
    
}
