package openwrestling.view.event.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import openwrestling.model.AngleParams;
import openwrestling.model.EventTemplate;
import openwrestling.model.MatchParams;
import openwrestling.model.SegmentItem;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.TagTeam;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.interfaces.iSegmentLength;
import openwrestling.model.modelView.SegmentTeam;
import openwrestling.model.modelView.SegmentView;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.segmentEnum.AngleLength;
import openwrestling.model.segmentEnum.AngleType;
import openwrestling.model.segmentEnum.JoinTeamType;
import openwrestling.model.segmentEnum.MatchFinish;
import openwrestling.model.segmentEnum.MatchLength;
import openwrestling.model.segmentEnum.OutcomeType;
import openwrestling.model.segmentEnum.ResponseType;
import openwrestling.model.segmentEnum.SegmentType;
import openwrestling.model.segmentEnum.SegmentValidation;
import openwrestling.model.segmentEnum.ShowType;
import openwrestling.model.segmentEnum.TeamType;
import openwrestling.model.utility.ModelUtils;
import openwrestling.model.utility.StaffUtils;
import openwrestling.view.event.helper.TeamPaneHelper;
import openwrestling.view.utility.ButtonWrapper;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.comparators.DateComparator;
import openwrestling.view.utility.interfaces.ControllerBase;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SegmentPaneController extends ControllerBase implements Initializable {

    private static final int DEFAULTTEAMS = 2;

    @FXML
    private VBox teamsVBox;

    @FXML
    private VBox ringsideVBox;

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
    private AngleOptionsController angleOptions;
    private GameScreen matchOptionsScreen;
    private MatchOptions matchOptions;
    private ButtonWrapper segmentLengthWrapper;
    private iSegmentLength segmentLength;

    private List<GameScreen> workerTeamWrappers;
    private List<TeamPaneWrapper> workerTeamControllers;
    private List<GameScreen> allWrapperScreens;
    private GameScreen titlesWrapper;
    private TeamPaneWrapper titlesController;
    private TeamPaneWrapper refsController;
    private GameScreen refScreen;
    private TeamPaneWrapper broadcastTeamController;

    private EventScreenController eventScreenController;

    private SegmentType segmentType;
    private SegmentView challengeSource;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this.getClass());
        workerTeamWrappers = new ArrayList<>();
        allWrapperScreens = new ArrayList<>();
        workerTeamControllers = new ArrayList<>();
        segmentTypeButtons = new ArrayList<>();
    }

    @Override
    public void initializeMore() {

        segmentTypeButtons.addAll(Arrays.asList(angleButton, matchButton));

        matchOptionsScreen = ViewUtils.loadScreenFromResource(ScreenCode.MATCH_OPTIONS, mainApp, gameController, optionsPane);
        matchOptions = (MatchOptions) matchOptionsScreen.controller;

        segmentType = SegmentType.MATCH;
        ViewUtils.updateSelectedButton(matchButton, segmentTypeButtons);

        segmentLength = MatchLength.SHORT;
        segmentLengthWrapper = new ButtonWrapper(FXCollections.observableArrayList(MatchLength.values()), 3);
        ViewUtils.anchorRegionToParent(segmentLengthAnchor, segmentLengthWrapper.getGridPane());
        for (Button button : segmentLengthWrapper.getButtons()) {
            button.setOnAction(e -> {
                segmentLength = ((iSegmentLength) segmentLengthWrapper.updateSelected(button));
                eventScreenController.updateLabels();
            });
        }
        segmentLengthWrapper.updateSelected(segmentLengthWrapper.getItems().indexOf(segmentLength));

        ringsideVBox.setSpacing(5);
        teamsVBox.setSpacing(5);

        initializeTitlesWrapper();
        initializeBroadcastTeam();
        initializeMatchOptions();
        initializeAngleOptions();
        initializeRef();

        addTeamButton.setOnAction(e -> addTeam(
                angleOptions.getAngleParams().getAngleType().addTeamType()
        ));
        interferenceButton.setOnAction(e -> addTeam(TeamType.INTERFERENCE));
    }

    private void initializeTitlesWrapper() {
        titlesWrapper = ViewUtils.loadScreenFromFXML(ScreenCode.TEAM_PANE_WRAPPER, mainApp, gameController);
        titlesController = ((TeamPaneWrapper) titlesWrapper.controller);
        titlesController.setTeamType(TeamType.TITLES);
        titlesController.setDragDroppedHandler(this);
        titlesController.setHeaderVisible(false);

        ringsideVBox.getChildren().add(titlesWrapper.pane);
        allWrapperScreens.add(titlesWrapper);
    }

    private void initializeRef() {
        refScreen = ViewUtils.loadScreenFromFXML(ScreenCode.TEAM_PANE_WRAPPER, mainApp, gameController);
        refsController = ((TeamPaneWrapper) refScreen.controller);
        refsController.setTeamType(TeamType.REF);
        refsController.setDragDroppedHandler(this);
        refsController.setHeaderVisible(false);
        ringsideVBox.getChildren().add(refScreen.pane);

        allWrapperScreens.add(refScreen);
    }

    private void initializeBroadcastTeam() {
        GameScreen broadcastTeamScreen = ViewUtils.loadScreenFromFXML(ScreenCode.TEAM_PANE_WRAPPER, mainApp, gameController);
        broadcastTeamController = ((TeamPaneWrapper) broadcastTeamScreen.controller);
        broadcastTeamController.setTeamType(TeamType.BROADCAST);
        broadcastTeamController.setDragDroppedHandler(this);
        broadcastTeamController.setHeaderVisible(false);

        ringsideVBox.getChildren().add(broadcastTeamScreen.pane);
        allWrapperScreens.add(broadcastTeamScreen);
    }

    private void initializeMatchOptions() {
        matchOptions.getMatchFinishes().setOnAction(e -> eventScreenController.updateLabels());
        matchOptions.getMatchRules().setOnAction(e -> eventScreenController.updateLabels());
    }

    private void initializeAngleOptions() {
        angleOptionsScreen = ViewUtils.loadScreenFromFXML(ScreenCode.ANGLE_OPTIONS, mainApp, gameController);
        angleOptions = (AngleOptionsController) angleOptionsScreen.controller;

        angleOptions.setAngleTypeListener(new ChangeListener<AngleType>() {
            @Override
            public void changed(ObservableValue ov, AngleType oldType, AngleType newType) {
                if (newType != null) {
                    titlesWrapper.pane.setVisible(newType.equals(AngleType.CHALLENGE));
                    eventScreenController.updateLabels();
                }
            }
        });

        List<Object> challengeOptions = new ArrayList<>();
        challengeOptions.add(ShowType.TONIGHT);
        List<EventTemplate> futureEvents = new ArrayList<>(playerPromotion().getEventTemplates());
        Collections.sort(futureEvents, new DateComparator());
        challengeOptions.addAll(futureEvents);
        angleOptions.setChallengeOptions(challengeOptions);

        angleOptions.getCombo1().valueProperty().addListener((ObservableValue ov, Object t, Object t1) -> {
            angleOptionChanged(t1);
        });

        angleOptions.setChallengeButtonAction(e -> {
            eventScreenController.addSegment(ModelUtils.getSegmentFromTeams(getSegmentTeamsForChallenge()));
        });

        for (int i = 0; i < DEFAULTTEAMS; i++) {

            addTeam(TeamType.DEFAULT);

        }
    }

    public void setEventScreenController(EventScreenController eventScreenController) {
        this.eventScreenController = eventScreenController;

    }

    private void angleOptionChanged(Object obj) {
        eventScreenController.updateLabels();
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
        removeSegmentItem(segmentItem, TeamType.DEFAULT, TeamType.DEFAULT);
    }

    public void removeSegmentItem(SegmentItem segmentItem, TeamType sourceType, TeamType targetType) {
        for (GameScreen screen : allWrapperScreens) {
            ((TeamPaneWrapper) screen.controller).removeSegmentItem(segmentItem, sourceType, targetType);
        }
    }

    public List<Worker> getWorkers() {
        return ModelUtils.getWorkersFromSegmentItems(getSegmentItems());
    }

    public List<SegmentItem> getSegmentItems() {
        List<SegmentItem> segmentItems = new ArrayList<>();
        for (GameScreen screen : allWrapperScreens) {
            segmentItems.addAll(((TeamPaneWrapper) screen.controller).getSegmentItems());
        }
        return segmentItems;
    }

    public void addTeam(List<? extends SegmentItem> segmentItems, boolean emptyOnly) {
        if (addTeam(segmentItems)) {
            return;
        }
        segmentItems.forEach(item -> removeSegmentItem(item));

        SegmentItem item = segmentItems.get(0);

        if (item instanceof Worker || item instanceof TagTeam) {

            GameScreen wrapperToInsert = wrapperToInsert(workerTeamWrappers, emptyOnly);
            if (wrapperToInsert == null) {
                GameScreen newTeam = addTeam(TeamType.DEFAULT);
                ((TeamPaneWrapper) newTeam.controller).addSegmentItems(segmentItems);
                newTeam.controller.updateLabels();
            } else {
                TeamPaneWrapper wrapperController = (TeamPaneWrapper) wrapperToInsert.controller;
                wrapperController.addSegmentItems(segmentItems);
                wrapperController.updateLabels();
            }
        }
        updateLabels();

    }

    public void addTeam(List<? extends SegmentItem> segmentItems, int index) {
        if (addTeam(segmentItems)) {
            return;
        }

        TeamPaneWrapper emptyWrapper = (TeamPaneWrapper) workerTeamWrappers.get(index).controller;
        emptyWrapper.addSegmentItems(segmentItems);
        emptyWrapper.updateLabels();
    }

    private boolean addTeam(List<? extends SegmentItem> segmentItems) {

        if (segmentItems.isEmpty() || ModelUtils.teamIsPresent(segmentItems, workerTeamWrappers)) {
            return true;
        }

        SegmentItem item = segmentItems.get(0);

        if (item instanceof Title) {
            addTitleView((Title) segmentItems.get(0));
            return true;
        }

        if (StaffUtils.isRef(item)) {
            setRef((StaffMember) item);
            return true;
        }

        return false;
    }

    public void addTitleView(Title title) {
        titlesController.addSegmentItem(title);
        titlesController.updateLabels();
        addTeam(title.getChampions(), true);
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

        GameScreen wrapperScreen = ViewUtils.loadScreenFromFXML(ScreenCode.TEAM_PANE_WRAPPER, mainApp, gameController);
        TeamPaneWrapper wrapperController = ((TeamPaneWrapper) wrapperScreen.controller);

        if (teamType.equals(TeamType.INTERFERENCE)) {
            workerTeamWrappers.add(wrapperScreen);
            workerTeamControllers.add(wrapperController);
            teamsVBox.getChildren().add(wrapperScreen.pane);
        } else {
            int indexToInsert = workerTeamWrappers.isEmpty() ? 0 : workerTeamWrappers.size();

            for (int i = 0; i < workerTeamWrappers.size(); i++) {
                if (isInterference(workerTeamWrappers.get(i)) && i > 0) {
                    indexToInsert = i;
                    break;
                }
            }

            workerTeamWrappers.add(indexToInsert, wrapperScreen);
            workerTeamControllers.add(indexToInsert, wrapperController);
            teamsVBox.getChildren().add(indexToInsert, wrapperScreen.pane);
        }

        if (teamType.equals(TeamType.DEFAULT)) {
            teamType = getTeamType(wrapperScreen);
        }

        wrapperController.setTeamType(teamType);
        wrapperController.setOutcomeType(getOutcomeType(wrapperScreen));

        TeamPaneHelper.initTeamPaneForSorting(wrapperScreen.pane, wrapperController.getDraggingTab(), this);

        wrapperController.setDragDroppedHandler(this);
        wrapperController.setTeamNumber(workerTeamWrappers.size() - 1);
        wrapperController.setOnXButton(e -> removeTeam(wrapperScreen));

        eventScreenController.updateLabels();

        updateLabels();

        allWrapperScreens.add(wrapperScreen);

        return wrapperScreen;
    }

    private void setSegmentType(SegmentType type) {
        segmentType = type;
        segmentLengthWrapper.setItems(FXCollections.observableArrayList(SegmentType.MATCH.equals(type)
                ? MatchLength.values() : AngleLength.values()));
        segmentLength = (iSegmentLength) segmentLengthWrapper.getSelected();
        refScreen.pane.setVisible(SegmentType.MATCH.equals(type));
        titlesWrapper.pane.setVisible(SegmentType.MATCH.equals(type) || AngleType.CHALLENGE.equals(angleOptions.getAngleParams().getAngleType()));
        eventScreenController.segmentsChanged();
        eventScreenController.updateLabels();
    }

    private boolean getXButtonVisible(int index, TeamType teamType) {
        int minTeams = getSegmentType().equals(SegmentType.MATCH)
                ? 2
                : angleOptions.getAngleParams().getAngleType().minWorkers();

        return index >= minTeams || teamType.equals(TeamType.INTERFERENCE);
    }

    private TeamType getTeamType(GameScreen wrapperScreen) {
        TeamPaneWrapper controller = ((TeamPaneWrapper) wrapperScreen.controller);
        if (controller.getTeamType().equals(TeamType.INTERFERENCE)) {
            return TeamType.INTERFERENCE;
        }

        int index = workerTeamWrappers.indexOf(wrapperScreen);
        TeamType teamType;

        if (getSegmentType().equals(SegmentType.ANGLE)) {
            teamType = index == 0 ? angleOptions.getAngleParams().getAngleType().mainTeamType()
                    : angleOptions.getAngleParams().getAngleType().addTeamType();
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

        if (teamsVBox.getChildren().contains(teamPaneWrapper.pane)) {
            teamsVBox.getChildren().remove(teamPaneWrapper.pane);
        }

        int index = workerTeamWrappers.indexOf(teamPaneWrapper);

        if (index > -1) {
            workerTeamWrappers.remove(index);
            workerTeamControllers.remove(index);
            allWrapperScreens.remove(teamPaneWrapper);
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
            controller.setXButtonVisible(getXButtonVisible(workerTeamWrappers.indexOf(screen), controller.getTeamType()));
            screen.controller.updateLabels();
        }

        if (SegmentType.ANGLE.equals(segmentType) && AngleType.OFFER.equals(angleOptions.getAngleParams().getAngleType())) {
            updateOffers();
        }

        if (SegmentType.ANGLE.equals(segmentType) && AngleType.CHALLENGE.equals(angleOptions.getAngleParams().getAngleType())) {
            SegmentView segmentView = getSegmentView();
            if (SegmentValidation.COMPLETE.equals(segmentView.getValidationStatus())) {
                SegmentView challengeMatch = ModelUtils.getSegmentFromTeams(getSegmentTeamsForChallenge());
                boolean isPresent = eventScreenController.challengeForTonightIsPresent(challengeMatch, this);
                angleOptions.setChallengeIsPresent(isPresent);
                angleOptions.setChallengeIsComplete(challengeMatch.getMatchParticipantTeams().size() > 1);
            } else {
                angleOptions.setChallengeIsPresent(true);
            }
        }
        angleOptions.updateLabels();
    }

    public void swapTeams(int indexA, int indexB) {
        List<SegmentItem> teamA = workerTeamControllers.get(indexA).getSegmentItems();
        List<SegmentItem> teamB = workerTeamControllers.get(indexB).getSegmentItems();
        workerTeamControllers.get(indexA).setSegmentItems(teamB);
        workerTeamControllers.get(indexB).setSegmentItems(teamA);

        eventScreenController.updateLabels();

        updateLabels();
    }

    public void itemDroppedInSegment() {
        eventScreenController.updateLabels();
    }

    public SegmentView getSegmentView() {
        SegmentView segmentView = new SegmentView(segmentType);
        segmentView.getSegment().setSegmentLength(segmentLength.value());
        segmentView.setTeams(getSegmentTeams());
        segmentView.addTitles(getTitles());
        segmentView.setReferee(getRef());
        segmentView.setBroadcastTeam(broadcastTeamController.getSegmentItems());
        if (segmentType.equals(SegmentType.MATCH)) {
            MatchParams params = new MatchParams();
            params.setMatchFinish(matchOptions.getMatchFinish());
            params.setMatchRule(matchOptions.getMatchRule());
            segmentView.getSegment().setSegmentParams(params);
        } else {
            AngleParams angleParams = angleOptions.getAngleParams();

            if (angleParams.getAngleType().equals(AngleType.CHALLENGE)) {
                angleParams.getChallengeSegment().getSegmentTeams().addAll(getSegmentTeamsForChallenge());
                angleParams.getChallengeSegment().getTitleViews().addAll(getTitles());
                angleParams.getChallengeSegment().setSourceEvent(eventScreenController.getCurrentEvent());
            }
            segmentView.getSegment().setSegmentParams(angleParams);
        }

        return segmentView;
    }

    private List<SegmentTeam> getSegmentTeamsForChallenge() {
        return getSegmentTeams().stream().filter(segmentTeam -> segmentTeam.getResponse().equals(ResponseType.YES)).collect(Collectors.toList());
    }

    private List<SegmentTeam> getSegmentTeams() {
        List<SegmentTeam> segmentTeams = new ArrayList<>();

        for (GameScreen screen : workerTeamWrappers) {
            TeamPaneWrapper controller = (TeamPaneWrapper) screen.controller;
            segmentTeams.add(controller.getSegmentTeam());
        }

        return segmentTeams;
    }

    private List<Title> getTitles() {
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

    /**
     * @return the segmentType
     */
    public SegmentType getSegmentType() {
        return segmentType;
    }

    private StaffMember getRef() {
        return refsController != null && !refsController.getSegmentItems().isEmpty()
                ? (StaffMember) refsController.getSegmentItems().get(0)
                : null;
    }

    public boolean isAutoSetRef() {
        return refsController.isAutoSet();
    }

    /**
     * @param ref the ref to set
     */
    public void setRefAuto(StaffMember ref) {
        refsController.setSegmentItems(Collections.singletonList(ref));
    }

    /**
     * @param ref the ref to set
     */
    public void setRef(StaffMember ref) {
        refsController.setAutoSet(false);
        refsController.setSegmentItems(Collections.singletonList(ref));
        eventScreenController.autoUpdateRefs();
    }

    public void clearRef() {
        refsController.setSegmentItems(Collections.emptyList());
        refsController.setAutoSet(true);
    }

    public void setBroadcastTeam(List<? extends SegmentItem> broadcastTeam) {
        broadcastTeamController.setSegmentItems(broadcastTeam);
    }

    private void updateOffers() {
        List<Object> offers = new ArrayList<>();

        TeamPaneWrapper offerer = workerTeamControllers.stream()
                .filter(controller -> TeamType.OFFERER.equals(controller.getTeamType()))
                .findFirst().orElse(null);

        List<TeamPaneWrapper> offerees = workerTeamControllers.stream()
                .filter(controller -> TeamType.OFFEREE.equals(controller.getTeamType())).collect(Collectors.toList());

        if (offerer != null) {
            if (offerer.getSegmentItems().size() <= 1
                    && (offerees.isEmpty() || offerees.size() == 1 && offerees.get(0).getSegmentItems().size() <= 1)) {
                List<SegmentItem> potentialTeam = new ArrayList<>();
                potentialTeam.addAll(offerer.getSegmentItems());
                potentialTeam.addAll(offerees.get(0).getSegmentItems());
                if (potentialTeam.size() != 2 || StringUtils.isEmpty(gameController.getSegmentManager().getTagTeamName(potentialTeam))) {
                    offers.add(JoinTeamType.TAG_TEAM);
                }
            }

            offers.add(JoinTeamType.NEW_STABLE);

            if (!offerer.getSegmentItems().isEmpty()) {
                for (Stable stable : gameController.getStableManager().getStables()) {
                    if (stable.getWorkers().containsAll(offerer.getSegmentItems())
                            && !offerees.stream()
                            .filter(offeree -> !offeree.getSegmentItems().isEmpty() && stable.getWorkers().containsAll(offeree.getSegmentItems()))
                            .findAny().isPresent()) {
                        offers.add(stable);
                    }
                }
            }
            angleOptions.setOffers(offers);
        }
    }

    /**
     * @return the challengeSource
     */
    public SegmentView getChallengeSource() {
        return challengeSource;
    }

    /**
     * @param challengeSource the challengeSource to set
     */
    public void setChallengeSource(SegmentView challengeSource) {
        this.challengeSource = challengeSource;
    }
}
