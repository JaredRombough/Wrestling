package openwrestling.model.manager;

import openwrestling.TestUtils;
import openwrestling.database.Database;
import openwrestling.manager.BankAccountManager;
import openwrestling.manager.ContractManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.NewsItem;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class NewsManagerTest {

    private NewsManager newsManager;
    private Worker worker;
    private Promotion promotion;
    private Worker worker2;

    @Before
    public void setUp() {
        Database.createNewDatabase("testdb");

        newsManager = new NewsManager();

        PromotionManager promotionManager = new PromotionManager(mock(BankAccountManager.class));
        WorkerManager workerManager = new WorkerManager(mock(ContractManager.class));
        worker = workerManager.createWorker(PersonFactory.randomWorker());
        worker2 = workerManager.createWorker(PersonFactory.randomWorker());
        promotion = promotionManager.createPromotions(List.of(TestUtils.randomPromotion())).get(0);
    }

    @Test
    public void addJobComplaintNewsItem() {
        newsManager.addJobComplaintNewsItem(worker, Arrays.asList(worker2), promotion, LocalDate.now());
        List<NewsItem> newsItems = newsManager.getNews(worker, LocalDate.now().minusDays(1), LocalDate.now());
        assertThat(newsItems).hasOnlyOneElementSatisfying(newsItem -> {
            assertThat(newsItem.getNewsItemID()).isEqualTo(1L);
            assertThat(newsItem.getDate()).isEqualTo(LocalDate.now());
            assertThat(newsItem.getWorkers()).containsOnly(worker, worker2);
            assertThat(newsItem.getPromotions()).containsOnly(promotion);
        });
    }
}
