package openwrestling.model.segment.constants.browse.mode;

import openwrestling.model.SegmentItem;
import openwrestling.model.controller.GameController;
import openwrestling.model.gameObjects.Promotion;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public interface iBrowseMode {

    default List<EnumSet> getSortFilters() {
        return Collections.emptyList();
    }

    default List<SegmentItem> listToBrowse(GameController gameController, Promotion promotion) {
        return Collections.emptyList();
    }
}
