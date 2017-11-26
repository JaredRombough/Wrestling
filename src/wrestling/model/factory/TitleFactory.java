package wrestling.model.factory;

import java.util.List;
import wrestling.model.Promotion;
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.model.manager.TitleManager;

/**
 * called whenever a new title is to be created
 *
 */
public class TitleFactory {

    private final TitleManager titleManager;

    public TitleFactory(TitleManager titleManager) {
        this.titleManager = titleManager;
    }

    //create a title with predetermined attributes
    public void createTitle(Promotion promotion, Worker worker, String name) {

        Title title = new Title(promotion, 1, name);

        titleManager.addTitle(title);
        titleManager.awardTitle(title, worker);
    }

    //create a title with predetermined attributes
    public void createTitle(Promotion promotion, List<Worker> workers, String name) {
        Title title = new Title(promotion, workers.size(), name);
        titleManager.addTitle(title);
        titleManager.awardTitle(title, workers);
    }

}
