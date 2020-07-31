package openwrestling.manager;


import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.MonthlyReview;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MonthlyReviewManager extends GameObjectManager implements Serializable {

    @Getter
    private List<MonthlyReview> monthlyReviews = new ArrayList<>();

    public MonthlyReviewManager(Database database) {
        super(database);
    }

    @Override
    public void selectData() {
        monthlyReviews = getDatabase().selectAll(MonthlyReview.class);
    }

    public MonthlyReview createMonthlyReview(MonthlyReview monthlyReview) {
        return createMonthlyReviews(List.of(monthlyReview)).get(0);
    }

    public List<MonthlyReview> createMonthlyReviews(List<MonthlyReview> monthlyReviews) {
        List<MonthlyReview> saved = getDatabase().insertList(monthlyReviews);
        this.monthlyReviews.addAll(saved);
        return saved;
    }

    public List<MonthlyReview> getRecentReviews(LocalDate date) {
        LocalDate boundary = date.minusMonths(3).withDayOfMonth(1);
        return monthlyReviews.stream()
                .filter(monthlyReview -> monthlyReview.getDate().isAfter(boundary) &&
                        monthlyReview.getDate().isBefore(date))
                .sorted(Comparator.comparing(MonthlyReview::getDate).reversed())
                .collect(Collectors.toList());
    }


}
