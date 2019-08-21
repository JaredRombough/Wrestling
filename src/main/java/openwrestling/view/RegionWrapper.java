package openwrestling.view;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class RegionWrapper {

    public GridPane wrapper;
    public Region region;

    public RegionWrapper(GridPane gridPane, Region region) {
        this.wrapper = gridPane;
        this.region = region;
    }
}
