package openwrestling.model.controller.nextDay;

import lombok.Builder;
import openwrestling.Logging;
import openwrestling.manager.ContractManager;
import openwrestling.manager.DateManager;
import openwrestling.manager.NewsManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.TitleManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.NewsItem;
import openwrestling.model.factory.ContractFactory;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.StaffContract;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.financial.Transaction;
import openwrestling.model.segment.constants.TransactionType;
import openwrestling.model.utility.ContractUtils;
import openwrestling.model.utility.ModelUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static openwrestling.model.utility.PromotionUtils.idealRosterSize;


@Builder
public class DailyContractUpdate extends Logging {

    private final PromotionManager promotionManager;
    private final DateManager dateManager;
    private final WorkerManager workerManager;
    private final ContractManager contractManager;
    private final ContractFactory contractFactory;
    private final NewsManager newsManager;
    private final TitleManager titleManager;

    private List<Worker> freeAgents;

    public List<Contract> getNewContracts(LocalDate today) {

        List<Contract> expiringContracts = contractManager.getContracts().stream()
                .filter(Contract::isActive)
                .filter(contract -> today.equals(contract.getEndDate()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(expiringContracts)) {
            return new ArrayList<>();
        }

        List<Contract> newContracts = new ArrayList<>();
        expiringContracts.forEach(expiringContract -> {
            Promotion promotion = promotionManager.refreshPromotion(expiringContract.getPromotion());
            freeAgents = getFreeAgentsForPromotion(promotion, newContracts);
            newContracts.addAll(getNewContractsToReplaceExpiring(promotion));
        });

        promotionManager.getAiPromotions().forEach(promotion -> {
            int activeRosterSize = workerManager.getRoster(promotion).size();
            int toSign = idealRosterSize(promotion) - activeRosterSize;
            if (toSign > 0) {
                List<Worker> freeAgents = getFreeAgentsForPromotion(promotion, newContracts);
                for (int i = 0; i < toSign && i < freeAgents.size(); i++) {
                    newContracts.add(contractFactory.contractForNextDay(freeAgents.get(0), promotion, dateManager.today()));
                }
            }
        });

        return newContracts;
    }

    private List<Worker> getFreeAgentsForPromotion(Promotion promotion, List<Contract> newContracts) {
        Map<Worker, List<Contract>> newContractMap = newContracts.stream()
                .collect(Collectors.groupingBy(Contract::getWorker));
        return new ArrayList<>(workerManager.freeAgents(promotion)).stream()
                .filter(worker -> worker.getPopularity() <= ModelUtils.maxPopularity(promotion))
                .filter(worker -> contractManager.getContracts(worker).size() < 3)
                .filter(worker -> !newContractMap.containsKey(worker) ||
                        newContractMap.get(worker).stream()
                                .filter(Contract::isExclusive)
                                .noneMatch(obj -> true))
                .filter(worker -> !newContractMap.containsKey(worker) ||
                        newContractMap.get(worker).stream()
                                .filter(contract -> contract.getWorker().equals(worker))
                                .filter(contract -> !contract.isExclusive())
                                .count() < 3)
                .collect(Collectors.toList());
    }

    public List<NewsItem> getNewContractsNewsItems(List<Contract> newContracts) {
        return newContracts.stream()
                .map(contract -> newsManager.getNewContractNewsItem(contract, dateManager.today()))
                .collect(Collectors.toList());
    }

    public List<NewsItem> getExpiringContractsNewsItems(List<Contract> updatedContracts) {
        List<NewsItem> expiringContractNewsItems = new ArrayList<>();
        updatedContracts.forEach(contract -> {
            if (contract.getEndDate().equals(dateManager.today())) {
                expiringContractNewsItems.add(
                        newsManager.getExpiringContractNewsItem(contract, dateManager.today())
                );
            }
        });
        return expiringContractNewsItems;
    }

    public List<Transaction> getNewContractTransactions(List<Contract> newContracts) {
        return newContracts.stream()
                .map(contract -> Transaction.builder()
                        .type(TransactionType.WORKER_MONTHLY)
                        .date(dateManager.today())
                        .amount(ContractUtils.calculateSigningFee(contract.getWorker(), dateManager.today()))
                        .promotion(contract.getPromotion())
                        .build())
                .collect(Collectors.toList());
    }


    private List<Contract> getNewContractsToReplaceExpiring(Promotion promotion) {
        List<Contract> contracts = new ArrayList<>();
        int activeRosterSize = workerManager.getRoster(promotion).size();
        while (activeRosterSize < idealRosterSize(promotion) && !freeAgents.isEmpty()) {
            Contract contract = contractFactory.contractForNextDay(freeAgents.get(0), promotion, dateManager.today());
            freeAgents.remove(contract.getWorker());
            contracts.add(contract);
            activeRosterSize++;
        }
        return contracts;
    }

    public void updateExpiringContracts() {
        contractManager.getContracts().stream()
                .filter(Contract::isActive)
                .filter(contract -> contract.getEndDate().equals(dateManager.today()))
                .forEach(contract -> {
                    contractManager.terminateContract(contract, dateManager.today());
                    titleManager.stripTitlesForExpiringContract(contract);
                });

        contractManager.getStaffContracts().stream()
                .filter(StaffContract::isActive)
                .filter(contract -> contract.getEndDate().equals(dateManager.today()))
                .forEach(contract -> contractManager.terminateStaffContract(contract, dateManager.today()));
    }

}
