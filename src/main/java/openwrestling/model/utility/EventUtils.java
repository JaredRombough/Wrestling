package openwrestling.model.utility;

import org.apache.commons.collections4.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventUtils {

    public static LocalDate dateForAnnualEvent(int month, DayOfWeek dayOfWeek, LocalDate date) {
        LocalDate modified;

        if (date.getMonth().getValue() == month) {
            List<LocalDate> potentialDatesInCurrentMonth = weekDaysLeftInMonth(month, dayOfWeek, date);
            if (CollectionUtils.isNotEmpty(potentialDatesInCurrentMonth)) {
                Collections.shuffle(potentialDatesInCurrentMonth);
                return potentialDatesInCurrentMonth.stream().findFirst().orElseThrow();
            }
        }

        if (date.getMonth().getValue() != month) {
            modified = date.withMonth(month);
            if (modified.isBefore(date)) {
                modified = modified.plusYears(1);
            }
        } else {
            modified = date.plusYears(1);
        }
        modified = modified.withDayOfMonth(1);

        List<LocalDate> potentialDates = weekDaysLeftInMonth(month, dayOfWeek, modified);
        Collections.shuffle(potentialDates);
        return potentialDates.stream().findFirst().orElseThrow();
    }

    public static List<LocalDate> weekDaysLeftInMonth(int month, DayOfWeek dayOfWeek, LocalDate date) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate thisDate = date;
        while (month == thisDate.with(TemporalAdjusters.next(dayOfWeek)).getMonth().getValue()) {
            dates.add(thisDate.with(TemporalAdjusters.next(dayOfWeek)));
            thisDate = thisDate.with(TemporalAdjusters.next(dayOfWeek));
        }
        return dates;
    }

}
