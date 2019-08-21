package openwrestling.model.interfaces;

import java.util.EnumSet;
import java.util.List;
import javafx.collections.ObservableList;
import openwrestling.model.SegmentItem;
import openwrestling.model.controller.GameController;
import openwrestling.model.modelView.PromotionView;
import openwrestling.view.utility.ScreenCode;

public interface iBrowseMode {

    public ObservableList comparators();

    public ScreenCode subScreenCode();

    public List<EnumSet> getSortFilters();

    public List<SegmentItem> listToBrowse(GameController gameController, PromotionView promotion);
}
