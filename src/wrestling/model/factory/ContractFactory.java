package wrestling.model.factory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import wrestling.model.dirt.News;
import wrestling.model.Contract;
import wrestling.model.controller.GameController;
import wrestling.model.Promotion;
import wrestling.model.Worker;
import wrestling.model.controller.ContractManager;
import wrestling.model.utility.ModelUtilityFunctions;

/**
 * attached to the gameController, it is called whenever a new contract is to be
 * created
 *
 */
public class ContractFactory {

    private final GameController gameController;
    private final ContractManager contractManager;

    public ContractFactory(GameController gameController) {
        this.gameController = gameController;
        this.contractManager = gameController.getContractManager();
    }

    //create a contract with predetermined attributes
    public void createContract(Worker worker, Promotion promotion, boolean exclusive, int duration, LocalDate startDate) {
        //create the contract
        Contract contract = createContract(worker, promotion);

        contract.setDuration(duration);

        if (exclusive) {
            setBiWeeklyCost(contract);
        } else {
            setAppearanceCost(contract);
        }

        contract.setStartDate(startDate);
    }

    //create a contract with set exclusivity (only used by import)
    public void createContract(Worker worker, Promotion promotion, LocalDate startDate, boolean exclusive) {

        //create the contract
        Contract contract = createContract(worker, promotion);

        //exclusive contracts are default for top level promotions
        if (exclusive) {

            contract.setExclusive(true);
            setBiWeeklyCost(contract);

            //'buy out' any the other contracts the worker has
            for (Contract c : contractManager.getContracts(worker)) {
                if (!c.getPromotion().equals(promotion)) {
                    contractManager.buyOutContract(c);
                }
            }
        } else {
            contract.setExclusive(false);
            setAppearanceCost(contract);
        }

        int duration = 30 + ModelUtilityFunctions.randRange(0, 30);

        //scale the duration and exclusivity based on promotion level
        for (int i = 0; i < promotion.getLevel(); i++) {
            duration += 30;
        }

        contract.setDuration(duration);
        contract.setStartDate(startDate);

        reportSigning(contract);
    }

    //create a default contract
    public void createContract(Worker worker, Promotion promotion, LocalDate startDate) {

        //create the contract
        Contract contract = createContract(worker, promotion);

        //exclusive contracts are default for top level promotions
        if (promotion.getLevel() == 5) {

            contract.setExclusive(true);
            setBiWeeklyCost(contract);

            //'buy out' any the other contracts the worker has
            for (Contract c : contractManager.getContracts(worker)) {

                if (!c.getPromotion().equals(promotion)) {
                    contractManager.buyOutContract(c);
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

        reportSigning(contract);
    }

    private Contract createContract(Worker worker, Promotion promotion) {
        Contract contract = new Contract();
        contract.setWorker(worker);
        contract.setPromotion(promotion);
        contractManager.addContract(contract);
        return contract;
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

        List<Integer> pricePoints = new ArrayList<>();

        pricePoints.addAll(Arrays.asList(0, 10, 20, 50, 75, 100, 250, 500, 1000, 10000, 100000));

        double nearest10 = contract.getWorker().getPopularity() / 10 * 10;
        double multiplier = (contract.getWorker().getPopularity() - nearest10) / 10;

        int ppIndex = (int) nearest10 / 10;

        int unitCost = pricePoints.get(ppIndex);

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

    private void reportSigning(Contract c) {
        StringBuilder sb = new StringBuilder();
        sb.append(c.getPromotion().getShortName()).append(" signed ").append(c.getWorker().getName())
                .append(" on ").append(c.getStartDate());

        gameController.getDirtSheet().newDirt(new News(sb.toString(), c.getWorker(), c.getPromotion()));
    }

    public void reportExpiration(Contract c) {
        StringBuilder sb = new StringBuilder();
        sb.append("Contract between ").append(c.getWorker()).append(" and ").append(c.getPromotion()).append(" has expired");
        gameController.getDirtSheet().newDirt(new News(sb.toString(), c.getWorker(), c.getPromotion()));
    }

}
