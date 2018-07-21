package wrestling.view.event.controller;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import wrestling.model.SegmentItem;
import wrestling.model.modelView.TitleView;
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

            segmentPaneController.removeSegmentItems(segmentItem.getSegmentItems());
            itemList.addAll(segmentItem.getSegmentItems());

            if (segmentItem instanceof TitleView) {
                TitleView titleView = (TitleView) segmentItem;
                if (!titleView.getChampions().isEmpty()) {
                    segmentPaneController.addTeam(((TitleView) segmentItem).getChampions());
                }
            }

            teamPaneController.updateLabels();
            segmentPaneController.updateLabels();
            eventScreenController.updateLabels();

            ldb.clearAll();

        }
    }

}
