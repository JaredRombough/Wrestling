package wrestling.view.event.controller;

import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import wrestling.model.SegmentItem;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.TitleView;
import wrestling.model.utility.StaffUtils;
import wrestling.view.utility.LocalDragboard;

public class SegmentItemDragDropHandler implements EventHandler<DragEvent> {

    private final TeamPaneWrapper teamPaneWrapper;
    private final SegmentPaneController segmentPaneController;

    public SegmentItemDragDropHandler(
            SegmentPaneController segmentPaneController,
            TeamPaneWrapper teamPaneController) {
        this.teamPaneWrapper = teamPaneController;
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
                segmentItem.getSegmentItems().forEach(item -> teamPaneWrapper.addSegmentItem(item));
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
