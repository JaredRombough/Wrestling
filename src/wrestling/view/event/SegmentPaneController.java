package wrestling.view.event;

import wrestling.model.segmentEnum.TeamType;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import wrestling.model.segmentEnum.MatchFinish;
import wrestling.model.Worker;
import wrestling.model.modelView.SegmentView;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.segmentEnum.AngleType;
import wrestling.model.segmentEnum.OutcomeType;
import wrestling.view.utility.Screen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class SegmentPaneController extends ControllerBase implements Initializable {

    private static final int DEFAULTTEAMS = 2;

    @FXML
    private VBox teamsPane;

    @FXML
    private Button matchButton;

    @FXML
    private Button angleButton;

    @FXML
    private AnchorPane optionsPane;

    private Screen angleOptionsScreen;
    private Screen matchOptionsScreen;
    private MatchOptions matchOptions;

    private List<Screen> teamPaneWrappers;

    private EventScreenController eventScreenController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this.getClass());
        teamPaneWrappers = new ArrayList<>();
        //in development
        //angleButton.setDisable(true);

    }

    @Override
    public void initializeMore() {

        matchOptionsScreen = ViewUtils.loadScreenFromResource(ScreenCode.MATCH_OPTIONS, mainApp, gameController, optionsPane);

        matchOptions = (MatchOptions) matchOptionsScreen.controller;

        matchOptions.getAddTeamButton().setOnAction(e -> addTeam(TeamType.DEFAULT));
        matchOptions.getInterferenceButton().setOnAction(e -> addTeam(TeamType.INTERFERENCE));
        matchOptions.getMatchFinishes().setOnAction(e -> updateLabels());
        matchOptions.getMatchRules().setOnAction(e -> updateLabels());

        angleOptionsScreen = ViewUtils.loadScreenFromResource(ScreenCode.ANGLE_OPTIONS, mainApp, gameController);

        ((AngleOptions) angleOptionsScreen.controller).getAngleTypeComboBox().valueProperty().addListener(new ChangeListener<AngleType>() {
            @Override
            public void changed(ObservableValue ov, AngleType t, AngleType t1) {
                if (t1 != null) {
                    // clearControls();
                    switch (t1) {
                        case PROMO:
                            //    addPromoComboBox();
                            break;
                        case OFFER:
                            //         addJoinTeamComboBox();
                            break;
                        case CHALLENGE:
                            //          addShowComboBox();
                            break;
                        default:
                            break;
                    }
//                    if (violenceComboBox != null && vBox.getChildren().contains(violenceComboBox.wrapper)) {
//                        violenceComboBox.wrapper.toFront();
//                    }
                }
            }
        });

        for (int i = 0; i < DEFAULTTEAMS; i++) {

            addTeam(TeamType.DEFAULT);

        }

    }

    public void setEventScreenController(EventScreenController eventScreenController) {
        this.eventScreenController = eventScreenController;

    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {

        if (event.getSource() == matchButton) {
            for (Screen screen : teamPaneWrappers) {
                ((TeamPaneWrapper) screen.controller).setMatch();
                setOptionsPane(matchOptionsScreen.pane);
            }
        } else if (event.getSource() == angleButton) {
            for (Screen screen : teamPaneWrappers) {
                ((TeamPaneWrapper) screen.controller).setAngle();
                setOptionsPane(angleOptionsScreen.pane);
            }
        }
    }

    private void setOptionsPane(Pane pane) {
        optionsPane.getChildren().clear();
        ViewUtils.anchorPaneToParent(optionsPane, pane);
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
            TeamPaneWrapper controller = (TeamPaneWrapper) screen.controller;
            controller.setTargets(getOtherTeams(teamPaneWrappers.indexOf(screen)));

            screen.controller.updateLabels();

        }
        eventScreenController.updateSegments();

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
            OutcomeType outcomeType = null;
            String teamType = "";
            if (matchOptions.getMatchFinish() != null && matchOptions.getMatchFinish().equals(MatchFinish.DRAW)) {
                teamType = "Draw";
                outcomeType = OutcomeType.DRAW;
            } else if (isInterference(screen)) {
                teamType = "Interference";
            } else {
                switch (teamPaneWrappers.indexOf(screen)) {
                    case 0:
                        teamType = "Winner";
                        outcomeType = OutcomeType.WINNER;
                        break;
                    default:
                        teamType = "Loser";
                        outcomeType = OutcomeType.LOSER;
                        break;
                }
            }
            ((TeamPaneWrapper) screen.controller).setTeamTypeLabel(teamType);
            ((TeamPaneWrapper) screen.controller).setOutcomeType(outcomeType);

        }
    }

    public SegmentView getSegmentView() {
        //this would return whatever segment we generate, match or angle
        //along with all the rules etc
        SegmentView segmentView = new SegmentView();
        segmentView.setFinish(matchOptions.getMatchFinish());
        segmentView.setRules(matchOptions.getMatchRule());
        segmentView.setTeams(getTeams());
        return segmentView;
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
            TeamPaneWrapper controller = (TeamPaneWrapper) screen.controller;
            teams.add(controller.getTeam());
        }

        return teams;
    }

    private List<SegmentTeam> getOtherTeams(int notThisIndex) {

        List<SegmentTeam> teams = new ArrayList<>();

        for (Screen screen : teamPaneWrappers) {
            SegmentTeam team = ((TeamPaneWrapper) screen.controller).getTeam();
            if (team != null && teamPaneWrappers.indexOf(screen) < notThisIndex) {
                teams.add(team);
            }
        }

        return teams;
    }
}
