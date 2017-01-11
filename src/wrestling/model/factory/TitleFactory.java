package wrestling.model.factory;

import wrestling.model.Promotion;
import wrestling.model.Title;
import wrestling.model.Worker;

/**
 * called whenever a new title is to be
 * created
 *
 */
public final class TitleFactory {

    //create a title with predetermined attributes
    public static void createTitle(Promotion promotion, Worker worker, String name) {
        Title title = new Title(promotion, worker, name);
        promotion.addTitle(title);
        worker.addTitle(title);
    }

}
