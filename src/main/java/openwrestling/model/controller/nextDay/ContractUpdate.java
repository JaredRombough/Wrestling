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
        return promotionManager.getPromotions().stream()
                .flatMap(promotion -> getNewContractsToReplaceExpiring(promotion).stream())
                .collect(Collectors.toList());
    }

    private List<Contract> getNewContractsToReplaceExpiring(Promotion promotion) {
        List<Contract> contracts = new ArrayList<>();
        freeAgents = new ArrayList<>(workerManager.freeAgents(promotion));
        int activeRosterSize = contractManager.getActiveRoster(promotion).size();
        while (activeRosterSize < idealRosterSize(promotion) && !workerManager.freeAgents(promotion).isEmpty()) {
            contracts.add(signContract(promotion));
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


    private Contract signContract(Promotion promotion) {
        Contract contract = null;
        for (Worker worker : freeAgents) {
            if (worker.getPopularity() <= ModelUtils.maxPopularity(promotion)) {
                contract = contractFactory.contractForNextDay(worker, promotion, dateManager.today());
                break;
            }
        }
        if (contract != null) {
            freeAgents.remove(contract.getWorker());
        }

        return contract;
    }


    private int idealRosterSize(Promotion promotion) {
        return 10 + (promotion.getLevel() * 10);
    }

}
