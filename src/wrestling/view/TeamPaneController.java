package wrestling.view;

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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.util.Callback;
import wrestling.model.Worker;

public class TeamPaneController implements Initializable {

    @FXML
    private ListView teamListView;

    @FXML
    private Pane mainPane;

    @FXML
    private Label teamNameLabel;
    
    @FXML
    private GridPane gridPane;

    private double cellHeight = 30;
    private double defaultMainPaneHeight;

    private EventScreenController eventScreenController;
    private SegmentPaneController segmentPaneController;

    public void setEventScreenController(EventScreenController eventScreenController) {
        this.eventScreenController = eventScreenController;
    }

    public void setSegmentPaneController(SegmentPaneController segmentPaneController) {
        this.segmentPaneController = segmentPaneController;
    }

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

    }

    private class DragDropHandler implements EventHandler<DragEvent> {

        private final ObservableList<Worker> itemList;

        DragDropHandler(ObservableList<Worker> itemList) {

            this.itemList = itemList;

        }

        @Override
        public void handle(DragEvent event) {

            LocalDragboard ldb = LocalDragboard.getInstance();
            if (ldb.hasType(Worker.class)) {
                Worker worker = ldb.getValue(Worker.class);

                itemList.add(worker);

                double height = cellHeight * itemList.size();
                
                mainPane.setMinHeight(defaultMainPaneHeight + cellHeight + height);
                teamListView.setMinHeight(height);

                updateTeamNameLabel();
                eventScreenController.updateEvent();

            }
        }

    }

    private void updateTeamNameLabel() {
        
        String oldLabel = teamNameLabel.getText();
       
        String string = getTeamName();
        
        segmentPaneController.updateTeamsSorter(oldLabel, string);
        
        teamNameLabel.setText(getTeamName());
        
    }

    public void initializeMore() {
        
        
    

        final EventHandler<DragEvent> dragOverHandler = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {

                dragEvent.acceptTransferModes(TransferMode.MOVE);

            }
        };

        setWorkerCellFactory(teamListView);

        teamListView.setOnDragOver(dragOverHandler);

        teamListView.setOnDragDropped(new TeamPaneController.DragDropHandler(teamListView.getItems()));

        double height = cellHeight;
        teamListView.setPrefHeight(height);
        mainPane.setPrefHeight(defaultMainPaneHeight + cellHeight + height);

        //update the label
        updateTeamNameLabel();

    }

    private void setWorkerCellFactory(ListView listView) {
        listView.setCellFactory(new Callback<ListView<Worker>, ListCell<Worker>>() {

            @Override
            public ListCell<Worker> call(ListView<Worker> listView) {
                return new WorkerCell();
            }
        });

    }

    public List<Worker> getWorkers() {

        List workers = new ArrayList<Worker>(teamListView.getItems());
        return workers;

    }

    public String getTeamName() {
        String string = new String();

        if (getWorkers().size() > 0) {

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
