package wrestling.view.event.controller;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import wrestling.model.SegmentItem;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.TitleView;
import wrestling.model.utility.StaffUtils;
import wrestling.view.utility.LocalDragboard;

public class SegmentItemDragDropHandler implements EventHandler<DragEvent> {

    private final ObservableList<SegmentItem> itemList;
    private final SegmentPaneController segmentPaneController;

    public SegmentItemDragDropHandler(
            SegmentPaneController segmentPaneController,
            ObservableList<SegmentItem> itemList) {
        this.itemList = itemList;
        this.segmentPaneController = segmentPaneController;
    }

    @Override
    public void handle(DragEvent event) {

        LocalDragboard ldb = LocalDragboard.getINSTANCE();
        if (ldb.hasInterface(SegmentItem.class)) {
            SegmentItem segmentItem = ldb.getValue(SegmentItem.class);

            segmentPaneController.removeSegmentItems(segmentItem.getSegmentItems());

            if (StaffUtils.isRef(segmentItem)) {
                segmentPaneController.setRef((StaffView) segmentItem);
            } else {
                itemList.addAll(segmentItem.getSegmentItems());
            }

            if (segmentItem instanceof TitleView) {
                TitleView titleView = (TitleView) segmentItem;
                if (!titleView.getChampions().isEmpty()) {
                    segmentPaneController.addTeam(((TitleView) segmentItem).getChampions(), true);
                }
            }

            segmentPaneController.updateLabels();

            ldb.clearAll();
        }
    }
}
