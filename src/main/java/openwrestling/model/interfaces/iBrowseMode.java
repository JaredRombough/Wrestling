package openwrestling.model.interfaces;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import openwrestling.model.SegmentItem;
import openwrestling.model.controller.GameController;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.view.utility.ScreenCode;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public interface iBrowseMode {

    default ObservableList<Comparator> comparators() {
        return FXCollections.observableArrayList();
    }

    default ScreenCode subScreenCode() {
        return null;
    }

    default List<EnumSet> getSortFilters() {
        return Collections.emptyList();
    }

    default List<SegmentItem> listToBrowse(GameController gameController, Promotion promotion) {
        return Collections.emptyList();
    }
}
