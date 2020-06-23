package openwrestling.model.controller.nextDay;

import lombok.Builder;
import openwrestling.Logging;
import openwrestling.manager.ContractManager;
import openwrestling.manager.DateManager;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.financial.Transaction;
import openwrestling.model.segmentEnum.TransactionType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Builder
public class DailyTransactions extends Logging {

    private DateManager dateManager;
    private ContractManager contractManager;

    public List<Transaction> getPayDayTransactions() {
        if (!dateManager.isPayDay()) {
            return List.of();
        }
        List<Transaction> transactions = new ArrayList<>();

        List<Transaction> workerTransactions = contractManager.getContracts().stream()
                .filter(Contract::isExclusive)
                .filter(contract -> contract.getMonthlyCost() > 0)
                .map(contract -> Transaction.builder()
                        .promotion(contract.getPromotion())
                        .type(TransactionType.WORKER)
                        .date(dateManager.today())
                        .amount(contract.getMonthlyCost())
                        .build())
                .collect(Collectors.toList());

        transactions.addAll(workerTransactions);

        List<Transaction> staffTransactions = contractManager.getStaffContracts().stream()
                .filter(contract -> contract.getMonthlyCost() > 0)
                .map(contract -> Transaction.builder()
                        .promotion(contract.getPromotion())
                        .type(TransactionType.STAFF)
                        .date(dateManager.today())
                        .amount(contract.getMonthlyCost())
                        .build())
                .collect(Collectors.toList());

        transactions.addAll(staffTransactions);

        return transactions;
    }

}
