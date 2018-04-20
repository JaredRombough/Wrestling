package wrestling.model.interfaces;

import java.util.EnumSet;
import java.util.List;
import javafx.collections.ObservableList;
import wrestling.model.Promotion;
import wrestling.model.controller.GameController;
import wrestling.view.utility.ScreenCode;

public interface iBrowseMode {

    public ObservableList comparators();

    public ScreenCode subScreenCode();

    public List<EnumSet> getSortFilters();

    public List listToBrowse(GameController gameController, Promotion promotion);
}
