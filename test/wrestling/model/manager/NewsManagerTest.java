package wrestling.model.manager;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import wrestling.model.NewsItem;
import wrestling.model.factory.PersonFactory;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.WorkerView;

public class NewsManagerTest {

    private NewsManager newsManager;
    private WorkerView worker;
    private PromotionView promotion;

    @Before
    public void setUp() {
        newsManager = new NewsManager();
        worker = PersonFactory.randomWorker();
        promotion = new PromotionView();
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
