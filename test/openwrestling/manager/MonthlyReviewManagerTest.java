package openwrestling.manager;

import openwrestling.database.Database;
import openwrestling.model.gameObjects.MonthlyReview;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static openwrestling.TestUtils.TEST_DB_PATH;
import static org.assertj.core.api.Assertions.assertThat;

public class MonthlyReviewManagerTest {

    private MonthlyReviewManager monthlyReviewManager;

    @Before
    public void setUp() {
        monthlyReviewManager = new MonthlyReviewManager(new Database(TEST_DB_PATH));
    }

    @Test
    public void getRecentReviews_returnsSortedReviews() {
        LocalDate date1 = LocalDate.now();
        LocalDate date2 = LocalDate.now().minusWeeks(1);
        LocalDate date3 = LocalDate.now().minusMonths(1);
        LocalDate date4 = LocalDate.now().minusMonths(2);
        List<MonthlyReview> reviews = List.of(date1, date2, date3, date4).stream()
                .map(date -> MonthlyReview.builder()
                        .date(date)
                        .build())
                .collect(Collectors.toList());
        monthlyReviewManager.createMonthlyReviews(reviews);

        List<MonthlyReview> recentReviews = monthlyReviewManager.getRecentReviews(date1);
        assertThat(recentReviews).extracting(MonthlyReview::getDate)
                .containsExactly(date2, date3, date4);
    }
}