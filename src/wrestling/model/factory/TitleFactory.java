package wrestling.model.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import wrestling.model.Promotion;
import wrestling.model.Title;
import wrestling.model.manager.TitleManager;
import wrestling.model.modelView.TitleView;
import wrestling.model.modelView.WorkerView;

/**
 * called whenever a new title is to be created
 *
 */
public class TitleFactory {

    private final TitleManager titleManager;

    public TitleFactory(TitleManager titleManager) {
        this.titleManager = titleManager;
    }

    public TitleView createTitle(Promotion promotion, String name) {
        return intitializeTitle(new Title(promotion, 1, name), new ArrayList<>());
    }

    //create a title with predetermined attributes
    public TitleView createTitle(Promotion promotion, WorkerView worker, String name) {
        return intitializeTitle(new Title(promotion, 1, name), Arrays.asList(worker));
    }

    //create a title with predetermined attributes
    public TitleView createTitle(Promotion promotion, List<WorkerView> workers, String name) {
        return intitializeTitle(new Title(promotion, workers.size(), name), workers);
    }

    private TitleView intitializeTitle(Title title, List<WorkerView> workers) {
        titleManager.addTitle(title);
        TitleView titleView = new TitleView(title);
        titleManager.addTitleView(titleView);
        titleManager.awardTitle(title, workers);
        return titleView;

    }

}
