package wrestling.model.controller;

import java.time.LocalDate;
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

    public List<Television> tvOnDay(Promotion promotion, LocalDate date) {
        List<Television> tvToday = new ArrayList<>();
        for (Television tv : televisionList) {
            if (tv.getPromotion().equals(promotion)
                    && tv.getDay().equals(date.getDayOfWeek())) {
                tvToday.add(tv);
            }
        }

        return tvToday;
    }

}
