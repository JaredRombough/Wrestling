package openwrestling.view.event.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import openwrestling.MainApp;
import openwrestling.model.SegmentItem;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.Segment;
import openwrestling.model.gameObjects.SegmentTeam;
import openwrestling.model.gameObjects.SegmentTemplate;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segment.constants.SegmentType;
import openwrestling.model.segment.constants.SegmentValidation;
import openwrestling.model.segment.constants.StaffType;
import openwrestling.model.segment.constants.browse.mode.BrowseMode;
import openwrestling.model.segment.constants.browse.mode.GameObjectQueryHelper;
import openwrestling.model.utility.ModelUtils;
import openwrestling.model.utility.SegmentUtils;
import openwrestling.model.utility.TestUtils;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.LocalDragboard;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.SortControl;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static openwrestling.model.utility.SegmentUtils.getMatchObjectors;

public class EventScreenController extends ControllerBase implements Initializable {

    private final List<Pane> segmentPanes = new ArrayList<>();
    private final List<SegmentPaneController> segmentPaneControllers = new ArrayList<>();
    private int defaultSegments;
    @FXML
    private Button runEventButton;
    @FXML
    private Button addSegmentButton;
    @FXML
    private ListView<SegmentNameItem> segmentListView;
    @FXML
    private Label totalCostLabel;
    @FXML
    private ListView<SegmentItem> segmentItemListView;
    @FXML
    private AnchorPane segmentPaneHolder;
    @FXML
    private AnchorPane workerInfoPane;
    @FXML
    private AnchorPane sortControlPane;
    @FXML
    private Label remainingTimeLabel;
    private SortControl sortControl;

    private Event currentEvent;

    private int eventLength;

    private BrowseMode browseMode;
    private GameObjectQueryHelper queryHelper;

    @FXML
    private ComboBox<BrowseMode> bookingBrowseComboBox;
    private boolean updatingChallenge = false;

