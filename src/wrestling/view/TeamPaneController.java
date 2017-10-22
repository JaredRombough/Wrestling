package wrestling.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import wrestling.model.Worker;

public class TeamPaneController extends ControllerBase implements Initializable {

    private static final double CELL_HEIGHT = 33;
    private static final String TAB_DRAG_KEY = "anchorpane";

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
    private ObjectProperty<AnchorPane> draggingTab;

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

    private void preparePaneForSorting() {
        draggingTab = new SimpleObjectProperty<>();
        mainPane.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Dragboard dragboard = mainPane.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(TAB_DRAG_KEY);
                dragboard.setContent(clipboardContent);
                draggingTab.set(mainPane);
                event.consume();
            }
        });
        mainPane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                final Dragboard dragboard = event.getDragboard();
                if (dragboard.hasString()
                        && TAB_DRAG_KEY.equals(dragboard.getString())
                        && draggingTab.get() != null) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    event.consume();
                }
            }
        });
        mainPane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(final DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    Pane parent = (Pane) mainPane.getParent();
                    Object source = event.getGestureSource();
                    int sourceIndex = parent.getChildren().indexOf(source);

                    int targetIndex = parent.getChildren().indexOf(mainPane);

                    List<Node> nodes = new ArrayList<>(parent.getChildren());
                    if (sourceIndex < targetIndex) {
                        Collections.rotate(
                                nodes.subList(sourceIndex, targetIndex + 1), -1);
                    } else {
                        Collections.rotate(
                                nodes.subList(targetIndex, sourceIndex + 1), 1);
                    }
                    parent.getChildren().clear();
                    parent.getChildren().addAll(nodes);
                    //tell the segmentPaneControlller that the team priority has changed
                    segmentPaneController.swapTeamIndices(sourceIndex, targetIndex);

                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
    }

    @Override
    public void initializeMore() {

        preparePaneForSorting();

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
        listView.setCellFactory(new Callback<ListView<Worker>, ListCell<Worker>>() {

            @Override
            public ListCell<Worker> call(ListView<Worker> listView) {
                return new WorkerCell(listView.getItems());
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
