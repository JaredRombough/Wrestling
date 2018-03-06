package wrestling.view.event;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import wrestling.model.MatchFinishes;
import wrestling.model.MatchRules;
import wrestling.model.Worker;
import wrestling.model.modelView.SegmentView;
import wrestling.model.modelView.SegmentTeam;
import wrestling.view.utility.Screen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class SegmentPaneController extends ControllerBase implements Initializable {

    private static final int DEFAULTTEAMS = 2;

    @FXML
    private VBox teamsPane;

    @FXML
    private Button addTeamButton;

    @FXML
    private ComboBox matchRules;

    @FXML
    private ComboBox matchFinishes;

    @FXML
    private Button matchButton;

    @FXML
    private Button angleButton;

    @FXML
    private Button interferenceButton;

    private List<Screen> teamPaneWrappers;

    private EventScreenController eventScreenController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this.getClass());
        teamPaneWrappers = new ArrayList<>();

    }

    @Override
    public void initializeMore() {

        for (int i = 0; i < DEFAULTTEAMS; i++) {

            addTeam(TeamType.DEFAULT);

        }

        intitializeMatchFinshesCombobox();

        initializeMatchRulesCombobox();

        updateMatchRulesCombobox();

    }

    private void intitializeMatchFinshesCombobox() {

        matchFinishes.getSelectionModel().selectedItemProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
            if (newValue != null) {
                //only update when a new value is being selected
                updateLabels();
            }

        });
        //set the list cells display
        matchFinishes.setCellFactory(new Callback<ListView<MatchFinishes>, ListCell<MatchFinishes>>() {
            @Override
            public ListCell<MatchFinishes> call(ListView<MatchFinishes> p) {
                return new ListCell<MatchFinishes>() {

                    @Override
                    protected void updateItem(MatchFinishes item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.description());
                        }
                    }
                };
            }
        });

        //set the main cell display
        matchFinishes.setButtonCell(
                new ListCell<MatchFinishes>() {
            @Override
            protected void updateItem(MatchFinishes t, boolean bln) {
                super.updateItem(t, bln);
                if (bln) {
                    setText("");
                } else {
                    setText(t.description());
                }
            }
        });

    }

    private void initializeMatchRulesCombobox() {

        //for when selection changes
        matchRules.setOnAction(event -> {
            updateMatchRulesCombobox();
            updateLabels();
        });

        //set the items, here we could filter out rules that the promotion can't use
        matchRules.setItems(FXCollections.observableArrayList(MatchRules.values()));

        //set the list cells display
        matchRules.setCellFactory(new Callback<ListView<MatchRules>, ListCell<MatchRules>>() {
            @Override
            public ListCell<MatchRules> call(ListView<MatchRules> p) {
                return new ListCell<MatchRules>() {

                    @Override
                    protected void updateItem(MatchRules item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.description());
                        }
                    }
                };
            }
        });

        //set the main cell display
        matchRules.setButtonCell(
                new ListCell<MatchRules>() {
            @Override
            protected void updateItem(MatchRules t, boolean bln) {
                super.updateItem(t, bln);
                if (bln) {
                    setText("");
                } else {
                    setText(t.description());
                }
            }
        });

        matchRules.getSelectionModel().selectFirst();

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
            addTeam(TeamType.DEFAULT);
        } else if (event.getSource() == matchButton) {
            for (Screen screen : teamPaneWrappers) {
                ((TeamPaneWrapper) screen.controller).setMatch();
            }
        } else if (event.getSource() == angleButton) {
            for (Screen screen : teamPaneWrappers) {
                ((TeamPaneWrapper) screen.controller).setAngle();
            }
        } else if (event.getSource() == interferenceButton) {
            addTeam(TeamType.INTERFERENCE);
        }
    }

    //removes a worker from any teams he might be on
    //called from a teamPaneController when adding a worker
    //from another team to avoid duplicates
    public void removeWorker(Worker worker) {
        for (Screen screen : teamPaneWrappers) {
            ((TeamPaneWrapper) screen.controller).getTeamPaneController().removeWorker(worker);
        }
    }

    public List<Worker> getWorkers() {
        List<Worker> workers = new ArrayList<>();
        for (Screen screen : teamPaneWrappers) {
            workers.addAll(((TeamPaneWrapper) screen.controller).getTeamPaneController().getWorkers());
        }
        return workers;
    }

    private Screen addTeam(TeamType state) {

        Screen teamPaneWrapper = ViewUtils.loadScreenFromResource(ScreenCode.TEAM_PANE_WRAPPER, mainApp, gameController);

        if (state.equals(TeamType.INTERFERENCE)) {
            teamPaneWrappers.add(teamPaneWrapper);
            teamsPane.getChildren().add(teamPaneWrapper.pane);
        } else {
            int indexToInsert = teamPaneWrappers.isEmpty() ? 0 : teamPaneWrappers.size();

            for (int i = 0; i < teamPaneWrappers.size(); i++) {
                if (isInterference(teamPaneWrappers.get(i)) && i > 0) {
                    indexToInsert = i;
                    break;
                }
            }

            teamPaneWrappers.add(indexToInsert, teamPaneWrapper);
            teamsPane.getChildren().add(indexToInsert, teamPaneWrapper.pane);
        }

        ((TeamPaneWrapper) teamPaneWrapper.controller).setTeamType(state);

        teamPaneWrapper.pane.setOnDragDropped((final DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                Pane parent = (Pane) teamPaneWrapper.pane.getParent();
                Object source = event.getGestureSource();
                int sourceIndex = parent.getChildren().indexOf(source);
                int targetIndex = parent.getChildren().indexOf(teamPaneWrapper.pane);
                this.swapTeams(sourceIndex, targetIndex);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        TeamPaneController controller = ((TeamPaneWrapper) teamPaneWrapper.controller).getTeamPaneController();
        controller.setDragDropHandler(this, eventScreenController);
        controller.setTeamNumber(teamPaneWrappers.size() - 1);
        ((TeamPaneWrapper) teamPaneWrapper.controller).getXButton().setOnAction(e -> removeTeam(teamPaneWrapper));

        eventScreenController.updateSegments();

        updateLabels();

        return teamPaneWrapper;

    }

    private boolean isInterference(Screen screen) {
        return screen.controller instanceof TeamPaneWrapper
                && ((TeamPaneWrapper) screen.controller).getTeamType() != null
                && ((TeamPaneWrapper) screen.controller).getTeamType().equals(TeamType.INTERFERENCE);
    }

    private void removeTeam(Screen teamPaneWrapper) {
        if (teamsPane.getChildren().contains(teamPaneWrapper.pane)) {
            teamsPane.getChildren().remove(teamPaneWrapper.pane);
        }
        if (teamPaneWrappers.contains(teamPaneWrapper)) {
            teamPaneWrappers.remove(teamPaneWrapper);
        }
        eventScreenController.updateSegments();
        updateLabels();
    }

    @Override
    public void updateLabels() {
        updateTeamNames();

        for (Screen screen : teamPaneWrappers) {
            if (isInterference(screen)) {
                ((TeamPaneWrapper) screen.controller).setTargets(getTeamNames(teamPaneWrappers.indexOf(screen)));
            }
            screen.controller.updateLabels();

        }
        eventScreenController.updateSegments();

    }

    private List<String> getTeamNames(int forIndex) {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < forIndex; i++) {
            names.add(((TeamPaneWrapper) teamPaneWrappers.get(i).controller).getTeamPaneController().getTeamName());
        }
        return names;
    }

    public void swapTeams(int indexA, int indexB) {
        List<Worker> teamA = getTeamPaneController(indexA).getWorkers();
        List<Worker> teamB = getTeamPaneController(indexB).getWorkers();
        getTeamPaneController(indexA).setWorkers(teamB);
        getTeamPaneController(indexB).setWorkers(teamA);

        eventScreenController.updateSegments();

        updateLabels();
    }

    //this will need to be more complex when more types of segments are added
    private void updateTeamNames() {
        for (Screen screen : teamPaneWrappers) {
            String teamType = "";
            if (matchFinish() != null && matchFinish().equals(MatchFinishes.DRAW)) {
                teamType = "Draw";
            } else if (isInterference(screen)) {
                teamType = "Interference";
            } else {
                switch (teamPaneWrappers.indexOf(screen)) {
                    case 0:
                        teamType = "Winner";
                        break;
                    default:
                        teamType = "Loser";
                        break;
                }
            }
            ((TeamPaneWrapper) screen.controller).setTeamTypeLabel(teamType);

        }
    }

    public SegmentView getSegmentView() {
        //this would return whatever segment we generate, match or angle
        //along with all the rules etc
        SegmentView match = new SegmentView();
        match.setFinish(matchFinish());
        match.setRules(matchRule());
        match.setTeams(getTeams());
        return match;
    }

    private MatchRules matchRule() {
        return (MatchRules) matchRules.getSelectionModel().getSelectedItem();
    }

    private MatchFinishes matchFinish() {
        return (MatchFinishes) matchFinishes.getSelectionModel().getSelectedItem();
    }

    private TeamPaneController getTeamPaneController(int index) {
        return ((TeamPaneWrapper) teamPaneWrappers.get(index).controller).getTeamPaneController();
    }

    /*
    just remove all the teams and add new ones to get back up to the default size
     */
    public void clear() {
        while (!teamPaneWrappers.isEmpty()) {
            removeTeam(teamPaneWrappers.get(0));
        }

        for (int i = 0; i < DEFAULTTEAMS; i++) {
            addTeam(TeamType.DEFAULT);
        }

    }

    private List<SegmentTeam> getTeams() {

        List<SegmentTeam> teams = new ArrayList<>();

        for (Screen screen : teamPaneWrappers) {
            teams.add(new SegmentTeam(
                    ((TeamPaneWrapper) screen.controller).getTeamPaneController().getWorkers(),
                    ((TeamPaneWrapper) screen.controller).getTeamType() == null ? TeamType.DEFAULT
                            : ((TeamPaneWrapper) screen.controller).getTeamType())
            );
        }

        return teams;
    }
}