    private WorkerInfoController workerInfoController;

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof Event) {
            if (!Objects.equals(currentEvent, obj)) {
                currentEvent = (Event) obj;
                if (currentEvent.getEventTemplate().getRosterSplit() != null) {
                    sortControl.setFilter(currentEvent.getEventTemplate().getRosterSplit());
                } else {
                    sortControl.clearFilters();
                }
                resetSegments();
            }
            updateLabels();
        } else {
            logger.log(Level.ERROR, "Invalid object passed to EventScreen");
        }
    }

    private Segment currentSegment() {
        if (segmentListView == null || segmentListView.getSelectionModel().getSelectedIndex() < 0) {
            return null;
        }
        return segmentPaneControllers.get(segmentListView.getSelectionModel().getSelectedIndex()).getSegment();
    }

    private List<Segment> getSegments() {
        List<Segment> segments = new ArrayList<>();
        eventLength = 0;
        for (SegmentPaneController controller : segmentPaneControllers) {
            Segment segment = controller.getSegment();
            segments.add(segment);
            eventLength += segment.getSegmentLength();
        }
        return segments;
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        if (event.getSource() == runEventButton) {
            handleRunEvent();
        } else if (event.getSource() == addSegmentButton) {
            addSegment();
        }
    }

    private void handleRunEvent() {
        if (handleErrors() && handleWarnings() && handleWorkers()) {
            showResults();
        }
    }

    private boolean handleWorkers() {
        StringBuilder objectionString = new StringBuilder();

        getSegments().stream()
                .filter(segment -> SegmentType.MATCH.equals(segment.getSegmentType()))
                .forEach(segment -> {
                    List<Worker> objectors = getMatchObjectors(segment);
                    if (!objectors.isEmpty()) {
                        objectionString.append(String.format("%s %s not happy about losing their match against %s.",
                                ModelUtils.andItemsLongName(objectors),
                                objectors.size() > 1 ? "are" : "is",
                                ModelUtils.andItemsLongName(segment.getWinner().getWorkers())));
                        objectionString.append("\n\n");
                    }
                });

        return objectionString.length() == 0 || ViewUtils.generateConfirmationDialogue(
                "Run the event anyway?",
                objectionString.toString());

    }

    private boolean handleWarnings() {
        String warnings = getWarnings();
        return getWarnings().isEmpty() || ViewUtils.generateConfirmationDialogue(
                "Consider the following...",
                warnings + "\nRun the event anyway?");
    }

    private boolean handleErrors() {
        String errors = getErrors();
        if (!errors.isEmpty()) {
            ViewUtils.generateAlert(
                    "Error",
                    "Event is not valid.",
                    errors + "\n" + getWarnings(),
                    AlertType.ERROR)
                    .showAndWait();
            return false;
        }
        return true;
    }

    private String getErrors() {
        StringBuilder errors = new StringBuilder();
        List<Segment> segments = getSegments();
        if (!validateDuration()) {
            errors.append("Event duration is invalid.\n");
        }
        for (int i = 0; i < segments.size(); i++) {
            SegmentValidation validation = segments.get(i).getValidationStatus();
            if (validation.equals(SegmentValidation.EMPTY)) {
                errors.append(String.format("Segment #%d is empty.\n", i + 1));
            } else if (validation.equals(SegmentValidation.INCOMPLETE)) {
                errors.append(String.format("Segment #%d has an empty team.\n", i + 1));
            }

        }

        return errors.toString();
    }

    private String getWarnings() {
        StringBuilder warnings = new StringBuilder();
        for (int i = 0; i < getSegments().size(); i++) {
            Segment segment = getSegments().get(i);
            if (segment.getSegmentType().equals(SegmentType.MATCH) && segment.getReferee() == null) {
                warnings.append(String.format("Segment #%d has no referee.\n", i + 1));
            }
            if (SegmentUtils.isChallengeForTonight(segment)) {
                Segment challengeMatch = ModelUtils.getSegmentFromTemplate(segment.getChallengeSegment());
                if (!challengeForTonightIsPresent(challengeMatch, i)) {
                    warnings.append(String.format("%s\nA challenge for this match was made and accepted tonight, but it is not present.\n",
                            gameController.getSegmentStringService().getVsMatchString(challengeMatch)));
                }
            }
            for (Title title : segment.getTitles()) {
                if (!title.getChampions().isEmpty()
                        && !ModelUtils.teamIsPresent(title.getChampions(),
                        segmentPaneControllers.get(i).getWorkerTeamWrappers())) {
                    warnings.append(String.format("The %s Title is not being defended by %s.\n",
                            title.getShortName(),
                            ModelUtils.slashNames(title.getChampions())));
                }

            }
        }
        for (SegmentTemplate segmentTemplate : gameController.getSegmentManager().getSegmentTemplates(currentEvent.getEventTemplate())) {
            Segment challengeMatch = ModelUtils.getSegmentFromTemplate(segmentTemplate);
            if (!challengeForTonightIsPresent(challengeMatch, 0)) {
                warnings.append(String.format("%s\nA challenge for this match was made and accepted on %s at %s, but it is not present.\n",
                        gameController.getSegmentStringService().getVsMatchString(challengeMatch),
                        segmentTemplate.getSourceEventDate().toString(),
                        segmentTemplate.getSourceEventName()));
            }
        }
        return warnings.toString();
    }

    private void showResults() {
        mainApp.setRootLayoutButtonDisable(true);
        boolean testing = false;
        if (testing) {
            mainApp.show(ScreenCode.RESULTS, TestUtils.testEventView(getCurrentEvent(), gameController.getWorkerManager().getRoster(playerPromotion()), mainApp.isRandomGame()));
        } else {
            currentEvent.setSegments(removeEmpty(getSegments()));
            mainApp.show(ScreenCode.RESULTS, currentEvent);
        }

    }

    private List<Segment> removeEmpty(List<Segment> list) {
        return list.stream().filter(segment -> !segment.getWorkers().isEmpty()).collect(Collectors.toList());
    }

    private void resetSegments() {
        segmentListView.getSelectionModel().clearSelection();

        segmentListView.getItems().clear();
        segmentPanes.clear();
        segmentPaneControllers.clear();

        gameController.getSegmentManager().getSegmentTemplates(currentEvent.getEventTemplate())
                .forEach(segmentTemplate ->
                        addSegment(ModelUtils.getSegmentFromTeams(segmentTemplate.getSegmentTeams()))
                );
        gameController.getSegmentManager().deleteSegmentTemplates(currentEvent.getEventTemplate());


        for (int i = 0; i < defaultSegments; i++) {
            addSegment();
        }

        segmentListView.getSelectionModel().selectFirst();
    }


    @Override
    public void updateLabels() {

        if (currentSegmentPaneController() != null) {
            currentSegmentPaneController().updateLabels();
        }

        totalCostLabel.setText("Total Cost:\t$" + currentCost());
        totalCostLabel.setVisible(currentCost() != 0);

        List<Segment> segments = getSegments();

        for (SegmentNameItem segmentNameItem : segmentListView.getItems()) {
            segmentNameItem.segment.set(segments.get(segmentListView.getItems().indexOf(segmentNameItem)));
        }

        if (getCurrentEvent() != null) {

            int duration = getDuration();

            int remainingHours = (duration - eventLength) / 60;
            int remainingMinutes = (duration - eventLength) % 60;

            if (remainingHours < 0 || remainingMinutes < 0) {
                remainingTimeLabel.setText("Remaining:\t-"
                        + ModelUtils.timeString(Math.abs(duration - eventLength)));
            } else {
                remainingTimeLabel.setText("Remaining:\t"
                        + ModelUtils.timeString(Math.abs(duration - eventLength)));
            }

        }

        updateSegmentItemListView();
    }

    private int getDuration() {
        return getCurrentEvent().getDefaultDuration();
    }

    private boolean validateDuration() {
        return Math.abs(getDuration() - eventLength) <= eventLength / 10;
    }

    private int currentCost() {

        int currentCost = 0;

        List<Worker> allWorkers = allWorkers();

        for (Worker worker : allWorkers) {
            currentCost += gameController.getContractManager().getActiveContract(worker, playerPromotion()).getAppearanceCost();

        }
        return currentCost;
    }

    private List<Worker> allWorkers() {
        List<Worker> allWorkers = new ArrayList<>();
        for (SegmentPaneController segmentPaneController : getSegmentPaneControllers()) {

            for (Worker worker : segmentPaneController.getWorkers()) {
                if (!allWorkers.contains(worker)) {
                    allWorkers.add(worker);
                }
            }

        }
        return allWorkers;
    }

    private SegmentPaneController initController() {
        SegmentPaneController controller = null;
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class
                    .getResource(ScreenCode.SEGMENT_PANE.resourcePath()));

            segmentPanes.add((Pane) loader.load());

            controller = loader.getController();
            segmentPaneControllers.add(controller);

            controller.setEventScreenController(this);
            controller.setWorkerInfoController(workerInfoController);
            controller.setDependencies(mainApp, gameController);

            controller.setBroadcastTeam(gameController.getBroadcastTeamManager().getDefaultBroadcastTeam(currentEvent.getEventTemplate()).isEmpty()
                    ? gameController.getBroadcastTeamManager().getDefaultBroadcastTeam(playerPromotion())
                    : gameController.getBroadcastTeamManager().getDefaultBroadcastTeam(currentEvent.getEventTemplate()));

        } catch (IOException ex) {
            logger.log(Level.ERROR, ex);
            throw new RuntimeException(ex);
        }
        return controller;
    }

    private void addSegment() {

        SegmentNameItem item = new SegmentNameItem();
        item.segment.set(initController().getSegment());
        segmentListView.getItems().add(item);
        segmentListView.getSelectionModel().select(item);

        segmentsChanged();

        updateLabels();
    }

    public void addSegment(Segment segment) {
        SegmentPaneController controller = initController();
        SegmentNameItem item = new SegmentNameItem();
        item.segment.set(controller.getSegment());
        segmentListView.getItems().add(item);
        segmentListView.getSelectionModel().select(item);
        segment.getMatchParticipantTeams().forEach(team -> controller.addTeam(team.getWorkers(), updatingChallenge));

        segmentsChanged();

        updateLabels();
    }

    public void removeSegment(int index) {
        if (getSegments().size() > 1) {

            int selectedIndex = segmentListView.getSelectionModel().getSelectedIndex();

            segmentListView.getItems().clear();

            segmentPanes.remove(index);

            //remove the controller too
            getSegmentPaneControllers().remove(index);

            for (Segment segment : getSegments()) {
                SegmentNameItem item = new SegmentNameItem();
                item.segment.set(segment);
                segmentListView.getItems().add(item);
            }

            if (segmentListView.getItems().size() > selectedIndex) {
                segmentListView.getSelectionModel().select(selectedIndex);
            } else {
                segmentListView.getSelectionModel().select(selectedIndex - 1);
            }

            segmentsChanged();

            updateLabels();
        }
    }

    private void initializeSegmentListView() {
        ObservableList<SegmentNameItem> items = FXCollections.observableArrayList(SegmentNameItem.extractor());

        segmentListView.setCellFactory(param -> new SorterCell(
                segmentPanes,
                segmentPaneControllers,
                segmentListView,
                this,
                gameController.getSegmentStringService()
        ));

        segmentListView.setItems(items);

        segmentListView.getSelectionModel().selectedIndexProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                    if (newValue != null && newValue.intValue() != oldValue.intValue() && newValue.intValue() >= 0) {
                        segmentPaneHolder.getChildren().clear();
                        ViewUtils.anchorRegionToParent(segmentPaneHolder, segmentPanes.get(newValue.intValue()));
                        updateLabels();
                    }
                });

    }

    @Override
    public void initializeMore() {
        queryHelper = new GameObjectQueryHelper(gameController);

        GameScreen sortControlscreen = ViewUtils.loadScreenFromResource(ScreenCode.SORT_CONTROL, mainApp, gameController, sortControlPane);
        sortControl = (SortControl) sortControlscreen.controller;

        GameScreen workerInfoScreen = ViewUtils.loadScreenFromResource(ScreenCode.WORKER_INFO, mainApp, gameController, workerInfoPane);
        workerInfoController = (WorkerInfoController) workerInfoScreen.controller;

        bookingBrowseComboBox.setItems((FXCollections.observableArrayList(
                BrowseMode.WORKERS,
                BrowseMode.TAG_TEAMS,
                BrowseMode.STABLES,
                BrowseMode.TITLES,
                BrowseMode.REFS,
                BrowseMode.BROADCAST
        )));
        bookingBrowseComboBox.getSelectionModel().selectFirst();
        bookingBrowseComboBox.valueProperty().addListener(new ChangeListener<BrowseMode>() {
            @Override
            public void changed(ObservableValue<? extends BrowseMode> observable, BrowseMode oldValue, BrowseMode newValue) {
                browseMode = newValue;
                sortControl.setBrowseMode(newValue);
                updateSegmentItemListView();
            }
        });

        sortControl.setCurrentPromotion(playerPromotion());
        sortControl.setBrowseMode(BrowseMode.WORKERS);
        sortControl.setUpdateAction(e -> {
            updateLabels();
        });

        initializeSegmentListView();

        initializeSegmentItemListView();

    }

    private void initializeSegmentItemListView() {
        final EventHandler<DragEvent> dragOverHandler = (DragEvent dragEvent) -> {
            LocalDragboard ldb = LocalDragboard.getINSTANCE();

            if (ldb.hasInterface(SegmentItem.class)) {
                dragEvent.acceptTransferModes(TransferMode.MOVE);
            }
        };

        segmentItemListView.setOnDragOver(dragOverHandler);

        segmentItemListView.getSelectionModel()
                .selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            SegmentItem item = segmentItemListView.getSelectionModel().getSelectedItem();
            if (item instanceof Worker) {
                workerInfoController.setWorker((Worker) item);
            } else {
                workerInfoController.clearText();
            }
        });

        //do this last as it is dependent on currentSegment
        updateSegmentItemListView();

        segmentItemListView.setOnDragDropped(new WorkersListViewDragDropHandler(this));

        segmentItemListView.setOnMouseClicked(click -> {
            if (click.getButton() == MouseButton.SECONDARY) {
                currentSegmentPaneController().addTeam(
                        segmentItemListView.getSelectionModel().getSelectedItem().getSegmentItems(), false);
            } else if (click.getButton() == MouseButton.PRIMARY && click.getClickCount() == 2) {
                currentSegmentPaneController().addTeam(
                        segmentItemListView.getSelectionModel().getSelectedItem().getSegmentItems(), 0);
            }
            updateLabels();
        });


    }

    public void updateSegmentItemListView() {
        int previousIndex = segmentItemListView.getSelectionModel().getSelectedIndex();

        boolean isMatch = currentSegmentPaneController() != null && currentSegmentPaneController().getSegmentType().equals(SegmentType.MATCH);

        List<SegmentItem> availableItems = queryHelper.segmentItemsToBrowse(browseMode, playerPromotion()).stream()
                .filter(segmentItem -> !segmentItemIsBookedForCurrentSegment(segmentItem))
                .filter(segmentItem -> !isMatch || !hasInjury(segmentItem))
                .collect(Collectors.toList());

        segmentItemListView.setItems(sortControl.getSortedList(availableItems));

        if (previousIndex > 0) {
            segmentItemListView.getSelectionModel().select(previousIndex);
        } else {
            segmentItemListView.getSelectionModel().selectFirst();
        }
    }

    private boolean segmentItemIsBookedForCurrentSegment(SegmentItem segmentItem) {
        boolean isBooked = false;
        if (currentSegment() != null
                && currentSegment().getSegmentItems().containsAll(segmentItem.getSegmentItems())) {
            isBooked = true;
        }

        return isBooked;
    }

    public boolean challengeForTonightIsPresent(Segment segment, int challengeSegmentIndex) {
        for (int i = challengeSegmentIndex; i < segmentPaneControllers.size(); i++) {
            if (SegmentType.MATCH.equals(segmentPaneControllers.get(i).getSegmentType())
                    && segmentsMatch(segmentPaneControllers.get(i).getSegment(), segment)) {
                return true;
            }
        }
        return false;
    }

    public boolean challengeForTonightIsPresent(Segment segment, SegmentPaneController sourceController) {
        return challengeForTonightIsPresent(segment, segmentPaneControllers.indexOf(sourceController));
    }

    private boolean segmentsMatch(Segment segment1, Segment segment2) {
        if (!Objects.equals(segment1.getSegmentType(), segment2.getSegmentType())) {
            return false;
        }
        if (segment1.getMatchParticipantTeams().size() != segment2.getMatchParticipantTeams().size()) {
            return false;
        }
        return segment1.getMatchParticipantTeams().stream()
                .allMatch(actualTeam -> segment2.getMatchParticipantTeams().stream()
                        .anyMatch(expectedTeam -> teamsMatch(actualTeam, expectedTeam))
                );
    }

    private boolean teamsMatch(SegmentTeam segment1, SegmentTeam segment2) {
        return segment1.getWorkers().size() == segment2.getWorkers().size()
                && segment1.getWorkers().containsAll(segment2.getWorkers());
    }

    private boolean segmentItemIsBookedForCurrentShow(SegmentItem segmentItem) {
        return segmentPaneControllers.stream().anyMatch(controller -> controller.getSegmentItems().contains(segmentItem));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        browseMode = BrowseMode.WORKERS;

        eventLength = 0;

        logger = LogManager.getLogger(this.getClass());

        defaultSegments = 1;

        initializeSegmentListView();

        setSegmentItemCellFactory(getSegmentItemListView());
    }

    private void setSegmentItemCellFactory(ListView listView) {

        listView.setCellFactory(lv -> new ListCell<SegmentItem>() {

            @Override
            public void updateItem(final SegmentItem segmentItem, boolean empty) {
                super.updateItem(segmentItem, empty);
                if (segmentItem != null) {
                    int booked = 0;
                    for (SegmentItem subItem : segmentItem.getSegmentItems()) {
                        if (segmentItemIsBookedForCurrentShow(subItem)) {
                            booked++;
                        }
                    }

                    getStyleClass().remove("highStat");
                    getStyleClass().remove("midStat");

                    if (segmentItem.getSegmentItems().size() == booked) {
                        getStyleClass().add("highStat");
                    } else if (booked != 0 && segmentItem.getSegmentItems().size() > booked) {
                        getStyleClass().add("midStat");
                    }

                    ViewUtils.initListCellForSegmentItemDragAndDrop(this, segmentItem, empty);
                } else {
                    setText(null);
                    setGraphic(null);
                    getStyleClass().remove("highStat");
                    getStyleClass().remove("midStat");
                }

            }

        });

    }

    private boolean hasInjury(SegmentItem segmentItem) {
        if (!(segmentItem instanceof Worker)) {
            return false;
        }
        return gameController.getInjuryManager().hasInjury((Worker) segmentItem);
    }

    /**
     * @return the workersListView
     */
    public ListView<SegmentItem> getSegmentItemListView() {
        return segmentItemListView;
    }

    /**
     * @return the segmentPaneControllers
     */
    public List<SegmentPaneController> getSegmentPaneControllers() {
        return segmentPaneControllers;
    }

    public SegmentPaneController currentSegmentPaneController() {
        if (segmentListView == null || segmentPaneControllers.isEmpty()
                || segmentListView.getSelectionModel().getSelectedIndex() < 0) {
            return null;
        }
        return segmentPaneControllers.get(segmentListView.getSelectionModel().getSelectedIndex());

    }

    public void segmentsChanged() {
        autoUpdateRefs();
        updateSegmentItemListView();
    }

    public void autoUpdateRefs() {
        List<StaffMember> refs = new ArrayList(gameController.getStaffManager().getStaff(StaffType.REFEREE, playerPromotion()));
        Collections.sort(refs, Comparator.comparingInt(StaffMember::getSkill));
        if (!refs.isEmpty()) {
            for (int i = segmentPaneControllers.size() - 1; i >= 0; i--) {
                SegmentPaneController controller = segmentPaneControllers.get(i);
                if (controller.getSegmentType().equals(SegmentType.ANGLE)) {
                    controller.clearRef();
                } else if (controller.isAutoSetRef()) {
                    controller.setRefAuto(refs.remove(refs.size() - 1));
                    if (refs.isEmpty()) {
                        refs.addAll(gameController.getStaffManager().getStaff(StaffType.REFEREE, playerPromotion()));
                    }
                }
            }
        }
    }

    /**
     * @return the currentEvent
     */
    public Event getCurrentEvent() {
        return currentEvent;
    }


}
