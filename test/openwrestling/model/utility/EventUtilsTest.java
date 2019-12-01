package openwrestling.model.utility;

import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.segmentEnum.EventFrequency;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static java.time.temporal.TemporalAdjusters.next;
import static org.assertj.core.api.Assertions.assertThat;


public class EventUtilsTest {

    @Test
    public void initializeEventTemplateDates() {
        EventTemplate eventTemplate = new EventTemplate();
        eventTemplate.setDayOfWeek(DayOfWeek.FRIDAY);
        eventTemplate.setMonth(8);
        LocalDate earliestDate = LocalDate.now();

        EventUtils.initializeEventTemplateDates(eventTemplate, earliestDate);

        assertThat(eventTemplate.getNextDate().getDayOfWeek()).isEqualTo(DayOfWeek.FRIDAY);
        assertThat(eventTemplate.getBookedUntil().getDayOfWeek()).isEqualTo(DayOfWeek.FRIDAY);
        assertThat(eventTemplate.getBookedUntil()).isEqualTo(eventTemplate.getNextDate());
    }

    @Test
    public void updateDatesAfterEvent_weekly() {
        EventTemplate eventTemplate = new EventTemplate();
        eventTemplate.setEventFrequency(EventFrequency.WEEKLY);
        LocalDate nextDate = LocalDate.now().with(next(DayOfWeek.MONDAY));
        LocalDate bookedUntil = nextDate.plusWeeks(10);
        eventTemplate.setNextDate(nextDate);
        eventTemplate.setBookedUntil(bookedUntil);
        EventUtils.updateDatesAfterEvent(eventTemplate);
        assertThat(eventTemplate.getNextDate()).isEqualTo(nextDate.plusWeeks(1));
        assertThat(eventTemplate.getBookedUntil()).isEqualTo(bookedUntil.plusWeeks(1));
    }
}