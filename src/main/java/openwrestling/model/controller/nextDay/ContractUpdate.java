package openwrestling.model.controller.nextDay;

import lombok.Builder;
import openwrestling.Logging;
import openwrestling.manager.ContractManager;
import openwrestling.manager.DateManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.factory.ContractFactory;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.utility.ModelUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Builder
public class ContractUpdate extends Logging {

    private PromotionManager promotionManager;
    private DateManager dateManager;
    private WorkerManager workerManager;
    private ContractManager contractManager;
    private ContractFactory contractFactory;

    private List<Worker> freeAgents;

    public void updateContracts(Map<Long, Contract> contractMap) {
        updateExpiringContracts(contractMap);
    }

    public List<Contract> getNewContracts() {
        List<Contract> newContracts = new ArrayList<>();
        promotionManager.getPromotions()
                .forEach(promotion -> {
                    freeAgents = getFreeAgentsForPromotion(promotion, newContracts);
                    newContracts.addAll(getNewContractsToReplaceExpiring(promotion));
                });

        return newContracts;
    }

    private List<Worker> getFreeAgentsForPromotion(Promotion promotion, List<Contract> newContracts) {
        return new ArrayList<>(workerManager.freeAgents(promotion)).stream()
                .filter(worker -> worker.getPopularity() <= ModelUtils.maxPopularity(promotion))
                .filter(worker -> contractManager.getContracts(worker).size() < 3)
                .filter(worker ->
                        newContracts.stream()
                                .filter(contract -> contract.getWorker().equals(worker))
                                .filter(Contract::isExclusive)
                                .noneMatch(obj -> true))
                .filter(worker ->
                        newContracts.stream()
                                .filter(contract -> contract.getWorker().equals(worker))
                                .filter(contract -> !contract.isExclusive())
                                .count() < 3)
                .collect(Collectors.toList());
    }

    private List<Contract> getNewContractsToReplaceExpiring(Promotion promotion) {
        List<Contract> contracts = new ArrayList<>();
        int activeRosterSize = contractManager.getActiveRoster(promotion).size();
        while (activeRosterSize < idealRosterSize(promotion) && !freeAgents.isEmpty()) {
            Contract contract = contractFactory.contractForNextDay(freeAgents.get(0), promotion, dateManager.today());
            freeAgents.remove(contract.getWorker());
            contracts.add(contract);
            activeRosterSize++;
        }
        return contracts;
    }

    private void updateExpiringContracts(Map<Long, Contract> contractMap) {
        contractManager.getContracts().stream()
                .filter(Contract::isActive)
                .filter(contract -> contract.getEndDate().equals(dateManager.today()))
                .forEach(contract -> {
                    if (contractMap.containsKey(contract.getContractID())) {
                        contractMap.get(contract.getContractID()).setActive(false);
                    } else {
                        contract.setActive(false);
                        contractMap.put(contract.getContractID(), contract);
                    }
                });
    }


    private int idealRosterSize(Promotion promotion) {
        return 10 + (promotion.getLevel() * 10);
    }

}
