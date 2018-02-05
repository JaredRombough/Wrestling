package wrestling.model.manager;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.Promotion;
import wrestling.model.Television;

public class TelevisionManager {

    private final List<Television> televisionList;

    public TelevisionManager() {
        televisionList = new ArrayList();
    }

    public void addTelevision(List<Television> television) {
        for (Television tv : television) {
            televisionList.add(tv);
        }
    }

    public List<Television> tvOnDate(Promotion promotion, LocalDate date) {
        List<Television> tvToday = new ArrayList<>();
        for (Television tv : televisionList) {
            if (tv.getPromotion().equals(promotion)
                    && tv.getDay().equals(date.getDayOfWeek())) {
                tvToday.add(tv);
            }
        }

        return tvToday;
    }

    public List<Television> getTvDates(Promotion promotion, LocalDate localDate, int advanceMonths) {

        YearMonth yearMonth = YearMonth.from(localDate);
        yearMonth.plusMonths(advanceMonths);
        LocalDate currentDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);
        List<Television> tvDates = new ArrayList<>();

        while (currentDate.getMonth().equals(yearMonth.getMonth())) {

            tvDates.addAll(tvOnDate(promotion, currentDate));

            currentDate = LocalDate.from(currentDate).plusDays(1);
        }
        return tvDates;

    }
}
