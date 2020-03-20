package openwrestling.model.controller;

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
import openwrestling.manager.WorkerManager;
import openwrestling.model.NewsItem;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.Injury;
import openwrestling.model.gameObjects.MoraleRelationship;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.financial.Transaction;
import openwrestling.model.segmentEnum.EventFrequency;
import openwrestling.model.segmentEnum.TransactionType;
import openwrestling.model.utility.ContractUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static openwrestling.model.constants.GameConstants.APPEARANCE_MORALE_BONUS;
import static openwrestling.model.constants.GameConstants.MORALE_PENALTY_DAYS_BETWEEN;
import static openwrestling.model.factory.EventFactory.bookEventForCompletedAnnualEventTemplateAfterDate;


@Builder
public class NextDayController extends Logging {

    private PromotionManager promotionManager;
    private EventManager eventManager;
    private DateManager dateManager;
    private PromotionController promotionController;
    private WorkerManager workerManager;
    private RelationshipManager relationshipManager;
    private BankAccountManager bankAccountManager;
    private InjuryManager injuryManager;
    private ContractManager contractManager;
    private NewsManager newsManager;

    public void nextDay() {
        long start = System.currentTimeMillis();
        logger.log(Level.DEBUG, "nextDay" + dateManager.todayString());
        List<Event> events = promotionManager.getPromotions().stream()
                .filter(promotion -> !promotionManager.getPlayerPromotion().equals(promotion))
                .map(this::eventOnDay)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        processEvents(events);

        logger.log(Level.DEBUG, String.format("nextDay took %d ms",
                System.currentTimeMillis() - start));

    }

    public void processEvents(List<Event> events) {
        logger.log(Level.DEBUG, "processEvents");
        Map<Worker, MoraleRelationship> relationships = new HashMap<>();
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
                        if (!relationships.containsKey(worker)) {
                            relationships.put(worker, relationshipManager.getMoraleRelationship(worker, segment.getPromotion()));
                        }
                        relationships.get(worker).modifyValue(APPEARANCE_MORALE_BONUS);
                    });

                    segment.getMoraleRelationshipMap().forEach((key, value) -> {
                        if (!relationships.containsKey(key)) {
                            relationships.put(key, value);
                        } else {
                            relationships.get(key).modifyValue(value.getLevel());
                        }
                    });
                });

        List<Event> newAnnualEvents = events.stream()
                .map(Event::getEventTemplate)
                .filter(eventTemplate -> eventTemplate.getEventFrequency().equals(EventFrequency.ANNUAL))
                .map(eventTemplate -> bookEventForCompletedAnnualEventTemplateAfterDate(eventTemplate, dateManager.today()))
                .collect(Collectors.toList());

        List<Contract> contracts = events.stream()
                .flatMap(event ->
                        event.getSegments().stream()
                                .flatMap(segment -> segment.getWorkers().stream())
                                .map(worker -> contractManager.getContract(worker, event.getPromotion()))
                )
                .peek(contract -> contract.setLastShowDate(dateManager.today()))
                .collect(Collectors.toList());


        contractManager.updateContracts(contracts);
        List<NewsItem> moraleCheckNewsItems = updateRelationshipsForMoraleCheck();
        newsManager.addNewsItems(ListUtils.union(moraleCheckNewsItems, newsItemsExtractedFromSegments));
        updateBankAccounts(events, contracts);
        relationshipManager.createOrUpdateMoraleRelationships(new ArrayList<>(relationships.values()));
        eventManager.createEvents(events);
        eventManager.createEvents(newAnnualEvents);
        injuryManager.createInjuries(injuriesExtractedFromSegments);

    }

    void updateBankAccounts(List<Event> events, List<Contract> eventContracts) {
        List<Transaction> transactions = events.stream()
                .map(event -> Transaction.builder()
                        .amount(event.getGate())
                        .type(TransactionType.GATE)
                        .date(dateManager.today())
                        .promotion(event.getPromotion())
                        .build()).collect(Collectors.toList());

        transactions.addAll(
                eventContracts.stream()
                        .filter(contract -> contract.getAppearanceCost() > 0)
                        .map(contract ->
                                Transaction.builder()
                                        .promotion(contract.getPromotion())
                                        .amount(contract.getAppearanceCost())
                                        .date(dateManager.today())
                                        .type(TransactionType.WORKER)
                                        .build()
                        )
                        .collect(Collectors.toList())
        );

        if (dateManager.isPayDay()) {
            transactions.addAll(getPayDayTransactions());
        }

        bankAccountManager.insertTransactions(transactions);
    }

    private List<Transaction> getPayDayTransactions() {
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

    private Event eventOnDay(Promotion promotion) {
        Event eventToday = eventManager.getEventOnDate(promotion, dateManager.today());
        if (eventToday != null) {
            List<Worker> roster = workerManager.selectRoster(promotion);
            if (roster.size() >= 2) {
                eventToday = promotionController.bookEvent(eventToday, promotion);
            }
        }
        return eventToday;
    }

    private List<NewsItem> updateRelationshipsForMoraleCheck() {
        List<MoraleRelationship> moraleRelationships = new ArrayList<>();
        List<NewsItem> moraleNewsItems = new ArrayList<>();
        contractManager.getContracts().stream()
                .filter(contract -> ContractUtils.isMoraleCheckDay(contract, dateManager.today()))
                .forEach(contract -> {
                    long daysBetween = DAYS.between(contract.getLastShowDate(), dateManager.today());
                    int penalty = Math.round(daysBetween / MORALE_PENALTY_DAYS_BETWEEN);
                    if (penalty > 0) {
                        MoraleRelationship moraleRelationship = relationshipManager.getMoraleRelationship(contract.getWorker(), contract.getPromotion());
                        moraleRelationship.setLevel(moraleRelationship.getLevel() - penalty);
                        moraleRelationships.add(moraleRelationship);
                        moraleNewsItems.add(newsManager.getMoraleNewsItem(contract, daysBetween, penalty, dateManager.today()));
                    }
                });
        relationshipManager.createOrUpdateMoraleRelationships(moraleRelationships);
        return moraleNewsItems;
    }
}
