package wrestling.view.event;

import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
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
        if (ldb.hasType(Worker.class)) {
            Worker worker = ldb.getValue(Worker.class);

            if (!eventScreenController.getWorkersListView().getItems().contains(worker)) {
                eventScreenController.getSegmentPaneControllers().get(eventScreenController.getCurrentSegmentNumber().intValue()).removeWorker(worker);
            }

            eventScreenController.updateLabels();
            eventScreenController.getSegmentPaneControllers().get(eventScreenController.getCurrentSegmentNumber().intValue()).updateLabels();
            eventScreenController.updateSegments();

            //Clear, otherwise we end up with the worker stuck on the dragboard?
            ldb.clearAll();

        }
    }

}
