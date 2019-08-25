package openwrestling.model.interfaces;

import java.util.EnumSet;
import java.util.List;
import javafx.collections.ObservableList;
import openwrestling.model.SegmentItem;
import openwrestling.model.controller.GameController;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.view.utility.ScreenCode;

public interface iBrowseMode {

    ObservableList comparators();

    ScreenCode subScreenCode();

    List<EnumSet> getSortFilters();

    List<SegmentItem> listToBrowse(GameController gameController, Promotion promotion);
}
