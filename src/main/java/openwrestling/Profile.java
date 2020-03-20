package openwrestling;

import openwrestling.model.controller.GameController;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.Segment;
import openwrestling.model.gameObjects.financial.BankAccount;
import openwrestling.model.gameObjects.financial.Transaction;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class Profile extends Logging {
    public void printEval(GameController gameController, List<Long> dayTimes) {
        logger.log(Level.DEBUG, "PROFILE GAME DATA");

        double average = dayTimes.stream().mapToLong(val -> val).average().orElse(0.0);
        long longest = Collections.max(dayTimes);

        logger.log(Level.DEBUG, String.format("Longest day: %s", longest));
        logger.log(Level.DEBUG, String.format("Average day: %s", average));

        gameController.getPromotionManager().getPromotions().forEach(promotion -> {
            logger.log(Level.DEBUG, String.format("%s %s %s", promotion.getName(), promotion.getLevel(), promotion.getPopularity()));
            List<Event> pastEvents = gameController.getEventManager().getPastEvents(promotion, gameController.getDateManager().today());
            logger.log(Level.DEBUG, "Events (past): " + pastEvents.size());
            List<Event> futureEvents = gameController.getEventManager().getFutureEvents(promotion, gameController.getDateManager().today());
            logger.log(Level.DEBUG, "Events (future): " + futureEvents.size());
            BankAccount bankAccount = gameController.getBankAccountManager().getBankAccount(promotion);
            logger.log(Level.DEBUG, "Funds: " + bankAccount.getFunds());
            logger.log(Level.DEBUG, "Transactions: " + gameController.getBankAccountManager().getTransactions(promotion).size());
            gameController.getBankAccountManager().getTransactions(promotion).stream()
                    .collect(groupingBy(Transaction::getType, toList()))
                    .forEach((transactionType, transactions) -> {
                        logger.log(Level.DEBUG, String.format("%s %d %d",
                                transactionType.toString(),
                                transactions.size(),
                                transactions.stream()
                                        .mapToLong(Transaction::getAmount)
                                        .sum()));
                    });


        });
        Map<Long, List<Segment>> map = new HashMap<>();
        gameController.getSegmentManager().getSegments().forEach(segment -> {
            segment.getMatchParticipants().forEach(worker -> {
                if (!map.containsKey(worker.getWorkerID())) {
                    map.put(worker.getWorkerID(), new ArrayList<>());
                    map.get(worker.getWorkerID()).add(segment);
                } else {
                    map.get(worker.getWorkerID()).add(segment);
                }
            });
        });


        map.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getValue().size()))
                .forEach(workerListEntry -> {
                    logger.log(Level.DEBUG, String.format("%s had %d matches",
                            gameController.getWorkerManager().getWorker(workerListEntry.getKey()).getName(),
                            workerListEntry.getValue().size()));
                });
    }
}
