package wrestling.model.interfaces;

import java.util.EnumSet;
import java.util.List;
import javafx.collections.ObservableList;
import wrestling.model.SegmentItem;
import wrestling.model.controller.GameController;
import wrestling.model.modelView.PromotionView;
import wrestling.view.utility.ScreenCode;

public interface iBrowseMode {

    public ObservableList comparators();

    public ScreenCode subScreenCode();

    public List<EnumSet> getSortFilters();

    public List<SegmentItem> listToBrowse(GameController gameController, PromotionView promotion);
}