package wrestling.view.event.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import wrestling.model.AngleParams;
import wrestling.model.MatchParams;
import wrestling.model.SegmentItem;
import wrestling.model.Worker;
import wrestling.model.interfaces.iSegmentLength;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.modelView.SegmentView;
import wrestling.model.segmentEnum.AngleLength;
import wrestling.model.segmentEnum.AngleType;
import wrestling.model.segmentEnum.MatchFinish;
import wrestling.model.segmentEnum.MatchLength;
import wrestling.model.segmentEnum.OutcomeType;
import wrestling.model.segmentEnum.SegmentType;
import wrestling.model.segmentEnum.TeamType;
import wrestling.model.utility.ModelUtils;
import wrestling.view.utility.ButtonWrapper;
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
    private AnchorPane segmentLengthAnchor;

    @FXML
    private AnchorPane optionsPane;

    @FXML
    private Button addTeamButton;

    @FXML
    private Button interferenceButton;

    private List<Button> segmentTypeButtons;

    private Screen angleOptionsScreen;
    private AngleOptions angleOptions;
    private Screen matchOptionsScreen;
    private MatchOptions matchOptions;
    private ButtonWrapper segmentLengthWrapper;
    private iSegmentLength segmentLength;

    private List<Screen> wrapperScreens;

    private EventScreenController eventScreenController;

    private SegmentType segmentType;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this.getClass());
        wrapperScreens = new ArrayList<>();
        segmentTypeButtons = new ArrayList<>();

    }

    @Override
    public void initializeMore() {

        segmentTypeButtons.addAll(Arrays.asList(angleButton, matchButton));

        matchOptionsScreen = ViewUtils.loadScreenFromResource(ScreenCode.MATCH_OPTIONS, mainApp, gameController, optionsPane);
        matchOptions = (MatchOptions) matchOptionsScreen.controller;

        segmentType = SegmentType.MATCH;
        ViewUtils.updateSelectedButton(matchButton, segmentTypeButtons);

        segmentLength = MatchLength.FIFTEEN;
        segmentLengthWrapper = new ButtonWrapper(FXCollections.observableArrayList(MatchLength.values()), 3);
        ViewUtils.anchorRegionToParent(segmentLengthAnchor, segmentLengthWrapper.getGridPane());
        for (Button button : segmentLengthWrapper.getButtons()) {
            button.setOnAction(e -> {
                segmentLength = ((iSegmentLength) segmentLengthWrapper.updateSelected(button));
                updateLabels();
            });
        }
        segmentLengthWrapper.updateSelected(segmentLengthWrapper.getItems().indexOf(segmentLength));

        initializeMatchOptions();
        initializeAngleOptions();

        addTeamButton.setOnAction(e -> addTeam(
                angleOptions.getAngleType().addTeamType()
        ));
        interferenceButton.setOnAction(e -> addTeam(TeamType.INTERFERENCE));

    }

    private void initializeMatchOptions() {

        matchOptions.getMatchFinishes().setOnAction(e -> updateLabels());
        matchOptions.getMatchRules().setOnAction(e -> updateLabels());

    }

    private void initializeAngleOptions() {
        angleOptionsScreen = ViewUtils.loadScreenFromResource(ScreenCode.ANGLE_OPTIONS, mainApp, gameController);
        angleOptions = (AngleOptions) angleOptionsScreen.controller;

        angleOptions.getAngleTypeComboBox().valueProperty().addListener(new ChangeListener<AngleType>() {
            @Override
            public void changed(ObservableValue ov, AngleType t, AngleType t1) {
                if (t1 != null) {
                    updateLabels();
                }
            }
        });
        angleOptions.getAngleTypeComboBox().getSelectionModel().selectFirst();

        angleOptions.getCombo1().valueProperty().addListener((ObservableValue ov, Object t, Object t1) -> {
            angleOptionChanged(t1);
        });
        angleOptions.getCombo2().valueProperty().addListener((ObservableValue ov, Object t, Object t1) -> {
            angleOptionChanged(t1);
        });

        for (int i = 0; i < DEFAULTTEAMS; i++) {

            addTeam(TeamType.DEFAULT);

        }
    }

    public void setEventScreenController(EventScreenController eventScreenController) {
        this.eventScreenController = eventScreenController;

    }

    private void angleOptionChanged(Object obj) {
        // if(obj instanceof)
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {

        Button button = (Button) event.getSource();

        if (button == matchButton) {
            for (Screen screen : wrapperScreens) {
                ((TeamPaneWrapper) screen.controller).changeSegmentType();
                setOptionsPane(matchOptionsScreen.pane);
            }
            setSegmentType(SegmentType.MATCH);
        } else if (button == angleButton) {
            for (Screen screen : wrapperScreens) {
                ((TeamPaneWrapper) screen.controller).changeSegmentType();
                setOptionsPane(angleOptionsScreen.pane);
            }
            setSegmentType(SegmentType.ANGLE);
        }

        ViewUtils.updateSelectedButton(button, segmentTypeButtons);
    }

    private void setOptionsPane(Pane pane) {
        optionsPane.getChildren().clear();
        ViewUtils.anchorRegionToParent(optionsPane, pane);
    }

    //removes a worker from any teams he might be on
    //called from a teamPaneController when adding a worker
    //from another team to avoid duplicates
    public void removeSegmentItem(SegmentItem segmentItem) {
        for (Screen screen : wrapperScreens) {
            ((TeamPaneWrapper) screen.controller).getTeamPaneController().removeSegmentItem(segmentItem);
        }

    }

    public List<Worker> getWorkers() {
        List<SegmentItem> segmentItems = new ArrayList<>();
        for (Screen screen : wrapperScreens) {
            segmentItems.addAll(((TeamPaneWrapper) screen.controller).getTeamPaneController().getSegmentItems());
        }
        return ModelUtils.getWorkersFromSegmentItems(segmentItems);
    }

    private Screen addTeam(TeamType teamType) {

        Screen wrapperScreen = ViewUtils.loadScreenFromResource(ScreenCode.TEAM_PANE_WRAPPER, mainApp, gameController);
        TeamPaneWrapper wrapperController = ((TeamPaneWrapper) wrapperScreen.controller);

        if (teamType.equals(TeamType.INTERFERENCE)) {
            wrapperScreens.add(wrapperScreen);
            teamsPane.getChildren().add(wrapperScreen.pane);
        } else {
            int indexToInsert = wrapperScreens.isEmpty() ? 0 : wrapperScreens.size();

            for (int i = 0; i < wrapperScreens.size(); i++) {
                if (isInterference(wrapperScreens.get(i)) && i > 0) {
                    indexToInsert = i;
                    break;
                }
            }

            wrapperScreens.add(indexToInsert, wrapperScreen);
            teamsPane.getChildren().add(indexToInsert, wrapperScreen.pane);
        }

        if (teamType.equals(TeamType.DEFAULT)) {
            teamType = getTeamType(wrapperScreen);

        }

        wrapperController.setTeamType(teamType);
        wrapperController.setOutcomeType(getOutcomeType(wrapperScreen));

        wrapperScreen.pane.setOnDragDropped((final DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                Pane parent = (Pane) wrapperScreen.pane.getParent();
                Object source = event.getGestureSource();
                int sourceIndex = parent.getChildren().indexOf(source);
                int targetIndex = parent.getChildren().indexOf(wrapperScreen.pane);
                this.swapTeams(sourceIndex, targetIndex);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        TeamPaneController teamPaneController = ((TeamPaneWrapper) wrapperScreen.controller).getTeamPaneController();
        teamPaneController.setDragDropHandler(this, eventScreenController);
        teamPaneController.setTeamNumber(wrapperScreens.size() - 1);
        wrapperController.getXButton().setOnAction(e -> removeTeam(wrapperScreen));

        eventScreenController.updateLabels();

        updateLabels();

        return wrapperScreen;

    }

    private void setSegmentType(SegmentType type) {
        segmentType = type;
        segmentLengthWrapper.setItems(FXCollections.observableArrayList(type.equals(SegmentType.MATCH)
                ? MatchLength.values() : AngleLength.values()));
        segmentLength = (iSegmentLength) segmentLengthWrapper.getSelected();
        updateLabels();

    }

    private boolean getXButtonVisible(int index, TeamType teamType) {
        int minTeams = segmentType.equals(SegmentType.MATCH)
                ? 2
                : angleOptions.getAngleType().minWorkers();

        return index >= minTeams || teamType.equals(TeamType.INTERFERENCE);
    }

    private TeamType getTeamType(Screen wrapperScreen) {
        TeamPaneWrapper controller = ((TeamPaneWrapper) wrapperScreen.controller);
        if (controller.getTeamType().equals(TeamType.INTERFERENCE)) {
            return TeamType.INTERFERENCE;
        }

        int index = wrapperScreens.indexOf(wrapperScreen);
        TeamType teamType;

        if (segmentType.equals(SegmentType.ANGLE)) {
            teamType = index == 0 ? angleOptions.getAngleType().mainTeamType()
                    : angleOptions.getAngleType().addTeamType();
        } else if (matchOptions.getMatchFinish().equals(MatchFinish.DRAW)) {
            teamType = TeamType.DRAW;
        } else {
            teamType = index == 0
                    ? TeamType.WINNER : TeamType.LOSER;

        }

        return teamType;
    }

    private OutcomeType getOutcomeType(Screen teamPaneWrapper) {
        OutcomeType outcomeType = null;
        if (matchOptions.getMatchFinish() != null && matchOptions.getMatchFinish().equals(MatchFinish.DRAW)) {
            outcomeType = OutcomeType.DRAW;
        } else {
            switch (wrapperScreens.indexOf(teamPaneWrapper)) {
                case 0:
                    outcomeType = OutcomeType.WINNER;
                    break;
                default:
                    outcomeType = OutcomeType.LOSER;
                    break;
            }
        }
        return outcomeType;
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

        if (wrapperScreens.contains(teamPaneWrapper)) {
            wrapperScreens.remove(teamPaneWrapper);
        }
        eventScreenController.updateLabels();
        updateLabels();
    }

    @Override
    public void updateLabels() {

        for (Screen screen : wrapperScreens) {
            TeamPaneWrapper controller = (TeamPaneWrapper) screen.controller;
            controller.setTargets(getOtherTeams(wrapperScreens.indexOf(screen)));
            controller.setTeamType(getTeamType(screen));
            controller.getXButton().setVisible(
                    getXButtonVisible(wrapperScreens.indexOf(screen), controller.getTeamType()));
            screen.controller.updateLabels();

        }
        eventScreenController.updateLabels();

    }

    public void swapTeams(int indexA, int indexB) {
        List<SegmentItem> teamA = getTeamPaneController(indexA).getSegmentItems();
        List<SegmentItem> teamB = getTeamPaneController(indexB).getSegmentItems();
        getTeamPaneController(indexA).setSegmentItems(teamB);
        getTeamPaneController(indexB).setSegmentItems(teamA);

        eventScreenController.updateLabels();

        updateLabels();
    }

    public SegmentView getSegmentView() {
        //this would return whatever segment we generate, match or angle
        //along with all the rules etc
        SegmentView segmentView = new SegmentView(segmentType);
        if (segmentType.equals(SegmentType.MATCH)) {
            MatchParams params = new MatchParams();
            params.setMatchFinish(matchOptions.getMatchFinish());
            params.setMatchRule(matchOptions.getMatchRule());
            segmentView.getSegment().setSegmentParams(params);
        } else {
            AngleParams params = new AngleParams();
            params.setAngleType(angleOptions.getAngleType());
            segmentView.getSegment().setSegmentParams(params);
        }
        segmentView.getSegment().setSegmentLength(segmentLength.value());
        segmentView.setTeams(getTeams());
        return segmentView;
    }

    private TeamPaneController getTeamPaneController(int index) {
        return ((TeamPaneWrapper) wrapperScreens.get(index).controller).getTeamPaneController();
    }

    private List<SegmentTeam> getTeams() {

        List<SegmentTeam> teams = new ArrayList<>();

        for (Screen screen : wrapperScreens) {
            TeamPaneWrapper controller = (TeamPaneWrapper) screen.controller;
            teams.add(controller.getTeam());
        }

        return teams;
    }

    private List<SegmentTeam> getOtherTeams(int notThisIndex) {

        List<SegmentTeam> teams = new ArrayList<>();

        for (Screen screen : wrapperScreens) {
            SegmentTeam team = ((TeamPaneWrapper) screen.controller).getTeam();
            if (team != null && wrapperScreens.indexOf(screen) < notThisIndex) {
                teams.add(team);
            }
        }

        return teams;
    }
}
