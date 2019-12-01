package openwrestling.model.utility;

import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.segmentEnum.EventFrequency;
import org.apache.commons.lang3.RandomUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import static java.time.temporal.TemporalAdjusters.next;

public class EventUtils {

    public static void initializeEventTemplateDates(EventTemplate eventTemplate, LocalDate earliestDate) {
        LocalDate date;
        if (eventTemplate.getEventFrequency().equals(EventFrequency.ANNUAL)) {
            while (earliestDate.getMonth().getValue() != eventTemplate.getMonth()) {
                earliestDate = earliestDate.plusMonths(1);
            }
            date = getAnnualEventDateInMonth(earliestDate, eventTemplate.getDayOfWeek());

        } else {
            if (earliestDate.getDayOfWeek().equals(eventTemplate.getDayOfWeek())) {
                date = earliestDate;
            } else {
                date = earliestDate.with(next(eventTemplate.getDayOfWeek()));
            }

        }
        eventTemplate.setNextDate(date);
        eventTemplate.setBookedUntil(date);

    }

//    public static LocalDate getInitialEventTemplateNextDate(EventTemplate eventTemplate, LocalDate earliestDate) {
//        if (eventTemplate.getEventFrequency().equals(EventFrequency.ANNUAL)) {
//            while (earliestDate.getMonth().getValue() != eventTemplate.getMonth()) {
//                earliestDate = earliestDate.plusMonths(1);
//            }
//            return getAnnualEventDateInMonth(earliestDate, eventTemplate.getDayOfWeek());
//
//        }
//        if (earliestDate.getDayOfWeek().equals(eventTemplate.getDayOfWeek())) {
//            return earliestDate;
//        }
//        return earliestDate.with(next(eventTemplate.getDayOfWeek()));
//    }


    public static LocalDate getAnnualEventDateInMonth(LocalDate dateInMonth, DayOfWeek dayOfWeek) {
        return dateInMonth.with(TemporalAdjusters.dayOfWeekInMonth(
                RandomUtils.nextInt(1, 4),
                dayOfWeek));
    }


//    public static LocalDate getNewBookedUntilDateAfterEvent(EventTemplate eventTemplate) {
//        if (eventTemplate.getEventFrequency().equals(EventFrequency.ANNUAL)) {
//            return EventUtils.getAnnualEventDateInMonth(eventTemplate.getBookedUntil().plusMonths(12), eventTemplate.getDayOfWeek());
//        }
//        return eventTemplate.getBookedUntil().plusDays(7);
//    }

    public static void updateDatesAfterEvent(EventTemplate eventTemplate) {
        if (eventTemplate.getEventFrequency().equals(EventFrequency.ANNUAL)) {
            LocalDate date = EventUtils.getAnnualEventDateInMonth(eventTemplate.getBookedUntil().plusMonths(12), eventTemplate.getDayOfWeek());
            eventTemplate.setNextDate(date);
        } else {
            eventTemplate.setNextDate(eventTemplate.getNextDate().plusDays(7));
            eventTemplate.setBookedUntil(eventTemplate.getBookedUntil().plusDays(7));
        }
    }

}
