package wrestling.model.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.NewsItem;

public class NewsManager implements Serializable {

    private final List<NewsItem> newsItems = new ArrayList<>();

    public void addNews(NewsItem newsItem) {
        newsItems.add(newsItem);
    }

    /**
     * @return the newsItems
     */
    public List<NewsItem> getNewsItems() {
        return newsItems;
    }
}
