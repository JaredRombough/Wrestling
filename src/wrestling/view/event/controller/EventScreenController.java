package wrestling.view.event.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import wrestling.MainApp;
import wrestling.model.Event;
import wrestling.model.SegmentItem;
import wrestling.model.Worker;
import wrestling.model.modelView.EventView;
import wrestling.model.modelView.SegmentView;
import wrestling.model.segmentEnum.BrowseMode;
import wrestling.model.segmentEnum.SegmentValidation;
import wrestling.model.utility.ModelUtils;
import wrestling.model.utility.TestUtils;
import wrestling.view.utility.LocalDragboard;
import wrestling.view.utility.RefreshSkin;
import wrestling.view.utility.GameScreen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.SortControlController;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

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
    private ListView<SegmentItem> listView;

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

    private GameScreen sortControl;

    private Event currentEvent;

    private int eventLength;

    private BrowseMode browseMode;

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof Event) {

            if (currentEvent != null && !currentEvent.equals(obj)) {
                resetSegments();
            }
            currentEvent = (Event) obj;
            eventTitleLabel.setText("Now booking: " + currentEvent.toString());

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
            String errors = getErrors();
            if (!errors.isEmpty()) {
                ViewUtils.generateAlert(
                        "Error",
                        "Event is not valid.",
                        errors,
                        AlertType.ERROR)
                        .showAndWait();
            } else {
                showResults();
            }
        } else if (event.getSource() == addSegmentButton) {
            addSegment();
        }
    }

    private String getErrors() {
        StringBuilder errors = new StringBuilder();
        List<SegmentView> segmentViews = getSegmentViews();
        if (!validateDuration()) {
            errors.append("Event duration is invalid\n");
        }
        for (int i = 0; i < segmentViews.size(); i++) {
            SegmentValidation validation = segmentViews.get(i).getValidationStatus();
            if (validation.equals(SegmentValidation.EMPTY)) {
                errors.append(String.format("Segment #%d is empty.\n", i + 1));
                continue;
            }

            if (validation.equals(SegmentValidation.INCOMPLETE)) {
                errors.append(String.format("Segment #%d has an empty team.\n", i + 1));
                break;
            }

        }

        return errors.toString();
    }

    private void showResults() {
        mainApp.setRootLayoutButtonDisable(true);
        boolean testing = false;
        if (testing) {
            mainApp.show(ScreenCode.RESULTS, TestUtils.testEventView(currentEvent, gameController.getContractManager().getFullRoster(playerPromotion()), mainApp.isRandomGame()));
        } else {
            mainApp.show(ScreenCode.RESULTS, new EventView(currentEvent, removeEmpty(getSegmentViews())));
        }

    }

    private List<SegmentView> removeEmpty(List<SegmentView> list) {
        List<SegmentView> returnList = new ArrayList<>();
        for (SegmentView segmentView : list) {
            if (!segmentView.getWorkers().isEmpty()) {
                returnList.add(segmentView);
            }

        }
        return returnList;
    }

    private void resetSegments() {
        segmentListView.getSelectionModel().clearSelection();

        segmentListView.getItems().clear();
        segmentPanes.clear();
        segmentPaneControllers.clear();

        for (int i = 0; i < defaultSegments; i++) {
            addSegment();
        }

        segmentListView.getSelectionModel().selectFirst();
    }

    //updates lists and labels
    @Override
    public void updateLabels() {

        totalCostLabel.setText("Total Cost: $" + currentCost());

        for (SegmentNameItem segmentNameItem : segmentListView.getItems()) {
            segmentNameItem.segment.set(getSegmentViews().get(segmentListView.getItems().indexOf(segmentNameItem)));
        }

        if (currentEvent != null) {

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

        updateListView();

        ((RefreshSkin) segmentListView.getSkin()).refresh();

    }

    private int getDuration() {
        return currentEvent.getDefaultDuration();
    }

    private boolean validateDuration() {
        return Math.abs(getDuration() - eventLength) <= eventLength / 10;
    }

    private int currentCost() {

        int currentCost = 0;

        for (Worker worker : allWorkers()) {
            currentCost += gameController.getContractManager().getContract(worker, playerPromotion()).getAppearanceCost();

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

    /*
    adds a segment to the segment listview, creates the corresponding segment
    pane and controller and adds them to the proper arrays for reference
     */
    private void addSegment() {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader
                    .setLocation(MainApp.class
                            .getResource(ScreenCode.SEGMENT_PANE.resourcePath()));
            Pane segmentPane = (Pane) loader.load();

            //keep a reference to the segment pane
            segmentPanes.add(segmentPane);

            //keep a reference to the controller
            SegmentPaneController controller = loader.getController();
            segmentPaneControllers.add(controller);

            controller.setEventScreenController(this);
            controller.setDependencies(mainApp, gameController);

            //update the segment listview
            SegmentNameItem item = new SegmentNameItem();
            item.segment.set(controller.getSegmentView());
            segmentListView.getItems().add(item);
            segmentListView.getSelectionModel().select(item);

            updateLabels();

        } catch (IOException ex) {
            logger.log(Level.ERROR, ex);
        }
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

            updateLabels();
        }
    }

    /*
    prepares the segment listView
    this may need modification if we allow adding/removing segments
     */
    private void initializeSegmentListView() {

        RefreshSkin skin = new RefreshSkin(segmentListView);
        segmentListView.setSkin(skin);

        ObservableList<SegmentNameItem> items = FXCollections.observableArrayList(SegmentNameItem.extractor());

        segmentListView.setCellFactory(param -> new SorterCell(
                segmentPanes, getSegmentPaneControllers(),
                getSegmentViews(),
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


    /*
    additional initialization to be called externally after we have our mainApp etc.
     */
    @Override
    public void initializeMore() {

        sortControl = ViewUtils.loadScreenFromResource(ScreenCode.SORT_CONTROL, mainApp, gameController, sortControlPane);

        sortControl.controller.setCurrent(BrowseMode.WORKERS);
        SortControlController sortControlController = (SortControlController) sortControl.controller;
        sortControlController.setParentScreenCode(ScreenCode.EVENT);
        sortControlController.setBookingBrowseModeEnabled(true);
        sortControlController.getBookingBrowseComboBox().valueProperty().addListener(new ChangeListener<BrowseMode>() {
            @Override
            public void changed(ObservableValue<? extends BrowseMode> observable, BrowseMode oldValue, BrowseMode newValue) {
                browseMode = newValue;
                updateListView();
            }
        });

        //here we set a blank event
        initializeSegmentListView();

        /*
        create versespanes and controllers for each segment and keeps references
        will need to be more flexible when other segment types are possible
         */
        for (int i = 0; i < defaultSegments; i++) {
            addSegment();
        }

        segmentListView.getSelectionModel().selectFirst();

        //for the workersListView to accept dragged items
        final EventHandler<DragEvent> dragOverHandler = (DragEvent dragEvent) -> {
            LocalDragboard ldb = LocalDragboard.getINSTANCE();

            if (ldb.hasInterface(SegmentItem.class)) {
                dragEvent.acceptTransferModes(TransferMode.MOVE);
            }
        };

        listView.setOnDragOver(dragOverHandler);

        //do this last as it is dependent on currentSegment
        updateListView();

        //add the special DragDropHandlder
        getListView().setOnDragDropped(new WorkersListViewDragDropHandler(this));

    }

    private void updateListView() {
        List<SegmentItem> segmentItems = new ArrayList<>();

        int previousIndex = listView.getSelectionModel().getSelectedIndex();

        for (SegmentItem segmentItem : browseMode.listToBrowse(gameController, playerPromotion())) {
            if (!segmentItemIsBookedForCurrentSegment(segmentItem)) {
                segmentItems.add(segmentItem);
            }
        }

        Comparator comparator = sortControl != null ? ((SortControlController) sortControl.controller).getCurrentComparator() : null;
        FilteredList filteredList = new FilteredList<>((FXCollections.observableArrayList(segmentItems)), p
                -> !((SortControlController) sortControl.controller).isFiltered(p));

        listView.setItems(new SortedList<>(filteredList, comparator));

        if (previousIndex > 0) {
            listView.getSelectionModel().select(previousIndex);
        } else {
            listView.getSelectionModel().selectFirst();
        }

        ((RefreshSkin) getListView().getSkin()).refresh();

    }

    private boolean segmentItemIsBookedForCurrentSegment(SegmentItem segmentItem) {
        boolean isBooked = false;
        if (currentSegment() != null
                && currentSegment().getWorkers().containsAll(segmentItem.getSegmentItems())) {
            isBooked = true;
        }

        return isBooked;
    }

    private boolean segmentItemIsBookedForCurrentShow(SegmentItem segmentItem) {
        for (SegmentPaneController controller : getSegmentPaneControllers()) {
            if (controller.getWorkers().contains(segmentItem)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        browseMode = BrowseMode.WORKERS;

        eventLength = 0;

        timeLabels = new ArrayList<>(Arrays.asList(totalTimeLabel, maxTimeLabel, remainingTimeLabel));

        logger = LogManager.getLogger(this.getClass());

        defaultSegments = 1;

        initializeSegmentListView();

        setSegmentItemCellFactory(getListView());

        RefreshSkin skin = new RefreshSkin(getListView());

        getListView().setSkin(skin);

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

                    if (segmentItem.getSegmentItems().size() == booked) {
                        getStyleClass().add("highStat");
                    } else if (booked != 0 && segmentItem.getSegmentItems().size() > booked) {
                        getStyleClass().add("midStat");
                    } else {
                        getStyleClass().remove("highStat");
                        getStyleClass().remove("midStat");
                    }

                    ViewUtils.initListCellForSegmentItemDragAndDrop(this, segmentItem, empty);
                }

            }

        });

    }

    /**
     * @return the workersListView
     */
    public ListView<SegmentItem> getListView() {
        return listView;
    }

    /**
     * @return the segmentPaneControllers
     */
    public List<SegmentPaneController> getSegmentPaneControllers() {
        return segmentPaneControllers;
    }

    public SegmentPaneController currentSegmentPaneController() {
        return segmentPaneControllers.get(segmentListView.getSelectionModel().getSelectedIndex());

    }

    // update the listview according to whatever browse mode we are in
    public static class SegmentNameItem {

        public static Callback<SegmentNameItem, Observable[]> extractor() {
            return (SegmentNameItem param) -> new Observable[]{param.segment};
        }
        ObjectProperty<SegmentView> segment = new SimpleObjectProperty();

    }

}
