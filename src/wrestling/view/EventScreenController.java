/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wrestling.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import wrestling.MainApp;
import wrestling.model.Event;
import wrestling.model.GameController;
import wrestling.model.Segment;
import wrestling.model.Worker;

/**
 *
 *
 */
public class EventScreenController implements Initializable {

    private MainApp mainApp;
    private GameController gameController;

    private int totalSegments;

    @FXML
    private Button runEventButton;

    @FXML
    private Button addSegmentButton;

    @FXML
    private Button removeSegmentButton;

    @FXML
    private ListView<SegmentNameItem> segmentListView;

    @FXML
    private Label totalCostLabel;

    @FXML
    private ListView<Worker> workersListView;

    @FXML
    private GridPane gridPane;

    //@FXML
    //private BorderPane segmentBorderPane;
    private List<Pane> segmentPanes = new ArrayList<>();
    private List<SegmentPaneController> segmentPaneControllers = new ArrayList<>();
    private List<Segment> segments = new ArrayList<>();

    //this would be for keeping track of the index number of the currently
    //selected segment
    private Number currentSegmentNumber;

    public Number getCurrentSegmentNumber() {
        return currentSegmentNumber;
    }

    private void setCurrentSegmentNumber(Number number) {

        //remove the previous segment pane from the grid first
        gridPane.getChildren().remove(segmentPanes.get(currentSegmentNumber.intValue()));

        currentSegmentNumber = number;

        //here we  update the central pane to show the corresponding segment
        gridPane.add(segmentPanes.get(currentSegmentNumber.intValue()), 1, 0);
        GridPane.setRowSpan(segmentPanes.get(currentSegmentNumber.intValue()), 3);

    }

    private Event currentEvent;

    public void setCurrentEvent(Event event) {
        this.currentEvent = event;
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {

        if (event.getSource() == runEventButton) {

            //select the first segment so when we come back to do a new event
            //it will be highlighted already
            segmentListView.getSelectionModel().selectFirst();

            //have to update the event segments first
            //this updates the segments list as well as the current event-in-progress
            updateEvent();

            //create a new event with the updated segment list, date, player promotion
            Event finishedEvent = new Event(segments, gameController.date(), gameController.playerPromotion());

            //clear the segments, so when we come back to do a new event
            //it will be empty again
            segments.clear();

            //go through the segmentPaneControllers and clear all the teams
            for (SegmentPaneController current : segmentPaneControllers) {
                current.clear();
            }

            //tell the main app to show the browser and pass the event
            //so it can be selected by the corresponding controller
            mainApp.showBrowser(finishedEvent);

            //advance the day
            gameController.nextDay();

            //tell the main app to update labels
            mainApp.updateLabels();

        } else if (event.getSource() == addSegmentButton) {
            addSegment();
        }
    }

    //this updates the segment list associated with the controller
    //and calls to update everything on the screen to reflect this
    public void updateEvent() {

        
        segments.clear();
        for (SegmentPaneController currentController : segmentPaneControllers) {
            segments.add(currentController.getSegment());
        }
        
        currentEvent.setSegments(segments);
        updateLabels();
    }

    //updates lists and labels
    public void updateLabels() {

        totalCostLabel.setText("Total Cost: $" + currentEvent.totalCost());

        //update the segmentListView
        for (SegmentNameItem item : segmentListView.getItems()) {
                
                item.name.set(segments.get(item.number.getValue() - 1).toString());
            
             
        }
    }

    private int segmentListViewWidth = 300;

    /*
    adds a segment to the segment listview, creates the corresponding segment
    pane and controller and adds them to the proper arrays for reference
     */
    private void addSegment() {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/SegmentPane.fxml"));
            Pane segmentPane = (Pane) loader.load();

            //keep a reference to the segment pane
            segmentPanes.add(segmentPane);

            //keep a reference to the controller
            SegmentPaneController controller = loader.getController();
            segmentPaneControllers.add(controller);

            controller.setEventScreenController(this);
            controller.initializeMore();

            //update the segment listview
            SegmentNameItem item = new SegmentNameItem();
            segmentListView.getItems().add(item);
            item.number.set(segments.size());
            item.name.set("Segment " + segments.size());

            updateEvent();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    private void removeSegment() {

    }

    /*
    prepares the segment listView
    this may need modification if we allow adding/removing segments
     */
    private void initializeSegmentListView() {

        ObservableList<SegmentNameItem> items = FXCollections.observableArrayList(SegmentNameItem.extractor());

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

    private static class SegmentNameItem {

        StringProperty name = new SimpleStringProperty();
        IntegerProperty number = new SimpleIntegerProperty();

        public static Callback<SegmentNameItem, Observable[]> extractor() {
            return new Callback<SegmentNameItem, Observable[]>() {
                @Override
                public Observable[] call(SegmentNameItem param) {
                    return new Observable[]{param.number, param.name};
                }
            };
        }

        @Override
        public String toString() {
            return name.get();
        }
    }

    /*
    additional initialization to be called externally after we have our mainApp etc.
     */
    private void initializeMore() {

        //here we set a blank event, this will have to take an event from somewhere else
        //ideally
        this.currentEvent = new Event(gameController.date(), gameController.playerPromotion());

        //get the workers and add them to the listview on the left
        ObservableList<Worker> workersList = FXCollections.observableArrayList();

        List<Worker> roster = gameController.playerPromotion().roster;
        for (Worker worker : roster) {
            workersList.add(worker);
        }

        workersListView.setItems(workersList);

        initializeSegmentListView();

        /*
        create versespanes and controllers for each segment and keeps references
        will need to be more flexible when other segment types are possible
         */
        for (int i = 0; i < totalSegments; i++) {
            addSegment();
        }

        //hardcoded zeros, not great
        segmentListView.getSelectionModel().select(0);
        setCurrentSegmentNumber(0);

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        totalSegments = 8;

        currentSegmentNumber = 0;
        initializeSegmentListView();

        setWorkerCellFactory(workersListView);

    }

    private void setWorkerCellFactory(ListView listView) {
        listView.setCellFactory(new Callback<ListView<Worker>, ListCell<Worker>>() {

            @Override
            public ListCell<Worker> call(ListView<Worker> listView) {
                return new WorkerCell();
            }
        });

    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;

        initializeMore();
    }

}
