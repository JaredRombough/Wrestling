package openwrestling.model.manager;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;

import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.manager.NewsManager;
import org.junit.Before;
import org.junit.Test;
import openwrestling.model.NewsItem;
import openwrestling.model.factory.PersonFactory;

public class NewsManagerTest {

    private NewsManager newsManager;
    private Worker worker;
    private Promotion promotion;

    @Before
    public void setUp() {
        newsManager = new NewsManager();
        worker = PersonFactory.randomWorker();
        promotion = new Promotion();
    }

    @Test
    public void getNews_returnsNewsItem() {
        for (int i = 0; i < 10; i++) {
            newsManager.addJobComplaintNewsItem(worker, Arrays.asList(), promotion, LocalDate.now());
            List<NewsItem> newsItems = newsManager.getNews(worker, LocalDate.now().minusDays(1), LocalDate.now());
            assertEquals(newsItems.size(), i + 1);
        }
    }

}
