package openwrestling.manager;

import openwrestling.TestUtils;
import openwrestling.database.Database;
import openwrestling.manager.BankAccountManager;
import openwrestling.manager.ContractManager;
import openwrestling.manager.GameSettingManager;
import openwrestling.manager.NewsManager;
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

import static openwrestling.TestUtils.TEST_DB_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class NewsManagerTest {

    private NewsManager newsManager;
    private Worker worker;
    private Promotion promotion;
    private Worker worker2;
    private Database database;

    @Before
    public void setUp() {
        database = new Database(TEST_DB_PATH);

        newsManager = new NewsManager(database);

        PromotionManager promotionManager = new PromotionManager(database, mock(BankAccountManager.class), mock(GameSettingManager.class));
        WorkerManager workerManager = new WorkerManager(database, mock(ContractManager.class));
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
