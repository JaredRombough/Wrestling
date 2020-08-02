package openwrestling.model.utility;

import junit.framework.TestCase;
import openwrestling.database.Database;
import openwrestling.manager.MonthlyReviewManager;
import openwrestling.model.gameObjects.MonthlyReview;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static openwrestling.TestUtils.TEST_DB_PATH;
import static org.assertj.core.api.Assertions.assertThat;

public class MonthlyReviewUtilsTest extends TestCase {

    private MonthlyReviewManager monthlyReviewManager;

    @Before
    public void setUp() {
        monthlyReviewManager = new MonthlyReviewManager(new Database(TEST_DB_PATH));
    }

    @Test
    public void testPopularityString_oneMonthDecline() {
        MonthlyReview monthlyReview1 = createReview(LocalDate.now().minusDays(1), 4, 10);
        MonthlyReview monthlyReview2 = createReview(LocalDate.now().minusWeeks(1), 4, 11);
        MonthlyReview monthlyReview3 = createReview(LocalDate.now().minusMonths(1), 4, 10);
        MonthlyReview monthlyReview4 = createReview(LocalDate.now().minusMonths(2), 4, 10);

        monthlyReviewManager.createMonthlyReviews(
                List.of(monthlyReview1, monthlyReview2, monthlyReview3, monthlyReview4)
        );

        List<MonthlyReview> recentReviews = monthlyReviewManager.getRecentReviews(LocalDate.now());

        String ownerName = RandomStringUtils.random(10);

        String popularityString = MonthlyReviewUtils.popularityString(recentReviews, ownerName);

        assertThat(popularityString).contains(ownerName);
        assertThat(popularityString).contains("1 month");
        assertThat(popularityString).contains("going down");
    }

    @Test
    public void testPopularityString_twoMonthDecline() {
        MonthlyReview monthlyReview1 = createReview(LocalDate.now().minusDays(1), 4, 10);
        MonthlyReview monthlyReview2 = createReview(LocalDate.now().minusWeeks(1), 4, 11);
        MonthlyReview monthlyReview3 = createReview(LocalDate.now().minusMonths(1), 4, 12);
        MonthlyReview monthlyReview4 = createReview(LocalDate.now().minusMonths(2), 4, 10);

        monthlyReviewManager.createMonthlyReviews(
                List.of(monthlyReview1, monthlyReview2, monthlyReview3, monthlyReview4)
        );

        List<MonthlyReview> recentReviews = monthlyReviewManager.getRecentReviews(LocalDate.now());

        String ownerName = RandomStringUtils.random(10);

        String popularityString = MonthlyReviewUtils.popularityString(recentReviews, ownerName);

        assertThat(popularityString).contains(ownerName);
        assertThat(popularityString).contains("2 month");
        assertThat(popularityString).contains("going down");
    }

    @Test
    public void testPopularityString_increase() {
        MonthlyReview monthlyReview1 = createReview(LocalDate.now().minusDays(1), 5, 10);
        MonthlyReview monthlyReview2 = createReview(LocalDate.now().minusWeeks(1), 4, 11);
        MonthlyReview monthlyReview3 = createReview(LocalDate.now().minusMonths(1), 4, 12);
        MonthlyReview monthlyReview4 = createReview(LocalDate.now().minusMonths(2), 4, 10);

        monthlyReviewManager.createMonthlyReviews(
                List.of(monthlyReview1, monthlyReview2, monthlyReview3, monthlyReview4)
        );

        List<MonthlyReview> recentReviews = monthlyReviewManager.getRecentReviews(LocalDate.now());

        String ownerName = RandomStringUtils.random(10);

        String popularityString = MonthlyReviewUtils.popularityString(recentReviews, ownerName);

        assertThat(popularityString).contains(ownerName);
        assertThat(popularityString).contains("increased");
    }

    @Test
    public void testPopularityString_stable() {
        MonthlyReview monthlyReview1 = createReview(LocalDate.now().minusDays(1), 5, 10);
        MonthlyReview monthlyReview2 = createReview(LocalDate.now().minusWeeks(1), 5, 10);
        MonthlyReview monthlyReview3 = createReview(LocalDate.now().minusMonths(1), 4, 12);
        MonthlyReview monthlyReview4 = createReview(LocalDate.now().minusMonths(2), 4, 10);

        monthlyReviewManager.createMonthlyReviews(
                List.of(monthlyReview1, monthlyReview2, monthlyReview3, monthlyReview4)
        );

        List<MonthlyReview> recentReviews = monthlyReviewManager.getRecentReviews(LocalDate.now());

        String ownerName = RandomStringUtils.random(10);

        String popularityString = MonthlyReviewUtils.popularityString(recentReviews, ownerName);

        assertThat(popularityString).contains(ownerName);
        assertThat(popularityString).contains("stable");
    }

