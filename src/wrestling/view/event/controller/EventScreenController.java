package wrestling.view.event.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import wrestling.MainApp;
import wrestling.model.Event;
import wrestling.model.SegmentItem;
import wrestling.model.SegmentTemplate;
import wrestling.model.modelView.EventView;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.modelView.SegmentView;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.TitleView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.segmentEnum.BrowseMode;
import wrestling.model.segmentEnum.SegmentType;
import wrestling.model.segmentEnum.SegmentValidation;
import wrestling.model.segmentEnum.StaffType;
import wrestling.model.segmentEnum.TeamType;
import wrestling.model.utility.ModelUtils;
import wrestling.model.utility.SegmentUtils;
import static wrestling.model.utility.SegmentUtils.getMatchLossMoralePenalty;
import static wrestling.model.utility.SegmentUtils.getMatchObjectors;
import wrestling.model.utility.StaffUtils;
import wrestling.model.utility.TestUtils;
import wrestling.view.utility.GameScreen;
import wrestling.view.utility.LocalDragboard;
import wrestling.view.utility.RefreshSkin;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.SortControl;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;
import static wrestling.model.utility.SegmentUtils.getMatchMoralePenalties;

public class EventScreenController extends ControllerBase implements Initializable {

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
    private Label eventTitleLabel;

    @FXML
    private AnchorPane sortControlPane;

    @FXML
    private Label totalTimeLabel;
    @FXML
    private Label maxTimeLabel;
    @FXML
    private Label remainingTimeLabel;
    private List<Label> timeLabels;

    private final List<Pane> segmentPanes = new ArrayList<>();
    private final List<SegmentPaneController> segmentPaneControllers = new ArrayList<>();

    private SortControl sortControl;

    private Event currentEvent;

    private int eventLength;

    private BrowseMode browseMode;

