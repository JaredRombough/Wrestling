package wrestling.model.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.interfaces.iNewsItem;

public class NewsManager implements Serializable {

    private final List<iNewsItem> newsItems = new ArrayList<>();

    public void addNews(iNewsItem newsItem) {
        newsItems.add(newsItem);
    }

    /**
     * @return the newsItems
     */
    public List<iNewsItem> getNewsItems() {
        return newsItems;
    }
}
