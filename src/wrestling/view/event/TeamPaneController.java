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
import wrestling.model.utility.ModelUtilityFunctions;
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

    private int teamNumber;

    public int getTeamNumber() {
        return this.teamNumber;
    }

    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        defaultMainPaneHeight = mainPane.getHeight();
        updateTeamNameLabel();
    }

    public void removeWorker(Worker worker) {

        if (teamListView.getItems().contains(worker)) {
            teamListView.getItems().remove(worker);
            updateLabels();
        }
    }

    @Override
    public void updateLabels() {

        updateTeamListViewHeight();

        updateTeamNameLabel();
    }

    private void updateTeamNameLabel() {
        if (!getWorkers().isEmpty()) {
            teamNameLabel.setText(ModelUtilityFunctions.slashShortNames(getWorkers()));
        } else {
            teamNameLabel.setText("(Empty Team)");
        }
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

        double height = CELL_HEIGHT;
        teamListView.setPrefHeight(height);
        mainPane.setPrefHeight(defaultMainPaneHeight + CELL_HEIGHT + height);

    }

    public ObservableList<Worker> getItems() {
        return teamListView.getItems();
    }

    public void setDragDropHandler(SegmentPaneController segmentPaneController, EventScreenController eventScreenController) {
        teamListView.setOnDragDropped(new WorkerDragDropHandler(segmentPaneController, eventScreenController, this));
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
    
    public void setWorkers(List<Worker> workers) {
        teamListView.getItems().clear();
        teamListView.getItems().addAll(workers);
        updateTeamNameLabel();
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

}
