package wrestling.view.event.controller;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import wrestling.model.Worker;
import wrestling.view.utility.LocalDragboard;

public class WorkerDragDropHandler implements EventHandler<DragEvent> {

    private final ObservableList<Worker> itemList;
    private final SegmentPaneController segmentPaneController;
    private final EventScreenController eventScreenController;
    private final TeamPaneController teamPaneController;

    public WorkerDragDropHandler(
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
        if (ldb.hasType(Worker.class)) {
            Worker worker = ldb.getValue(Worker.class);

            segmentPaneController.removeWorker(worker);
            itemList.add(worker);

            teamPaneController.updateLabels();
            segmentPaneController.updateLabels();
            eventScreenController.updateLabels();

            //Clear, otherwise we end up with the worker stuck on the dragboard?
            ldb.clearAll();

        }
    }

}
