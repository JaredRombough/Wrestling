package wrestling.view.event.controller;

import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import wrestling.model.SegmentItem;
import wrestling.model.Worker;
import wrestling.view.utility.LocalDragboard;

/*
    to be used by the workersListView on the left of the screen
    should only be needed for when the user is dropping a worker
    on the listView that has been dragged from one of the teams
 */
public class WorkersListViewDragDropHandler implements EventHandler<DragEvent> {

    private final EventScreenController eventScreenController;

    public WorkersListViewDragDropHandler(EventScreenController eventScreenController) {
        this.eventScreenController = eventScreenController;
    }

    @Override
    public void handle(DragEvent event) {

        LocalDragboard ldb = LocalDragboard.getINSTANCE();
        if (ldb.hasInterface(SegmentItem.class)) {
            SegmentItem segmentItem = ldb.getValue(SegmentItem.class);

            if (!eventScreenController.getListView().getItems().contains(segmentItem)) {
                eventScreenController.currentSegmentPaneController().removeSegmentItem(segmentItem);
            }

            eventScreenController.updateLabels();

            //Clear, otherwise we end up with the worker stuck on the dragboard?
            ldb.clearAll();

        }
    }

}
