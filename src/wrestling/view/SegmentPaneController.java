package wrestling.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import wrestling.MainApp;
import wrestling.model.Match;
import wrestling.model.MatchFinishes;
import wrestling.model.Segment;
import wrestling.model.Worker;
import wrestling.model.MatchRules;

public class SegmentPaneController implements Initializable {

    @FXML
    private VBox teamsPane;

    @FXML
    private Button addTeamButton;

    @FXML
    private Button removeTeamButton;

    @FXML
    private ComboBox matchRules;

    @FXML
    private ComboBox matchFinishes;

    private List<Pane> teamPanes;
    private List<TeamPaneController> teamPaneControllers;
    private int defaultTeams = 2;

    private EventScreenController eventScreenController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        teamPanes = new ArrayList<>();
        teamPaneControllers = new ArrayList<>();

    }

    public void initializeMore() {

        for (int i = 0; i < defaultTeams; i++) {

            addTeam();

        }

        matchRules.setOnAction((event) -> {
            updateMatchRulesCombobox();
            updateLabels();

        });

        matchFinishes.setOnAction((event) -> {
            updateLabels();
        });

        matchRules.setItems(FXCollections.observableArrayList(MatchRules.values()));
        matchRules.getSelectionModel().selectFirst();

        updateMatchRulesCombobox();

    }

    private void updateMatchRulesCombobox() {
        MatchRules current = (MatchRules) matchRules.getSelectionModel().getSelectedItem();
        MatchFinishes lastFinish = (MatchFinishes) matchFinishes.getSelectionModel().getSelectedItem();
        List<MatchFinishes> finishes = new ArrayList<>();
        for (MatchFinishes f : MatchFinishes.values()) {

            if (current.nodq() && f.nodq()) {
                finishes.add(f);
            } else if (!current.nodq()) {
                finishes.add(f);
            }
        }

        matchFinishes.setItems(FXCollections.observableArrayList(finishes));
        if (matchFinishes.getItems().contains(lastFinish)) {
            matchFinishes.getSelectionModel().select(lastFinish);
        } else {
            matchFinishes.getSelectionModel().selectFirst();
        }
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

    //removes a worker from any teams he might be on
    //called from a teamPaneController when adding a worker
    //from another team to avoid duplicates
    public void removeWorker(Worker worker) {
        for (TeamPaneController controller : teamPaneControllers) {
            controller.removeWorker(worker);
        }
    }

    private void addTeam() {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/TeamPane.fxml"));
            AnchorPane teamPane = (AnchorPane) loader.load();
            teamPanes.add(teamPane);

            //keep a list of the controllers too
            TeamPaneController controller = loader.getController();
            teamPaneControllers.add(controller);

            controller.setEventScreenController(eventScreenController);
            controller.setSegmentPaneController(this);
            controller.setTeamNumber(teamPaneControllers.size() - 1);
            controller.initializeMore();

            teamsPane.getChildren().add(teamPane);

            eventScreenController.updateSegments();

        } catch (IOException e) {
        }

    }

    public void updateLabels() {
        for (TeamPaneController controller : teamPaneControllers) {
            controller.updateLabels();

        }
        eventScreenController.updateSegments();
    }

    private void removeTeam() {

        //we may want an alternate method where a minimum of one team is kept
        if (teamPanes.size() > 0 && teamPaneControllers.size() > 0) {

            teamPanes.remove(teamPanes.size() - 1);
            teamPaneControllers.remove(teamPaneControllers.size() - 1);
            teamsPane.getChildren().remove(teamsPane.getChildren().size() - 1);

            //tell the event screen to update particularly the segment listView
            //because we have changed the segment name
            eventScreenController.updateSegments();

        }

    }

    //called by a team pane from drag dropped to update the team priority
    public void swapTeamIndices(int indexA, int indexB) {
        Collections.swap(teamPanes, indexA, indexB);
        Collections.swap(teamPaneControllers, indexA, indexB);
        //tell the event screen to update the event to reflect the new team priorirty
        eventScreenController.updateSegments();

    }

    public Segment getSegment() {
        //this would return whatever segment we generate, match or angle
        //along with all the rules etc

        Match match = new Match(getTeams(),
                Arrays.asList((MatchRules) matchRules.getSelectionModel().getSelectedItem()),
                Arrays.asList((MatchFinishes) matchFinishes.getSelectionModel().getSelectedItem()));

        return match;
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

}
