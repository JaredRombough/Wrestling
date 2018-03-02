package wrestling.view.event;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import wrestling.model.Worker;
import wrestling.view.utility.LocalDragboard;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class TeamPaneController extends ControllerBase implements Initializable {

    private static final double CELL_HEIGHT = 33;

    @FXML
    private ListView teamListView;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Label teamNameLabel;

    private double defaultMainPaneHeight;

    private EventScreenController eventScreenController;
    private SegmentPaneController segmentPaneController;
    private int teamNumber;

    public void setEventScreenController(EventScreenController eventScreenController) {
        this.eventScreenController = eventScreenController;
    }

    public void setSegmentPaneController(SegmentPaneController segmentPaneController) {
        this.segmentPaneController = segmentPaneController;
    }

    public int getTeamNumber() {
        return this.teamNumber;
    }

    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        defaultMainPaneHeight = mainPane.getHeight();
    }

    public void removeWorker(Worker worker) {

        if (teamListView.getItems().contains(worker)) {
            teamListView.getItems().remove(worker);
        }
    }

    @Override
    public void updateLabels() {

        updateTeamListViewHeight();
    }

    public void setTeamNameLabel(String string) {
        teamNameLabel.setText(string);
    }

    private void updateTeamListViewHeight() {

        double height = CELL_HEIGHT * teamListView.getItems().size() + 5;

        mainPane.setMinHeight(defaultMainPaneHeight + CELL_HEIGHT + height);
        teamListView.setMinHeight(height);
    }

    @Override
    public void initializeMore() {

        final EventHandler<DragEvent> dragOverHandler = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {

                dragEvent.acceptTransferModes(TransferMode.MOVE);

            }
        };

        setWorkerCellFactory(teamListView);

        teamListView.setOnDragOver(dragOverHandler);

        teamListView.setOnDragDropped(new DragDropHandler(teamListView.getItems()));

        double height = CELL_HEIGHT;
        teamListView.setPrefHeight(height);
        mainPane.setPrefHeight(defaultMainPaneHeight + CELL_HEIGHT + height);

    }

    private void setWorkerCellFactory(ListView listView) {
        listView.setCellFactory(lv -> new ListCell<Worker>() {

            @Override
            public void updateItem(final Worker worker, boolean empty) {
                super.updateItem(worker, empty);
                ViewUtils.initListCellForWorkerDragAndDrop(this, worker, empty);
            }

        });
    }

    public List<Worker> getWorkers() {

        return new ArrayList<>(teamListView.getItems());

    }

    public String getTeamName() {
        String string = "";

        if (!getWorkers().isEmpty()) {

            for (int i = 0; i < getWorkers().size(); i++) {
                Worker worker = getWorkers().get(i);
                string += worker.getName();
                if (i < getWorkers().size() - 1) {
                    string += "/";
                }
            }
        } else {
            string += "Team #" + teamNumber;
        }

        return string;
    }

    private class DragDropHandler implements EventHandler<DragEvent> {

        private final ObservableList<Worker> itemList;

        DragDropHandler(ObservableList<Worker> itemList) {

            this.itemList = itemList;

        }

        @Override
        public void handle(DragEvent event) {

            LocalDragboard ldb = LocalDragboard.getINSTANCE();
            if (ldb.hasType(Worker.class)) {
                Worker worker = ldb.getValue(Worker.class);

                segmentPaneController.removeWorker(worker);
                itemList.add(worker);

                updateLabels();
                segmentPaneController.updateLabels();
                eventScreenController.updateSegments();

                //Clear, otherwise we end up with the worker stuck on the dragboard?
                ldb.clearAll();

            }
        }

    }

}