    @FXML
    private ComboBox<BrowseMode> bookingBrowseComboBox;

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof Event) {
            if (!Objects.equals(currentEvent, obj)) {
                currentEvent = (Event) obj;
                eventTitleLabel.setText("Now booking: " + getCurrentEvent().toString());
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

    private SegmentView currentSegment() {
        if (segmentListView == null || segmentListView.getSelectionModel().getSelectedIndex() < 0) {
            return null;
        }
        return segmentPaneControllers.get(segmentListView.getSelectionModel().getSelectedIndex()).getSegmentView();
    }

    private List<SegmentView> getSegmentViews() {
        List<SegmentView> segmentViews = new ArrayList<>();
        eventLength = 0;
        for (SegmentPaneController controller : segmentPaneControllers) {
            SegmentView segmentView = controller.getSegmentView();
            segmentViews.add(segmentView);
            eventLength += segmentView.getSegment().getSegmentLength();
        }
        return segmentViews;
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

        getSegmentViews().stream().forEach(segment -> {
            List<WorkerView> objectors = getMatchObjectors(segment);
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
        List<SegmentView> segmentViews = getSegmentViews();
        if (!validateDuration()) {
            errors.append("Event duration is invalid.\n");
        }
        for (int i = 0; i < segmentViews.size(); i++) {
            SegmentValidation validation = segmentViews.get(i).getValidationStatus();
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
        for (int i = 0; i < getSegmentViews().size(); i++) {
            SegmentView segmentView = getSegmentViews().get(i);
            if (segmentView.getSegmentType().equals(SegmentType.MATCH) && segmentView.getReferee() == null) {
                warnings.append(String.format("Segment #%d has no referee.\n", i + 1));
            }
            if (SegmentUtils.isChallengeForTonight(segmentView)) {
                SegmentView challengeMatch = ModelUtils.getSegmentFromTemplate(segmentView.getAngleParams().getChallengeSegment());
                if (!challengeForTonightIsPresent(challengeMatch, i)) {
                    warnings.append(String.format("%s\nA challenge for this match was made and accepted tonight, but it is not present.\n",
                            gameController.getSegmentManager().getVsMatchString(challengeMatch)));
                }
            }
            for (TitleView titleView : segmentView.getTitleViews()) {
                if (!titleView.getChampions().isEmpty()
                        && !ModelUtils.teamIsPresent(titleView.getChampions(),
                                segmentPaneControllers.get(i).getWorkerTeamWrappers())) {
                    warnings.append(String.format("The %s Title is not being defended by %s.\n",
                            titleView.getShortName(),
                            ModelUtils.slashNames(titleView.getChampions())));
                }

            }
        }
        for (SegmentTemplate segmentTemplate : currentEvent.getEventTemplate().getSegmentTemplates()) {
            SegmentView challengeMatch = ModelUtils.getSegmentFromTemplate(segmentTemplate);
            if (!challengeForTonightIsPresent(challengeMatch, 0)) {
                warnings.append(String.format("%s\nA challenge for this match was made and accepted on %s at %s, but it is not present.\n",
                        gameController.getSegmentManager().getVsMatchString(challengeMatch),
                        segmentTemplate.getSourceEvent().getDate().toString(),
                        segmentTemplate.getSourceEvent().getName()));
            }
        }
        return warnings.toString();
    }

    private void showResults() {
        mainApp.setRootLayoutButtonDisable(true);
        boolean testing = false;
        if (testing) {
            mainApp.show(ScreenCode.RESULTS, TestUtils.testEventView(getCurrentEvent(), playerPromotion().getFullRoster(), mainApp.isRandomGame()));
        } else {
            mainApp.show(ScreenCode.RESULTS, new EventView(getCurrentEvent(), removeEmpty(getSegmentViews())));
        }

    }

    private List<SegmentView> removeEmpty(List<SegmentView> list) {
        return list.stream().filter(segmentView -> !segmentView.getWorkers().isEmpty()).collect(Collectors.toList());
    }

    private void resetSegments() {
        segmentListView.getSelectionModel().clearSelection();

        segmentListView.getItems().clear();
        segmentPanes.clear();
        segmentPaneControllers.clear();

        for (SegmentTemplate segmentTemplate : currentEvent.getEventTemplate().getSegmentTemplates()) {
            addSegment(ModelUtils.getSegmentFromTeams(segmentTemplate.getSegmentTeams()));
        }

        for (int i = 0; i < defaultSegments; i++) {
            addSegment();
        }

        segmentListView.getSelectionModel().selectFirst();
    }

    private boolean updatingChallenge = false;

    @Override
    public void updateLabels() {

        if (currentSegmentPaneController() != null) {
            currentSegmentPaneController().updateLabels();
        }

        totalCostLabel.setText("Total Cost: $" + currentCost());
        totalCostLabel.setVisible(currentCost() != 0);

        List<SegmentView> segmentViews = getSegmentViews();

        for (SegmentNameItem segmentNameItem : segmentListView.getItems()) {
            segmentNameItem.segment.set(segmentViews.get(segmentListView.getItems().indexOf(segmentNameItem)));
        }

        if (getCurrentEvent() != null) {

            int duration = getDuration();

            int remaining = duration - eventLength;
            int remainingHours = (duration - eventLength) / 60;
            int remainingMinutes = (duration - eventLength) % 60;

            totalTimeLabel.setText("Total:\t\t" + ModelUtils.timeString(eventLength));
            maxTimeLabel.setText("Max:\t\t\t" + ModelUtils.timeString(duration));
            if (remainingHours < 0 || remainingMinutes < 0) {
                remainingTimeLabel.setText("Remaining:\t-"
                        + ModelUtils.timeString(Math.abs(duration - eventLength)));
            } else {
                remainingTimeLabel.setText("Remaining:\t"
                        + ModelUtils.timeString(Math.abs(duration - eventLength)));
            }
            totalTimeLabel.getStyleClass().clear();
            if (Math.abs(remaining) <= 10) {
                totalTimeLabel.getStyleClass().add("highStat");
            } else if (Math.abs(remaining) <= 30) {
                totalTimeLabel.getStyleClass().add("midStat");
            } else {
                totalTimeLabel.getStyleClass().add("lowStat");
            }

        }

        updateSegmentItemListView();

        ((RefreshSkin) segmentListView.getSkin()).refresh();

    }

    private int getDuration() {
        return getCurrentEvent().getDefaultDuration();
    }

    private boolean validateDuration() {
        return Math.abs(getDuration() - eventLength) <= eventLength / 10;
    }

    private int currentCost() {

        int currentCost = 0;

        for (WorkerView worker : allWorkers()) {
            currentCost += gameController.getContractManager().getContract(worker, playerPromotion()).getAppearanceCost();

        }
        return currentCost;
    }

    private List<WorkerView> allWorkers() {
        List<WorkerView> allWorkers = new ArrayList<>();
        for (SegmentPaneController segmentPaneController : getSegmentPaneControllers()) {

            for (WorkerView worker : segmentPaneController.getWorkers()) {
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
            controller.setDependencies(mainApp, gameController);

            controller.setBroadcastTeam(currentEvent.getEventTemplate().getDefaultBroadcastTeam().isEmpty()
                    ? playerPromotion().getDefaultBroadcastTeam()
                    : currentEvent.getEventTemplate().getDefaultBroadcastTeam());

        } catch (IOException ex) {
            logger.log(Level.ERROR, ex);
        }
        return controller;
    }

    private void addSegment() {

        SegmentNameItem item = new SegmentNameItem();
        item.segment.set(initController().getSegmentView());
        segmentListView.getItems().add(item);
        segmentListView.getSelectionModel().select(item);

        segmentsChanged();

        updateLabels();
    }

    public void addSegment(SegmentView segmentView) {
        SegmentPaneController controller = initController();
        SegmentNameItem item = new SegmentNameItem();
        item.segment.set(controller.getSegmentView());
        segmentListView.getItems().add(item);
        segmentListView.getSelectionModel().select(item);
        segmentView.getMatchParticipantTeams().forEach(team -> controller.addTeam(team.getWorkers(), updatingChallenge));

        segmentsChanged();

        updateLabels();
    }

    public void removeSegment(int index) {
        if (getSegmentViews().size() > 1) {

            int selectedIndex = segmentListView.getSelectionModel().getSelectedIndex();

            segmentListView.getItems().clear();

            segmentPanes.remove(index);

            //remove the controller too
            getSegmentPaneControllers().remove(index);

            for (SegmentView segmentView : getSegmentViews()) {
                SegmentNameItem item = new SegmentNameItem();
                item.segment.set(segmentView);
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

        RefreshSkin skin = new RefreshSkin(segmentListView);
        segmentListView.setSkin(skin);

        ObservableList<SegmentNameItem> items = FXCollections.observableArrayList(SegmentNameItem.extractor());

        segmentListView.setCellFactory(param -> new SorterCell(
                segmentPanes,
                segmentPaneControllers,
                segmentListView,
                this,
                gameController.getSegmentManager()
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

        GameScreen sortControlscreen = ViewUtils.loadScreenFromResource(ScreenCode.SORT_CONTROL, mainApp, gameController, sortControlPane);
        sortControl = (SortControl) sortControlscreen.controller;

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

        //do this last as it is dependent on currentSegment
        updateSegmentItemListView();

        segmentItemListView.setOnDragDropped(new WorkersListViewDragDropHandler(this));

        segmentItemListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getButton() == MouseButton.SECONDARY) {
                    currentSegmentPaneController().addTeam(
                            segmentItemListView.getSelectionModel().getSelectedItem().getSegmentItems(), false);
                } else if (click.getButton() == MouseButton.PRIMARY && click.getClickCount() == 2) {
                    currentSegmentPaneController().addTeam(
                            segmentItemListView.getSelectionModel().getSelectedItem().getSegmentItems(), 0);
                }
                updateLabels();
            }
        });

    }

    public void updateSegmentItemListView() {
        List<SegmentItem> segmentItems = new ArrayList<>();

        int previousIndex = segmentItemListView.getSelectionModel().getSelectedIndex();

        for (SegmentItem segmentItem : browseMode.listToBrowse(gameController, playerPromotion())) {
            if (!segmentItemIsBookedForCurrentSegment(segmentItem)) {
                segmentItems.add(segmentItem);
            }
        }

        boolean isMatch = currentSegmentPaneController() != null
                ? currentSegmentPaneController().getSegmentType().equals(SegmentType.MATCH)
                : true;

        Comparator comparator = sortControl != null ? sortControl.getCurrentComparator() : null;
        FilteredList filteredList = new FilteredList<>((FXCollections.observableArrayList(segmentItems)), segmentItem
                -> !(sortControl.isFiltered(segmentItem) || (isMatch && filterInjured(segmentItem))));

        segmentItemListView.setItems(new SortedList<>(filteredList, comparator));

        if (previousIndex > 0) {
            segmentItemListView.getSelectionModel().select(previousIndex);
        } else {
            segmentItemListView.getSelectionModel().selectFirst();
        }

        ((RefreshSkin) getSegmentItemListView().getSkin()).refresh();

    }

    private boolean segmentItemIsBookedForCurrentSegment(SegmentItem segmentItem) {
        boolean isBooked = false;
        if (currentSegment() != null
                && currentSegment().getSegmentItems().containsAll(segmentItem.getSegmentItems())) {
            isBooked = true;
        }

        return isBooked;
    }

    public boolean challengeForTonightIsPresent(SegmentView segmentView, int challengeSegmentIndex) {
        for (int i = challengeSegmentIndex; i < segmentPaneControllers.size(); i++) {
            if (SegmentType.MATCH.equals(segmentPaneControllers.get(i).getSegmentType())
                    && segmentsMatch(segmentPaneControllers.get(i).getSegmentView(), segmentView)) {
                return true;
            }
        }
        return false;
    }

    public boolean challengeForTonightIsPresent(SegmentView segmentView, SegmentPaneController sourceController) {
        return challengeForTonightIsPresent(segmentView, segmentPaneControllers.indexOf(sourceController));
    }

    private boolean segmentsMatch(SegmentView segment1, SegmentView segment2) {
        if (!Objects.equals(segment1.getSegmentType(), segment2.getSegmentType())) {
            return false;
        }
        if (segment1.getMatchParticipantTeams().size() != segment2.getMatchParticipantTeams().size()) {
            return false;
        }
        return segment1.getMatchParticipantTeams().stream().allMatch(actualTeam -> {
            return segment2.getMatchParticipantTeams().stream().anyMatch(expectedTeam -> {
                return teamsMatch(actualTeam, expectedTeam);
            });
        });
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

        timeLabels = new ArrayList<>(Arrays.asList(totalTimeLabel, maxTimeLabel, remainingTimeLabel));

        logger = LogManager.getLogger(this.getClass());

        defaultSegments = 1;

        initializeSegmentListView();

        setSegmentItemCellFactory(getSegmentItemListView());

        RefreshSkin skin = new RefreshSkin(getSegmentItemListView());

        getSegmentItemListView().setSkin(skin);

    }

    private void setSegmentItemCellFactory(ListView listView) {

        listView.setCellFactory(lv -> new ListCell<SegmentItem>() {

            @Override
            public void updateItem(final SegmentItem segmentItem, boolean empty) {
                super.updateItem(segmentItem, empty);
                int booked = 0;
                if (segmentItem != null) {
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
                }

            }

        });

    }

    private boolean filterInjured(SegmentItem segmentItem) {
        if (!(segmentItem instanceof WorkerView)) {
            return false;
        }
        return ((WorkerView) segmentItem).getInjury() != null;

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
        List<StaffView> refs = new ArrayList(StaffUtils.getStaff(StaffType.REFEREE, playerPromotion()));
        Collections.sort(refs, Comparator.comparingInt(StaffView::getSkill));
        if (!refs.isEmpty()) {
            for (int i = segmentPaneControllers.size() - 1; i >= 0; i--) {
                SegmentPaneController controller = segmentPaneControllers.get(i);
                if (controller.getSegmentType().equals(SegmentType.ANGLE)) {
                    controller.clearRef();
                } else if (controller.isAutoSetRef()) {
                    controller.setRefAuto(refs.remove(refs.size() - 1));
                    if (refs.isEmpty()) {
                        refs.addAll(StaffUtils.getStaff(StaffType.REFEREE, playerPromotion()));
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

    public static class SegmentNameItem {

        public static Callback<SegmentNameItem, Observable[]> extractor() {
            return (SegmentNameItem param) -> new Observable[]{param.segment};
        }
        ObjectProperty<SegmentView> segment = new SimpleObjectProperty();

    }

}
