package openwrestling.manager;


import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.MonthlyReview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
        MonthlyReview saved = getDatabase().insertList(List.of(monthlyReview)).get(0);
        this.monthlyReviews.add(saved);
        return saved;
    }


}
