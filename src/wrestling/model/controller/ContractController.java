package wrestling.model.controller;

import java.io.Serializable;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import wrestling.model.Contract;

public class ContractController implements Serializable {

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

        contract.getWorker().getController().removeContract(contract);

        contract.getPromotion().removeContract(contract);

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
}
