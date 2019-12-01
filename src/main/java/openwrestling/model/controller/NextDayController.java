package openwrestling.model.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import openwrestling.Logging;
import openwrestling.manager.EventManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.manager.DateManager;
import openwrestling.model.utility.EventUtils;
import org.apache.logging.log4j.Level;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static openwrestling.model.factory.EventFactory.bookEventForTemplate;


@Builder
public class NextDayController extends Logging {

    private PromotionManager promotionManager;
    private EventManager eventManager;
    private DateManager dateManager;
    private PromotionController promotionController;
    private WorkerManager workerManager;

    public void nextDay() {
        logger.log(Level.DEBUG, "nextDay");
        List<Event> events = promotionManager.getPromotions().stream()
                .filter(promotion -> !promotionManager.getPlayerPromotion().equals(promotion))
                .map(this::eventOnDay)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<EventTemplate> eventTemplates = events.stream()
                .map(Event::getEventTemplate)
                .peek(EventUtils::updateDatesAfterEvent)
                .collect(Collectors.toList());

        List<Event> newEvents = eventTemplates.stream()
                .map(eventTemplate -> bookEventForTemplate(eventTemplate, eventTemplate.getBookedUntil()))
                .collect(Collectors.toList());


        eventManager.createEvents(events);
        eventManager.createEventTemplates(eventTemplates);
        eventManager.createEvents(newEvents);

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
