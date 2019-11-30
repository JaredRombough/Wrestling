package openwrestling.model.utility;

import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.segmentEnum.EventFrequency;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.next;

public class EventUtils {

    public static List<Event> bookEventsForNewEventTemplate(EventTemplate eventTemplate) {
        List<Event> newEvents = new ArrayList<>();
        if (eventTemplate.getEventFrequency().equals(EventFrequency.ANNUAL)) {
            Event event = new Event();
            event.setDate(eventTemplate.getNextDate());
            event.setPromotion(eventTemplate.getPromotion());
            event.setName(eventTemplate.getName());
            event.setEventTemplate(eventTemplate);
            event.setDefaultDuration(eventTemplate.getDefaultDuration());
            newEvents.add(event);
        } else if (eventTemplate.getEventFrequency().equals(EventFrequency.WEEKLY)) {
            LocalDate weeklyDate = LocalDate.of(eventTemplate.getNextDate().getYear(),
                    eventTemplate.getNextDate().getMonthValue(),
                    eventTemplate.getNextDate().getDayOfMonth()
            );
            for (int i = 0; i < eventTemplate.getEventsLeft(); i++) {

                Event event = new Event();
                event.setDate(weeklyDate);
                event.setPromotion(eventTemplate.getPromotion());
                event.setName(eventTemplate.getName());
                event.setEventTemplate(eventTemplate);
                event.setDefaultDuration(eventTemplate.getDefaultDuration());
                newEvents.add(event);
                eventTemplate.setBookedUntil(weeklyDate);
                weeklyDate = weeklyDate.with(next(eventTemplate.getDayOfWeek()));
            }
        }
        return newEvents;
    }

    public static LocalDate getInitialEventTemplateDate(EventTemplate eventTemplate, LocalDate startDate) {
        if (startDate.getDayOfWeek().equals(eventTemplate.getDayOfWeek())) {
            return startDate;
        }
        return startDate.with(next(eventTemplate.getDayOfWeek()));
    }

}