    @Test
    public void testFundsString_oneMonthDecline() {
        MonthlyReview monthlyReview1 = createReview(LocalDate.now().minusDays(1), 80);
        MonthlyReview monthlyReview2 = createReview(LocalDate.now().minusWeeks(1), 100);
        MonthlyReview monthlyReview3 = createReview(LocalDate.now().minusMonths(1), 100);
        MonthlyReview monthlyReview4 = createReview(LocalDate.now().minusMonths(2), 90);

        monthlyReviewManager.createMonthlyReviews(
                List.of(monthlyReview1, monthlyReview2, monthlyReview3, monthlyReview4)
        );

        List<MonthlyReview> recentReviews = monthlyReviewManager.getRecentReviews(LocalDate.now());

        String ownerName = RandomStringUtils.random(10);

        String fundsString = MonthlyReviewUtils.fundsString(recentReviews, ownerName);

        assertThat(fundsString).contains(ownerName);
        assertThat(fundsString).contains("1 month");
        assertThat(fundsString).contains("going down");
    }

    @Test
    public void testFundsString_twoMonthDecline() {
        MonthlyReview monthlyReview1 = createReview(LocalDate.now().minusDays(1), 80);
        MonthlyReview monthlyReview2 = createReview(LocalDate.now().minusWeeks(1), 90);
        MonthlyReview monthlyReview3 = createReview(LocalDate.now().minusMonths(1), 100);
        MonthlyReview monthlyReview4 = createReview(LocalDate.now().minusMonths(2), 90);

        monthlyReviewManager.createMonthlyReviews(
                List.of(monthlyReview1, monthlyReview2, monthlyReview3, monthlyReview4)
        );

        List<MonthlyReview> recentReviews = monthlyReviewManager.getRecentReviews(LocalDate.now());

        String ownerName = RandomStringUtils.random(10);

        String fundsString = MonthlyReviewUtils.fundsString(recentReviews, ownerName);

        assertThat(fundsString).contains(ownerName);
        assertThat(fundsString).contains("2 month");
        assertThat(fundsString).contains("going down");
    }

    @Test
    public void testFundsString_increase() {
        MonthlyReview monthlyReview1 = createReview(LocalDate.now().minusDays(1), 100);
        MonthlyReview monthlyReview2 = createReview(LocalDate.now().minusWeeks(1), 90);
        MonthlyReview monthlyReview3 = createReview(LocalDate.now().minusMonths(1), 100);
        MonthlyReview monthlyReview4 = createReview(LocalDate.now().minusMonths(2), 90);

        monthlyReviewManager.createMonthlyReviews(
                List.of(monthlyReview1, monthlyReview2, monthlyReview3, monthlyReview4)
        );

        List<MonthlyReview> recentReviews = monthlyReviewManager.getRecentReviews(LocalDate.now());

        String ownerName = RandomStringUtils.random(10);

        String fundsString = MonthlyReviewUtils.fundsString(recentReviews, ownerName);

        assertThat(fundsString).contains(ownerName);
        assertThat(fundsString).contains("increased");
    }

    @Test
    public void testFundsString_stable() {
        MonthlyReview monthlyReview1 = createReview(LocalDate.now().minusDays(1), 100);
        MonthlyReview monthlyReview2 = createReview(LocalDate.now().minusWeeks(1), 100);
        MonthlyReview monthlyReview3 = createReview(LocalDate.now().minusMonths(1), 100);
        MonthlyReview monthlyReview4 = createReview(LocalDate.now().minusMonths(2), 90);

        monthlyReviewManager.createMonthlyReviews(
                List.of(monthlyReview1, monthlyReview2, monthlyReview3, monthlyReview4)
        );

        List<MonthlyReview> recentReviews = monthlyReviewManager.getRecentReviews(LocalDate.now());

        String ownerName = RandomStringUtils.random(10);

        String fundsString = MonthlyReviewUtils.fundsString(recentReviews, ownerName);

        assertThat(fundsString).contains(ownerName);
        assertThat(fundsString).contains("stable");
    }

    private MonthlyReview createReview(LocalDate date, long funds) {
        return MonthlyReview.builder()
                .funds(funds)
                .date(date)
                .build();
    }

    private MonthlyReview createReview(LocalDate date, int level, int popularity) {
        return MonthlyReview.builder()
                .level(level)
                .popularity(popularity)
                .date(date)
                .build();
    }

}