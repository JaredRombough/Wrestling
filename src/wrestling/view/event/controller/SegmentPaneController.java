package wrestling.view.event.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import wrestling.model.modelView.TagTeamView;
import wrestling.model.modelView.TitleView;
import wrestling.model.segmentEnum.AngleLength;
import wrestling.model.segmentEnum.AngleType;
import wrestling.model.segmentEnum.MatchFinish;
import wrestling.model.segmentEnum.MatchLength;
import wrestling.model.segmentEnum.OutcomeType;
import wrestling.model.segmentEnum.SegmentType;
import wrestling.model.segmentEnum.TeamType;
import wrestling.model.utility.ModelUtils;
import wrestling.view.browser.controller.TitleViewController;
import wrestling.view.utility.ButtonWrapper;
import wrestling.view.utility.GameScreen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class SegmentPaneController extends ControllerBase implements Initializable {

    private static final int DEFAULTTEAMS = 2;

    @FXML
    private VBox teamsPane;

    @FXML
    private VBox titlesPane;

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

    private GameScreen angleOptionsScreen;
    private AngleOptions angleOptions;
    private GameScreen matchOptionsScreen;
    private MatchOptions matchOptions;
    private ButtonWrapper segmentLengthWrapper;
    private iSegmentLength segmentLength;

    private List<GameScreen> workerTeamWrappers;
    private List<GameScreen> allWrappers;
    private GameScreen titlesWrapper;
    private TeamPaneWrapper titlesController;

    private EventScreenController eventScreenController;

    private SegmentType segmentType;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this.getClass());
        workerTeamWrappers = new ArrayList<>();
        allWrappers = new ArrayList<>();
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

        initializeTitlesWrapper();
        initializeMatchOptions();
        initializeAngleOptions();

        addTeamButton.setOnAction(e -> addTeam(
                angleOptions.getAngleType().addTeamType()
        ));
        interferenceButton.setOnAction(e -> addTeam(TeamType.INTERFERENCE));
    }

    private void initializeTitlesWrapper() {
        titlesWrapper = ViewUtils.loadScreenFromResource(ScreenCode.TEAM_PANE_WRAPPER, mainApp, gameController);
        titlesController = ((TeamPaneWrapper) titlesWrapper.controller);
        titlesController.setTeamType(TeamType.TITLES);
        titlesController.setDragDropHandler(this, eventScreenController);
        titlesController.setVisible(false);

        titlesPane.getChildren().add(titlesWrapper.pane);
        allWrappers.add(titlesWrapper);
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
            for (GameScreen screen : workerTeamWrappers) {
                ((TeamPaneWrapper) screen.controller).changeSegmentType();
                setOptionsPane(matchOptionsScreen.pane);
            }
            setSegmentType(SegmentType.MATCH);
        } else if (button == angleButton) {
            for (GameScreen screen : workerTeamWrappers) {
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

    public void removeSegmentItem(SegmentItem segmentItem) {
        for (GameScreen screen : allWrappers) {
            ((TeamPaneWrapper) screen.controller).getTeamPaneController().removeSegmentItem(segmentItem);
        }
    }

    public void removeSegmentItems(List<? extends SegmentItem> segmentItems) {
        for (SegmentItem segmentItem : segmentItems) {
            removeSegmentItem(segmentItem);
        }
    }

    public List<Worker> getWorkers() {
        return ModelUtils.getWorkersFromSegmentItems(getSegmentItems());
    }

    public List<SegmentItem> getSegmentItems() {
        List<SegmentItem> segmentItems = new ArrayList<>();
        for (GameScreen screen : allWrappers) {
            segmentItems.addAll(((TeamPaneWrapper) screen.controller).getTeamPaneController().getSegmentItems());
        }
        return segmentItems;
    }

    public void addTeam(List<? extends SegmentItem> segmentItems, boolean emptyOnly) {

        if (segmentItems.isEmpty() || ModelUtils.teamIsPresent(segmentItems, workerTeamWrappers)) {
            return;
        }

        if (segmentItems.get(0) instanceof TitleView) {
            addTitleView((TitleView) segmentItems.get(0));
            return;
        }

        removeSegmentItems(segmentItems);

        if (segmentItems.get(0) instanceof Worker || segmentItems.get(0) instanceof TagTeamView) {

            GameScreen wrapperToInsert = wrapperToInsert(workerTeamWrappers, emptyOnly);
            if (wrapperToInsert == null) {
                GameScreen newTeam = addTeam(TeamType.DEFAULT);
                ((TeamPaneWrapper) newTeam.controller).getTeamPaneController().getItems().addAll(segmentItems);
                newTeam.controller.updateLabels();
            } else {
                TeamPaneWrapper wrapperController = (TeamPaneWrapper) wrapperToInsert.controller;
                wrapperController.getTeamPaneController().getItems().addAll(segmentItems);
                wrapperController.updateLabels();
            }
        }
        updateLabels();

    }

    public void addTitleView(TitleView titleView) {
        titlesController.getTeamPaneController().getItems().add(titleView);
        titlesController.updateLabels();
        addTeam(titleView.getChampions(), true);
    }

    public void addTeam(List<? extends SegmentItem> segmentItems, int index) {
        if (segmentItems.isEmpty() || ModelUtils.teamIsPresent(segmentItems, workerTeamWrappers)) {
            return;
        }

        if (segmentItems.get(0) instanceof TitleView) {
            addTitleView((TitleView) segmentItems.get(0));
            return;
        }
        TeamPaneWrapper emptyWrapper = (TeamPaneWrapper) workerTeamWrappers.get(index).controller;
        emptyWrapper.getTeamPaneController().getItems().addAll(segmentItems);
        emptyWrapper.updateLabels();
    }

    private GameScreen wrapperToInsert(List<GameScreen> workerTeamWrappers, boolean onlyEmpty) {
        GameScreen biggestTeamScreen = Collections.max(workerTeamWrappers,
                Comparator.comparing(c -> ((TeamPaneWrapper) c.controller).getSegmentItems().size()));
        int maxSize = ((TeamPaneWrapper) biggestTeamScreen.controller).getSegmentItems().size();
        if (maxSize == 0 && !workerTeamWrappers.isEmpty()) {
            return workerTeamWrappers.get(0);
        }

        if (onlyEmpty) {
            GameScreen smallest = Collections.min(workerTeamWrappers,
                    Comparator.comparing(c -> ((TeamPaneWrapper) c.controller).getSegmentItems().size()));
            int minSize = ((TeamPaneWrapper) smallest.controller).getSegmentItems().size();
            if (minSize == 0) {
                return smallest;
            }
            return null;
        }
        return workerTeamWrappers.stream()
                .filter(x -> ((TeamPaneWrapper) x.controller).getSegmentItems().size() < maxSize)
                .findFirst()
                .orElse(null);
    }

    private GameScreen addTeam(TeamType teamType) {

        GameScreen wrapperScreen = ViewUtils.loadScreenFromResource(ScreenCode.TEAM_PANE_WRAPPER, mainApp, gameController);
        TeamPaneWrapper wrapperController = ((TeamPaneWrapper) wrapperScreen.controller);

        if (teamType.equals(TeamType.INTERFERENCE)) {
            workerTeamWrappers.add(wrapperScreen);
            teamsPane.getChildren().add(wrapperScreen.pane);
        } else {
            int indexToInsert = workerTeamWrappers.isEmpty() ? 0 : workerTeamWrappers.size();

            for (int i = 0; i < workerTeamWrappers.size(); i++) {
                if (isInterference(workerTeamWrappers.get(i)) && i > 0) {
                    indexToInsert = i;
                    break;
                }
            }

            workerTeamWrappers.add(indexToInsert, wrapperScreen);
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
        teamPaneController.setTeamNumber(workerTeamWrappers.size() - 1);
        wrapperController.getXButton().setOnAction(e -> removeTeam(wrapperScreen));

        eventScreenController.updateLabels();

        updateLabels();

        allWrappers.add(wrapperScreen);

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

    private TeamType getTeamType(GameScreen wrapperScreen) {
        TeamPaneWrapper controller = ((TeamPaneWrapper) wrapperScreen.controller);
        if (controller.getTeamType().equals(TeamType.INTERFERENCE)) {
            return TeamType.INTERFERENCE;
        }

        int index = workerTeamWrappers.indexOf(wrapperScreen);
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

    private OutcomeType getOutcomeType(GameScreen teamPaneWrapper) {
        OutcomeType outcomeType = null;
        if (matchOptions.getMatchFinish() != null && matchOptions.getMatchFinish().equals(MatchFinish.DRAW)) {
            outcomeType = OutcomeType.DRAW;
        } else {
            switch (workerTeamWrappers.indexOf(teamPaneWrapper)) {
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

    private boolean isInterference(GameScreen screen) {
        return screen.controller instanceof TeamPaneWrapper
                && ((TeamPaneWrapper) screen.controller).getTeamType() != null
                && ((TeamPaneWrapper) screen.controller).getTeamType().equals(TeamType.INTERFERENCE);
    }

    private void removeTeam(GameScreen teamPaneWrapper) {

        if (teamsPane.getChildren().contains(teamPaneWrapper.pane)) {
            teamsPane.getChildren().remove(teamPaneWrapper.pane);
        }

        if (workerTeamWrappers.contains(teamPaneWrapper)) {
            workerTeamWrappers.remove(teamPaneWrapper);
            allWrappers.remove(teamPaneWrapper);
        }
        eventScreenController.updateLabels();
        updateLabels();
    }

    @Override
    public void updateLabels() {

        for (GameScreen screen : workerTeamWrappers) {
            TeamPaneWrapper controller = (TeamPaneWrapper) screen.controller;
            controller.setTargets(getOtherTeams(workerTeamWrappers.indexOf(screen)));
            controller.setTeamType(getTeamType(screen));
            controller.getXButton().setVisible(
                    getXButtonVisible(workerTeamWrappers.indexOf(screen), controller.getTeamType()));
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
        segmentView.setTeams(getSegmentTeams());
        segmentView.setTitleViews(getTitles());
        return segmentView;
    }

    private TeamPaneController getTeamPaneController(int index) {
        return ((TeamPaneWrapper) workerTeamWrappers.get(index).controller).getTeamPaneController();
    }

    private List<SegmentTeam> getSegmentTeams() {

        List<SegmentTeam> segmentTeams = new ArrayList<>();

        for (GameScreen screen : workerTeamWrappers) {
            TeamPaneWrapper controller = (TeamPaneWrapper) screen.controller;
            segmentTeams.add(controller.getSegmentTeam());
        }

        return segmentTeams;
    }

    private List<TitleView> getTitles() {
        return ModelUtils.getTitleViewsFromSegmentItems(((TeamPaneWrapper) titlesWrapper.controller).getSegmentItems());
    }

    private List<SegmentTeam> getOtherTeams(int notThisIndex) {

        List<SegmentTeam> teams = new ArrayList<>();

        for (GameScreen screen : workerTeamWrappers) {
            SegmentTeam team = ((TeamPaneWrapper) screen.controller).getSegmentTeam();
            if (team != null && workerTeamWrappers.indexOf(screen) < notThisIndex) {
                teams.add(team);
            }
        }

        return teams;
    }

    /**
     * @return the workerTeamWrappers
     */
    public List<GameScreen> getWorkerTeamWrappers() {
        return workerTeamWrappers;
    }
}
