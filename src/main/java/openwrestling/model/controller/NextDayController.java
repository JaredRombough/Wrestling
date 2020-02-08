package openwrestling.model.controller;

import lombok.Builder;
import openwrestling.Logging;
import openwrestling.manager.BankAccountManager;
import openwrestling.manager.EventManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.Injury;
import openwrestling.model.gameObjects.MoraleRelationship;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.financial.Transaction;
import openwrestling.manager.DateManager;
import openwrestling.manager.InjuryManager;
import openwrestling.manager.RelationshipManager;
import openwrestling.model.segmentEnum.TransactionType;
import openwrestling.model.utility.EventUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static openwrestling.model.constants.GameConstants.APPEARANCE_MORALE_BONUS;
import static openwrestling.model.factory.EventFactory.bookEventForTemplate;


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

    public void nextDay() {
        long start = System.currentTimeMillis();
        logger.log(Level.DEBUG, "nextDay");
        List<Event> events = promotionManager.getPromotions().stream()
                .filter(promotion -> !promotionManager.getPlayerPromotion().equals(promotion))
                .map(this::eventOnDay)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        logger.log(Level.DEBUG, String.format("nextDay promo loop took %d ms",
                System.currentTimeMillis() - start));
        long start2 = System.currentTimeMillis();
        if(CollectionUtils.isNotEmpty(events)) {
            processEvents(events);
        }
        logger.log(Level.DEBUG, String.format("nextDay processEvents took %d ms",
                System.currentTimeMillis() - start2));

    }

    public void processEvents(List<Event> events) {
        long start = System.currentTimeMillis();
        logger.log(Level.DEBUG, "processEvents");
        Map<Worker, MoraleRelationship> relationships = new HashMap<>();
        List<Injury> injuries = new ArrayList<>();

        events.stream()
                .flatMap(event -> event.getSegments().stream())
                .forEach(segment -> {
                    if (CollectionUtils.isNotEmpty(segment.getInjuries())) {
                        injuries.addAll(segment.getInjuries());
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


        List<EventTemplate> eventTemplates = events.stream()
                .map(Event::getEventTemplate)
                .peek(EventUtils::updateDatesAfterEvent)
                .collect(Collectors.toList());

        List<Event> newEvents = eventTemplates.stream()
                .map(eventTemplate -> bookEventForTemplate(eventTemplate, eventTemplate.getBookedUntil()))
                .collect(Collectors.toList());


        logger.log(Level.DEBUG, String.format("processEvents a took %d ms",
                System.currentTimeMillis() - start));
        long start2 = System.currentTimeMillis();

        updateBankAccounts(events);
        logger.log(Level.DEBUG, String.format("processEvents updateBankAccounts took %d ms",
                System.currentTimeMillis() - start2));
        long start3 = System.currentTimeMillis();
        relationshipManager.createOrUpdateMoraleRelationships(new ArrayList<>(relationships.values()));
        logger.log(Level.DEBUG, String.format("processEvents createOrUpdateMoraleRelationships took %d ms",
                System.currentTimeMillis() - start3));
        long start4 = System.currentTimeMillis();
        eventManager.createEvents(events);
        logger.log(Level.DEBUG, String.format("processEvents createEvents took %d ms",
                System.currentTimeMillis() - start4));
        long start5 = System.currentTimeMillis();
        eventManager.updateEventTemplates(eventTemplates);
        logger.log(Level.DEBUG, String.format("processEvents createEventTemplates took %d ms",
                System.currentTimeMillis() - start5));
        long start6 = System.currentTimeMillis();
        eventManager.createEvents(newEvents);
        logger.log(Level.DEBUG, String.format("processEvents createEvents took %d ms",
                System.currentTimeMillis() - start6));
        long start7 = System.currentTimeMillis();
        injuryManager.createInjuries(injuries);
        logger.log(Level.DEBUG, String.format("processEvents createInjuries took %d ms",
                System.currentTimeMillis() - start7));
    }

    void updateBankAccounts(List<Event> events) {
        List<Transaction> transactions = events.stream()
                .map(event -> Transaction.builder()
                        .amount(event.getGate())
                        .type(TransactionType.GATE)
                        .promotion(event.getPromotion())
                        .build()).collect(Collectors.toList());

        bankAccountManager.insertTransactions(transactions);
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
}
