package openwrestling.view.event.controller;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;
import openwrestling.model.gameObjects.Segment;

public class SegmentNameItem {

    ObjectProperty<Segment> segment = new SimpleObjectProperty();

    public static Callback<SegmentNameItem, Observable[]> extractor() {
        return (SegmentNameItem param) -> new Observable[]{param.segment};
    }

}
