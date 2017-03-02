package wrestling.model.factory;

import wrestling.model.Contract;
import wrestling.model.Promotion;
import wrestling.model.Worker;

/**
 * attached to the gameController, it is called whenever a new contract is to be
 * created
 *
 */
public final class ContractFactory {

    //create a contract with predetermined attributes
    public static void createContract(Worker worker, Promotion promotion, boolean monthly, boolean exclusive, int duration, int cost, int startDate) {
        //create the contract
        Contract contract = new Contract();

        contract.setWorker(worker);
        contract.setPromotion(promotion);

        contract.setMonthly(monthly);
        contract.setDuration(duration);
        contract.setUnitCost(cost);
        contract.setStartDate(startDate);

        //assign the contract
        promotion.addContract(contract);
        worker.addContract(contract);
    }

    //create a default contract
    public static void createContract(Worker worker, Promotion promotion) {

        //create the contract
        Contract contract = new Contract();

        contract.setWorker(worker);
        contract.setPromotion(promotion);

        contract.setMonthly(true);

        //exclusive contracts are default for top level promotions
        if (promotion.getLevel() == 5) {

            contract.setExclusive(true);

            //'buy out' any the other contracts the worker has
            for (Contract c : worker.getContracts()) {

                if (!c.getPromotion().equals(promotion)) {

                    c.buyOutContract();
                }
            }
        } else {
            contract.setExclusive(false);
        }

        int duration = 30;

        //scale the duration and exclusivity based on promotion level
        for (int i = 0; i < promotion.getLevel(); i++) {
            duration += 30;
        }

        contract.setDuration(duration);

        calculateCost(contract);

        //assign the contract
        promotion.addContract(contract);
        worker.addContract(contract);

    }

    /*
    calculate the cost for a contract if not explicitly specified
     */
    private static void calculateCost(Contract contract) {

        int unitCost = 0;

        for (int i = 0; i < contract.getWorker().getPopularity(); i++) {
            if (i < 50) {
                unitCost += 5;
            } else {
                unitCost += 10;
            }
        }

        if (contract.isExclusive()) {
            unitCost *= 1.5;
        }

        contract.setUnitCost(unitCost);

    }

}
