package wrestling.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import wrestling.MainApp;
import wrestling.model.Match;
import wrestling.model.Segment;
import wrestling.model.Worker;

/**
 *
 *
 */
public class SegmentPaneController implements Initializable {

    @FXML
    private VBox teamsPane;

    @FXML
    private ListView<String> teamSorter;

    @FXML
    private Pane stipulationsPane;

    @FXML
    private Pane summaryPane;

    @FXML
    private Button addTeamButton;

    @FXML
    private Button removeTeamButton;

    private List<Pane> teamPanes;
    private List<TeamPaneController> teamPaneControllers;
    private int defaultTeams = 2;

    private EventScreenController eventScreenController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        teamPanes = new ArrayList<Pane>();
        teamPaneControllers = new ArrayList<TeamPaneController>();

    }

    public void initializeMore() {

        for (int i = 0; i < defaultTeams; i++) {

            addTeam();

        }

        initializeTeamSorter();
    }

    private void initializeTeamSorter() {

        teamSorter.setCellFactory(param -> new SorterCell());

    }

    public void setEventScreenController(EventScreenController eventScreenController) {
        this.eventScreenController = eventScreenController;

    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        if (event.getSource() == addTeamButton) {

            addTeam();

        } else if (event.getSource() == removeTeamButton) {
            removeTeam();

        }
    }

    private void addTeam() {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/TeamPane.fxml"));
            Pane teamPane = (Pane) loader.load();
            teamPanes.add(teamPane);

            //keep a list of the controllers too
            TeamPaneController controller = loader.getController();
            teamPaneControllers.add(controller);

            controller.setEventScreenController(eventScreenController);
            controller.setSegmentPaneController(this);
            controller.setTeamNumber(teamPaneControllers.size());
            controller.initializeMore();

            teamsPane.getChildren().add(teamPane);
            teamSorter.getItems().add(controller.getTeamName());

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    private void removeTeam() {

        //we may want an alternate method where a minimum of one team is kept
        if (teamPanes.size() > 0 && teamPaneControllers.size() > 0) {

            String teamName = teamPaneControllers.get(teamPaneControllers.size() - 1).getTeamName();

            ObservableList<String> newList = FXCollections.observableArrayList();

            //only keep the entries that aren't the one we want to remove
            for (String string : teamSorter.getItems()) {
                if (!string.equals(teamName)) {
                    newList.add(string);

                }

            }

            teamSorter.setItems(newList);

            teamPanes.remove(teamPanes.size() - 1);
            teamPaneControllers.remove(teamPaneControllers.size() - 1);
            teamsPane.getChildren().remove(teamsPane.getChildren().size() - 1);

        }

    }

    public Segment getSegment() {
        //this would return whatever segment we generate, match or angle
        //along with all the rules etc

        Match match = new Match(getTeams());

        return match;
    }

    public void updateTeamsSorter(String oldText, String newText) {

        ObservableList<String> newList = FXCollections.observableArrayList();

        for (String string : teamSorter.getItems()) {

            if (string.equals(oldText)) {
                string = newText;
            }

            newList.add(string);
        }

        teamSorter.setItems(null);
        teamSorter.setItems(newList);

    }

    /*
    just remove all the teams and add new ones to get back up to the default size
     */
    public void clear() {

        while (teamPanes.size() > 0 && teamPaneControllers.size() > 0) {
            removeTeam();
        }

        for (int i = 0; i < defaultTeams; i++) {
            addTeam();
        }

    }

    private List<List<Worker>> getTeams() {

        List<List<Worker>> teams = new ArrayList<>();

        for (TeamPaneController teamPaneController : teamPaneControllers) {
            teams.add(teamPaneController.getWorkers());
        }

        return teams;
    }

    /*
    special cell for the teamsorter
     */
    private class SorterCell extends ListCell<String> {

        public SorterCell() {
            ListCell thisCell = this;

            setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (getText() == null) {

                        return;
                    }

                    Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(getText());

                    dragboard.setContent(content);

                    event.consume();
                }
            });

            setOnDragOver(event -> {
                if (event.getGestureSource() != thisCell
                        && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }

                event.consume();
            });

            setOnDragEntered(event -> {
                if (event.getGestureSource() != thisCell
                        && event.getDragboard().hasString()) {
                    setOpacity(0.3);
                }
            });

            setOnDragExited(event -> {
                if (event.getGestureSource() != thisCell
                        && event.getDragboard().hasString()) {
                    setOpacity(1);
                }
            });

            setOnDragDropped(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent event) {
                    if (getText() == null) {
                        return;
                    }

                    Dragboard db = event.getDragboard();
                    boolean success = false;

                    if (db.hasString()) {
                        ObservableList<String> items = getListView().getItems();
                        int draggedIdx = items.indexOf(db.getString());
                        int thisIdx = items.indexOf(getText());

                        items.set(draggedIdx, getText());
                        items.set(thisIdx, db.getString());

                        List<String> itemscopy = new ArrayList<>(getListView().getItems());
                        getListView().getItems().setAll(itemscopy);

                        success = true;
                    }
                    event.setDropCompleted(success);

                    event.consume();
                }
            });

            setOnDragDone(DragEvent::consume);
        }

        @Override
        protected void updateItem(String item, boolean empty) {

            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.toString());
            }
        }
    }

}
