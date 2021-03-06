package openwrestling.model.controller.nextDay;

import lombok.Builder;
import openwrestling.Logging;
import openwrestling.manager.BankAccountManager;
import openwrestling.manager.ContractManager;
import openwrestling.manager.DateManager;
import openwrestling.manager.EventManager;
import openwrestling.manager.InjuryManager;
import openwrestling.manager.NewsManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.RelationshipManager;
import openwrestling.model.NewsItem;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.Injury;
import openwrestling.model.gameObjects.MoraleRelationship;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.financial.Transaction;
import openwrestling.model.segment.constants.EventFrequency;
import openwrestling.model.segment.constants.TransactionType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static openwrestling.model.constants.GameConstants.APPEARANCE_MORALE_BONUS;
import static openwrestling.model.factory.EventFactory.bookEventForCompletedAnnualEventTemplateAfterDate;
import static openwrestling.model.utility.ModelUtils.distinctByKey;
import static openwrestling.model.utility.PromotionUtils.*;


@Builder
public class NextDayController extends Logging {

    private EventManager eventManager;
    private DateManager dateManager;
    private RelationshipManager relationshipManager;
    private BankAccountManager bankAccountManager;
    private InjuryManager injuryManager;
    private ContractManager contractManager;
    private NewsManager newsManager;
    private PromotionManager promotionManager;

    private DailyEventBooker dailyEventBooker;
    private DailyContractUpdate dailyContractUpdate;
    private DailyTransactions dailyTransactions;
    private DailyRelationshipUpdate dailyRelationshipUpdate;
    private MonthlyReviewController monthlyReviewController;

    private List<Transaction> cachedTransactions;
    private Map<Long, Contract> cachedContractsMap;
    private List<NewsItem> cachedNewsItems;
    private Map<Worker, MoraleRelationship> cachedMoraleRelationshipMap;
    private List<Promotion> cachedPromotions;


    public void nextDay() {
        clearCache();
        long start = System.currentTimeMillis();
        logger.log(Level.DEBUG, "nextDay" + dateManager.todayString());

        dailyContractUpdate.renewExpiringContracts();
        logger.log(Level.DEBUG, String.format("updateExpiringContracts took %d ms", System.currentTimeMillis() - start));

        processEvents(dailyEventBooker.getEvents());
        logger.log(Level.DEBUG, String.format("processEvents took %d ms", System.currentTimeMillis() - start));

        cachedTransactions.addAll(dailyTransactions.getPayDayTransactions());
        logger.log(Level.DEBUG, String.format("cachedTransactions took %d ms", System.currentTimeMillis() - start));

        logger.log(Level.DEBUG, String.format("cachedNewContracts took %d ms", System.currentTimeMillis() - start));

        List<MoraleRelationship> updatedRelationshipsAfterDailyMoraleCheck = dailyRelationshipUpdate.getUpdatedRelationshipsForDailyMoraleCheck();
        cachedNewsItems.addAll(dailyRelationshipUpdate.getUpdatedMoraleRelationshipNewsItems(updatedRelationshipsAfterDailyMoraleCheck));
        dailyRelationshipUpdate.updateRelationshipMap(cachedMoraleRelationshipMap, updatedRelationshipsAfterDailyMoraleCheck);

        if (dateManager.isLastDayOfMonth()) {
            monthlyReviewController.createMonthlyReview(promotionManager.getPlayerPromotion(), dateManager.today());
        }

        processCache();

        logger.log(Level.DEBUG, String.format("nextDay took %d ms", System.currentTimeMillis() - start));
    }

    public void playerEvent(Event event) {
        clearCache();
        processEvents(List.of(event));
        processCache();
    }

