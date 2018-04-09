package wrestling.view.event;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import wrestling.model.Worker;
import wrestling.model.modelView.EventView;
import wrestling.model.modelView.SegmentView;
import wrestling.model.segmentEnum.SegmentType;
import wrestling.model.utility.TestUtils;
import wrestling.view.utility.LocalDragboard;
import wrestling.view.utility.RefreshSkin;
import wrestling.view.utility.Screen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.SortControlController;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.comparators.WorkerNameComparator;
import wrestling.view.utility.comparators.WorkerPopularityComparator;
import wrestling.view.utility.interfaces.ControllerBase;

public class EventScreenController extends ControllerBase implements Initializable {

    private int totalSegments;

    @FXML
    private Button runEventButton;

    @FXML
    private Button addSegmentButton;

    @FXML
    private ListView<SegmentNameItem> segmentListView;

    @FXML
    private Label totalCostLabel;

    @FXML
    private ListView<Worker> workersListView;

    @FXML
    private AnchorPane segmentPaneHolder;

    @FXML
    private Label eventTitleLabel;

    @FXML
    private AnchorPane sortControlPane;

    private final List<Pane> segmentPanes = new ArrayList<>();
    private final List<SegmentPaneController> segmentPaneControllers = new ArrayList<>();

    private SortedList workerSortedList;
    private Screen sortControl;

    private Event currentEvent;

    private Number currentSegmentNumber;

