package wrestling.model.factory;

import java.util.Arrays;
import java.util.List;
import wrestling.model.Promotion;
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.model.manager.TitleManager;
import wrestling.model.modelView.TitleView;

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
        intitializeTitle(new Title(promotion, 1, name), Arrays.asList(worker));
    }

    //create a title with predetermined attributes
    public void createTitle(Promotion promotion, List<Worker> workers, String name) {
        intitializeTitle(new Title(promotion, workers.size(), name), workers);
    }

    private void intitializeTitle(Title title, List<Worker> workers) {
        titleManager.addTitle(title);
        titleManager.addTitleView(new TitleView (title));
        titleManager.awardTitle(title, workers);
       
    }

}