    public void processEvents(List<Event> events) {
        List<Injury> injuriesExtractedFromSegments = new ArrayList<>();
        List<NewsItem> newsItemsExtractedFromSegments = new ArrayList<>();

        events.stream()
                .flatMap(event -> event.getSegments().stream())
                .forEach(segment -> {
                    if (CollectionUtils.isNotEmpty(segment.getSegmentNewsItems())) {
                        newsItemsExtractedFromSegments.addAll(segment.getSegmentNewsItems());
                    }

                    if (CollectionUtils.isNotEmpty(segment.getInjuries())) {
                        injuriesExtractedFromSegments.addAll(segment.getInjuries());
                    }

                    segment.getWorkers().forEach(worker -> {
                        if (!cachedMoraleRelationshipMap.containsKey(worker)) {
                            cachedMoraleRelationshipMap.put(worker, relationshipManager.getMoraleRelationship(worker, segment.getPromotion()));
                        }
                        cachedMoraleRelationshipMap.get(worker).modifyValue(APPEARANCE_MORALE_BONUS);
                    });

                    segment.getMoraleRelationshipMap().forEach((key, value) -> {
                        if (!cachedMoraleRelationshipMap.containsKey(key)) {
                            cachedMoraleRelationshipMap.put(key, value);
                        } else {
                            cachedMoraleRelationshipMap.get(key).modifyValue(value.getLevel());
                        }
                    });
                });

        events.forEach(event -> {
            Promotion promotion = event.getPromotion();
            int targetScore = getEventTargetScore(promotion);
            if (event.getRating() > targetScore) {
                gainPopularity(promotion);
                cachedPromotions.add(promotion);
            } else if (event.getRating() < targetScore) {
                losePopularity(promotion);
                cachedPromotions.add(promotion);
            }
        });

        List<Event> newAnnualEvents = events.stream()
                .map(Event::getEventTemplate)
                .filter(eventTemplate -> eventTemplate.getEventFrequency().equals(EventFrequency.ANNUAL))
                .map(eventTemplate -> bookEventForCompletedAnnualEventTemplateAfterDate(eventTemplate, dateManager.today()))
                .collect(Collectors.toList());

        cachedContractsMap = events.stream()
                .flatMap(event ->
                        event.getSegments().stream()
                                .flatMap(segment -> segment.getWorkers().stream())
                                .map(worker -> contractManager.getActiveContract(worker, event.getPromotion()))
                )
                .filter(distinctByKey(Contract::getContractID))
                .peek(contract -> contract.setLastShowDate(dateManager.today()))
                .collect(Collectors.toMap(Contract::getContractID, Function.identity()));


        cachedNewsItems.addAll(newsItemsExtractedFromSegments);
        addEventTransactionsToCache(events, new ArrayList<>(cachedContractsMap.values()));
        eventManager.createEvents(events);
        eventManager.createEvents(newAnnualEvents);
        injuryManager.createInjuries(injuriesExtractedFromSegments);
    }

    void addEventTransactionsToCache(List<Event> events, List<Contract> eventContracts) {
        cachedTransactions = events.stream()
                .map(event -> Transaction.builder()
                        .amount(event.getGate())
                        .type(TransactionType.GATE)
                        .date(dateManager.today())
                        .promotion(event.getPromotion())
                        .build()).collect(Collectors.toList());

        cachedTransactions.addAll(
                eventContracts.stream()
                        .filter(contract -> contract.getAppearanceCost() > 0)
                        .map(contract ->
                                Transaction.builder()
                                        .promotion(contract.getPromotion())
                                        .amount(contract.getAppearanceCost())
                                        .date(dateManager.today())
                                        .type(TransactionType.WORKER_APPEARANCE)
                                        .build()
                        )
                        .collect(Collectors.toList())
        );
    }

    private void processCache() {
        bankAccountManager.insertTransactions(cachedTransactions);
        contractManager.updateContracts(new ArrayList<>(cachedContractsMap.values()));
        newsManager.addNewsItems(cachedNewsItems);
        relationshipManager.createOrUpdateMoraleRelationships(new ArrayList<>(cachedMoraleRelationshipMap.values()));
        promotionManager.updatePromotions(cachedPromotions);
        clearCache();
    }

    private void clearCache() {
        cachedTransactions = new ArrayList<>();
        cachedContractsMap = new HashMap<>();
        cachedNewsItems = new ArrayList<>();
        cachedMoraleRelationshipMap = new HashMap<>();
        cachedPromotions = new ArrayList<>();
    }
}
