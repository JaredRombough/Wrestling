package wrestling.model.factory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import wrestling.model.Contract;
import wrestling.model.GameController;
import wrestling.model.Promotion;
import wrestling.model.Worker;

/**
 * attached to the gameController, it is called whenever a new contract is to be
 * created
 *
 */
public class ContractFactory {

    private GameController gc;

    public ContractFactory(GameController gameController) {
        this.gc = gameController;
    }

    //create a contract with predetermined attributes
    public void createContract(Worker worker, Promotion promotion, boolean exclusive, int duration, LocalDate startDate) {
        //create the contract
        Contract contract = new Contract();

        contract.setWorker(worker);
        contract.setPromotion(promotion);

        contract.setDuration(duration);

        if (exclusive) {
            setBiWeeklyCost(contract);
        } else {
            setAppearanceCost(contract);
        }

        contract.setStartDate(startDate);

        //assign the contract
        promotion.addContract(contract);
        worker.addContract(contract);
    }

    //create a contract with set exclusivity (only used by import)
    public void createContract(Worker worker, Promotion promotion, LocalDate startDate, boolean exclusive) {

        //create the contract
        Contract contract = new Contract();

        contract.setWorker(worker);
        contract.setPromotion(promotion);

        //exclusive contracts are default for top level promotions
        if (exclusive) {

            contract.setExclusive(true);
            setBiWeeklyCost(contract);

            //'buy out' any the other contracts the worker has
            for (Contract c : worker.getContracts()) {

                if (!c.getPromotion().equals(promotion)) {

                    c.buyOutContract();
                }
            }
        } else {
            contract.setExclusive(false);
            setAppearanceCost(contract);
        }

        int duration = 30;

        //scale the duration and exclusivity based on promotion level
        for (int i = 0; i < promotion.getLevel(); i++) {
            duration += 30;
        }

        contract.setDuration(duration);
        contract.setStartDate(startDate);

        //assign the contract
        promotion.addContract(contract);
        worker.addContract(contract);

        reportSigning(promotion, worker, startDate);

    }

    //create a default contract
    public void createContract(Worker worker, Promotion promotion, LocalDate startDate) {

        //create the contract
        Contract contract = new Contract();

        contract.setWorker(worker);
        contract.setPromotion(promotion);

        //exclusive contracts are default for top level promotions
        if (promotion.getLevel() == 5) {

            contract.setExclusive(true);
            setBiWeeklyCost(contract);

            //'buy out' any the other contracts the worker has
            for (Contract c : worker.getContracts()) {

                if (!c.getPromotion().equals(promotion)) {

                    c.buyOutContract();
                }
            }
        } else {
            contract.setExclusive(false);
            setAppearanceCost(contract);
        }

        int duration = 30;

        //scale the duration and exclusivity based on promotion level
        for (int i = 0; i < promotion.getLevel(); i++) {
            duration += 30;
        }

        contract.setDuration(duration);
        contract.setStartDate(startDate);

        //assign the contract
        promotion.addContract(contract);
        worker.addContract(contract);

        reportSigning(promotion, worker, startDate);

    }

    public int calculateAppearanceCost(Worker worker, boolean exclusive) {
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

        int unitCost = 0;

        List<Integer> pricePoints = new ArrayList<>();

        pricePoints.addAll(Arrays.asList(0, 10, 20, 50, 75, 100, 250, 500, 1000, 10000, 100000));

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

    private void reportSigning(Promotion p, Worker w, LocalDate d) {
        StringBuilder sb = new StringBuilder();
        sb.append(p.getShortName()).append(" pop ").append(p.getPopulatirty()).append(" signed ").append(w.getName())
                .append(" pop ").append(w.getPopularity()).append(" on ").append(d);
        gc.newDirt(sb.toString());
    }
    
    public void reportExpiration(Contract c)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Contract between ").append(c.getWorker()).append(" and ").append(c.getPromotion()).append(" has expired");
        gc.newDirt(sb.toString());
    }

}