    private int eventLength;

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof Event) {

            if (currentEvent != null && !currentEvent.equals(obj)) {
                resetSegments();
            }
            currentEvent = (Event) obj;

            updateLabels();
        } else {
            logger.log(Level.ERROR, "Invalid object passed to EventScreen");
        }
    }

    private SegmentView currentSegment() {
        return segmentPaneControllers.get(getCurrentSegmentNumber().intValue()).getSegmentView();
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

    public void setCurrentSegmentNumber(int number) {

        Number newNumber = number;
        setCurrentSegmentNumber(newNumber);
    }

    private void setCurrentSegmentNumber(Number number) {
        currentSegmentNumber = number;

        segmentPaneHolder.getChildren().clear();
        ViewUtils.anchorPaneToParent(segmentPaneHolder, segmentPanes.get(getCurrentSegmentNumber().intValue()));

        updateLabels();

    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {

        if (event.getSource() == runEventButton) {
            if (removeEmpty(getSegmentViews()).isEmpty()) {
                ViewUtils.generateAlert(
                        "Error",
                        "Event is not valid.",
                        "An event must have at least one non-empty segment.",
                        AlertType.ERROR)
                        .showAndWait();
            } else {
                showResults();
            }
        } else if (event.getSource() == addSegmentButton) {
            addSegment();
        }
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

        for (int i = 0; i < totalSegments; i++) {
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
            String eventTitle = "Now booking: " + currentEvent.toString() + "\n";
            if (currentEvent.getTelevision() != null) {
                eventTitle += String.format("Event length: %d/%d", eventLength, currentEvent.getTelevision().getDuration());
            } else {
                eventTitle += String.format("Event length: %d", eventLength);
            }
            eventTitleLabel.setText(eventTitle);

        }

        updateWorkerListView();

        ((RefreshSkin) segmentListView.getSkin()).refresh();

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
            loader.setLocation(MainApp.class.getResource(ScreenCode.SEGMENT_PANE.resourcePath()));
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
            segmentListView.getItems().add(item);
            item.segment.set(controller.getSegmentView());

            updateLabels();

        } catch (IOException ex) {
            logger.log(Level.ERROR, ex);
        }
    }

    public void removeSegment(int index) {
        if (getSegmentViews().size() > 1) {

            int selectedIndex = segmentListView.getSelectionModel().getSelectedIndex();

            segmentListView.getItems().remove(index);

            segmentPanes.remove(index);

            setCurrentSegmentNumber(getCurrentSegmentNumber());

            //remove the controller too
            getSegmentPaneControllers().remove(index);

            if (segmentListView.getItems().size() > selectedIndex) {
                segmentListView.getSelectionModel().select(selectedIndex);
            } else {
                segmentListView.getSelectionModel().selectLast();
            }

            //update the event since we have changed the number of segments
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

        segmentListView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                //check that we have a valid newValue, because strange things happen otherwise
                //when we clear the list and refresh it
                if (newValue.intValue() >= 0) {
                    setCurrentSegmentNumber(newValue);

                }

            }
        });
    }


    /*
    additional initialization to be called externally after we have our mainApp etc.
     */
    @Override
    public void initializeMore() {

        sortControl = ViewUtils.loadScreenFromResource(ScreenCode.SORT_CONTROL, mainApp, gameController, sortControlPane);

        sortControl.controller.setCurrent(ViewUtils.getWorkerComparators(gameController));

        ((SortControlController) sortControl.controller).setParentScreenCode(ScreenCode.EVENT);

        //here we set a blank event
        initializeSegmentListView();

        /*
        create versespanes and controllers for each segment and keeps references
        will need to be more flexible when other segment types are possible
         */
        for (int i = 0; i < totalSegments; i++) {
            addSegment();
        }

        segmentListView.getSelectionModel().selectFirst();

        //for the workersListView to accept dragged items
        final EventHandler<DragEvent> dragOverHandler = (DragEvent dragEvent) -> {
            LocalDragboard ldb = LocalDragboard.getINSTANCE();
            if (ldb.hasType(Worker.class)) {
                dragEvent.acceptTransferModes(TransferMode.MOVE);
            }

        };

        workersListView.setOnDragOver(dragOverHandler);

        //do this last as it is dependent on currentSegment
        updateWorkerListView();

        //add the special DragDropHandlder
        getWorkersListView().setOnDragDropped(new WorkersListViewDragDropHandler(this));

    }

    private void updateWorkerListView() {
        List<Worker> workers = new ArrayList<>();

        int previousIndex = workersListView.getSelectionModel().getSelectedIndex();

        for (Worker worker : gameController.getContractManager().getFullRoster(playerPromotion())) {
            if (workerIsAvailableForCurrentSegment(worker)) {
                workers.add(worker);
            }
        }

        Comparator comparator = sortControl != null ? ((SortControlController) sortControl.controller).getCurrentComparator() : null;
        FilteredList filteredList = new FilteredList<>((FXCollections.observableArrayList(workers)), p
                -> !((SortControlController) sortControl.controller).isFiltered(p));

        workersListView.setItems(new SortedList<>(filteredList, comparator));

        if (previousIndex > 0) {
            workersListView.getSelectionModel().select(previousIndex);
        } else {
            workersListView.getSelectionModel().selectFirst();
        }

        ((RefreshSkin) getWorkersListView().getSkin()).refresh();

    }

    private boolean workerIsAvailableForCurrentSegment(Worker worker) {
        return !currentSegment().getWorkers().contains(worker)
                && gameController.getEventManager().isAvailable(
                        worker,
                        gameController.getDateManager().today(),
                        playerPromotion());
    }

    private boolean workerIsBookedOnShow(Worker worker) {
        for (SegmentPaneController controller : getSegmentPaneControllers()) {
            if (controller.getWorkers().contains(worker)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        eventLength = 0;

        logger = LogManager.getLogger(this.getClass());

        totalSegments = 8;

        currentSegmentNumber = 0;
        initializeSegmentListView();

        setWorkerCellFactory(getWorkersListView());

        RefreshSkin skin = new RefreshSkin(getWorkersListView());

        getWorkersListView().setSkin(skin);

    }

    private void setWorkerCellFactory(ListView listView) {

        listView.setCellFactory(lv -> new ListCell<Worker>() {

            @Override
            public void updateItem(final Worker worker, boolean empty) {
                super.updateItem(worker, empty);
                if (workerIsBookedOnShow(worker)) {
                    getStyleClass().add("highStat");
                } else {
                    getStyleClass().remove("highStat");
                }
                ViewUtils.initListCellForWorkerDragAndDrop(this, worker, empty);

            }

        });

    }

    /**
     * @return the currentSegmentNumber
     */
    public Number getCurrentSegmentNumber() {
        return segmentListView.getSelectionModel().selectedIndexProperty().get() == -1
                ? 0
                : currentSegmentNumber;
    }

    /**
     * @return the workersListView
     */
    public ListView<Worker> getWorkersListView() {
        return workersListView;
    }

    /**
     * @return the segmentPaneControllers
     */
    public List<SegmentPaneController> getSegmentPaneControllers() {
        return segmentPaneControllers;
    }

    // update the listview according to whatever browse mode we are in
    public static class SegmentNameItem {

        public static Callback<SegmentNameItem, Observable[]> extractor() {
            return (SegmentNameItem param) -> new Observable[]{param.segment};
        }
        ObjectProperty<SegmentView> segment = new SimpleObjectProperty();

    }

}
