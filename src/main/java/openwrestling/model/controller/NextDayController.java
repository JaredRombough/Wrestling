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
import openwrestling.model.gameObjects.SegmentTemplate;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.financial.Transaction;
import openwrestling.model.manager.DateManager;
import openwrestling.manager.InjuryManager;
import openwrestling.manager.RelationshipManager;
import openwrestling.model.segmentEnum.AngleType;
import openwrestling.model.segmentEnum.SegmentType;
import openwrestling.model.segmentEnum.ShowType;
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
        logger.log(Level.DEBUG, "nextDay");
        List<Event> events = promotionManager.getPromotions().stream()
                .filter(promotion -> !promotionManager.getPlayerPromotion().equals(promotion))
                .map(this::eventOnDay)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        processEvents(events);

    }

    public void processEvents(List<Event> events) {
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


        updateBankAccounts(events);
        relationshipManager.createOrUpdateMoraleRelationships(new ArrayList<>(relationships.values()));
        eventManager.createEvents(events);
        eventManager.createEventTemplates(eventTemplates);
        eventManager.createEvents(newEvents);
        injuryManager.createInjuries(injuries);

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
