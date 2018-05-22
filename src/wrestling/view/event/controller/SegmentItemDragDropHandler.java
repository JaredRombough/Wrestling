package wrestling.view.event.controller;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import wrestling.model.SegmentItem;
import wrestling.view.utility.LocalDragboard;

public class SegmentItemDragDropHandler implements EventHandler<DragEvent> {

    private final ObservableList<SegmentItem> itemList;
    private final SegmentPaneController segmentPaneController;
    private final EventScreenController eventScreenController;
    private final TeamPaneController teamPaneController;

    public SegmentItemDragDropHandler(
            SegmentPaneController segmentPaneController,
            EventScreenController eventScreenController,
            TeamPaneController teamPaneController) {

        this.itemList = teamPaneController.getItems();
        this.segmentPaneController = segmentPaneController;
        this.eventScreenController = eventScreenController;
        this.teamPaneController = teamPaneController;

    }

    @Override
    public void handle(DragEvent event) {

        LocalDragboard ldb = LocalDragboard.getINSTANCE();
        if (ldb.hasInterface(SegmentItem.class)) {
            SegmentItem segmentItem = ldb.getValue(SegmentItem.class);

            for (SegmentItem subItem : segmentItem.getSegmentItems()) {
                segmentPaneController.removeSegmentItem(subItem);
                itemList.add(subItem);
            }

            teamPaneController.updateLabels();
            segmentPaneController.updateLabels();
            eventScreenController.updateLabels();

            ldb.clearAll();

        }
    }

}
