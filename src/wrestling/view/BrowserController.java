package wrestling.view;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import wrestling.MainApp;
import wrestling.model.EventArchive;
import wrestling.model.GameController;
import wrestling.model.Promotion;
import wrestling.model.Worker;
import wrestling.view.sort.EventDateComparator;
import wrestling.view.sort.WorkerNameComparator;
import wrestling.view.sort.WorkerPopularityComparator;

/**
 *
 * main browser, to be used for checking data for almost everything
 */
public class BrowserController implements Initializable {

    private MainApp mainApp;
    private GameController gameController;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
        initializeMore();
    }

    @FXML
    private Button rosterButton;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button eventsButton;

    @FXML
    private Button staffButton;

    @FXML
    private Button titlesButton;

    @FXML
    private Button stablesButton;

    @FXML
    private Button teamsButton;

    @FXML
    private Button freeAgentsButton;

    @FXML
    private Button myPromotionButton;

    @FXML
    private ComboBox promotionComboBox;

    @FXML
    private Label currentPromotionLabel;

    @FXML
    private ComboBox sortBox;

    private ObservableList<Comparator<Worker>> workerComparators;

    private Button lastButton;

    //for keeping track of the last nodes displayed 
    //so we can find it and remove it from the gridpane before replacing it
    private Node lastDisplayNode;
    private ListView lastListView;

    private AnchorPane workerOverviewPane;
    private WorkerOverviewController workerOverviewPaneController;
    private Label eventSummary;

    private ListView eventsListView;
    private ListView workersListView;
    private Label categoryButton;

    private Promotion currentPromotion;

    /*
    sets the current promotion
    updates relevant labels
    fires the last button to update the list
     */
    private void setCurrentPromotion(Promotion newPromotion) {
        this.currentPromotion = newPromotion;

        categoryButton.setText(newPromotion.toString());

        //make sure the combobox is on the correct promotion
        //in case we have called this from some
        promotionComboBox.getSelectionModel().select(currentPromotion);

        currentPromotionLabel.setText(currentPromotion.getName() + "\n"
                + "Level " + currentPromotion.getLevel()
                + "\tPopularity " + currentPromotion.getPopulatirty()
                + "\tFunds: " + currentPromotion.getFunds());

        workerOverviewPaneController.setCurrentPromotion(currentPromotion);

        //this is kind of a hack but it gets the main listview
        //to display whatever was last selected (roster, events, etc.)
        //for the newly selected promotion
        //might not work for more complex situations
        lastButton.fire();

    }

    private void sortByPopularity(List<Worker> workerList) {
        //sort roster by popularity
        Collections.sort(workerList, new Comparator<Worker>() {
            @Override
            public int compare(Worker w1, Worker w2) {
                return -Integer.valueOf(w1.getPopularity()).compareTo(w2.getPopularity());
            }
        });
    }
    private SortedList<Worker> workerSortedList;
    private SortedList<EventArchive> eventSortedList;
    private SortedList lastSortedList;

    private void setListViewWorkers(ListView listView, List<Worker> list) {

        FilteredList<Worker> filteredList
                = new FilteredList<>(FXCollections.observableArrayList(list), p -> true);

        //add filter stuff here
        workerSortedList = new SortedList<>(filteredList);

        sortBoxWorkers();

        workerSortedList.comparatorProperty().bind(sortBox.valueProperty());

        listView.setItems(workerSortedList);
    }

    /*
    prepare the sort box to sort workers
     */
    private void sortBoxWorkers() {
        //the list of comparators we want
        ObservableList comparators = FXCollections.observableArrayList(
                new WorkerNameComparator(),
                new WorkerPopularityComparator()
        );

        //definitely update the box if the box is empty
        if (sortBox.getItems().isEmpty()) {
            sortBox.setItems(comparators);
            sortBox.getSelectionModel().selectFirst();
        }

        //if the box is not empty check if it has the same stuff we're trying to put in it
        if (!sortBox.getItems().get(0).getClass().equals(comparators.get(0).getClass())) {
            sortBox.setItems(comparators);
            sortBox.getSelectionModel().selectFirst();
        }

    }

    private void sortBoxEvents() {
        ObservableList comparators = FXCollections.observableArrayList(
                new EventDateComparator()
        );
        if (!sortBox.getItems().get(0).getClass().equals(comparators.get(0).getClass())) {
            sortBox.setItems(comparators);
            sortBox.getSelectionModel().selectFirst();
        }

    }

    private void setListViewContentEvents(ListView listView, List<EventArchive> list) {

        FilteredList<EventArchive> filteredList
                = new FilteredList<>(FXCollections.observableArrayList(list), p -> true);

        eventSortedList = new SortedList<>(filteredList);

        sortBoxEvents();

        eventSortedList.comparatorProperty().bind(sortBox.valueProperty());

        listView.setItems(eventSortedList);
        sortBox.getSelectionModel().selectFirst();

    }

    public void updateLabels() {
        setCurrentPromotion(currentPromotion);
        workerOverviewPaneController.updateLabels();
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {

        if (event.getSource() == rosterButton) {

            browseWorkers();

            lastButton = rosterButton;

        } else if (event.getSource() == eventsButton) {

            browseEvents();

            lastButton = eventsButton;

        } else if (event.getSource() == freeAgentsButton) {

            browseFreeAgents();

        } else if (event.getSource() == myPromotionButton) {

            setCurrentPromotion(gameController.playerPromotion());

        }
    }

    /*
    meant to be called from the event booking screen (or perhaps elsewhere)
    and shows the most recent event. right now it just selects the first one
    on the list
     */
    public void showLastEvent() {
        setCurrentPromotion(gameController.playerPromotion());
        eventsListView.getSelectionModel().selectFirst();
        eventsButton.fire();
    }

    /*
    clear the last listview and display node
     */
    private void clearLast() {

        if (lastSortedList != null) {
            lastSortedList.comparatorProperty().unbind();
        }

        gridPane.getChildren().remove(lastListView);
        gridPane.getChildren().remove(lastDisplayNode);
    }

    private void browseEvents() {
        clearLast();

        setListViewContentEvents(eventsListView, currentPromotion.getEventArchives());

        gridPane.add(eventSummary, 1, 1);
        GridPane.setRowSpan(eventSummary, 2);
        gridPane.add(eventsListView, 0, 1);
        GridPane.setRowSpan(eventsListView, 2);

        lastListView = eventsListView;
        lastSortedList = eventSortedList;
        lastDisplayNode = eventSummary;

        eventsListView.getSelectionModel().selectFirst();
    }

    private void browseWorkers() {

        clearLast();

        setListViewWorkers(workersListView, currentPromotion.getRoster());

        gridPane.add(workersListView, 0, 1);
        GridPane.setRowSpan(workersListView, 2);
        gridPane.add(workerOverviewPane, 1, 1);

        lastListView = workersListView;
        lastDisplayNode = workerOverviewPane;
        lastSortedList = workerSortedList;

        workersListView.getSelectionModel().selectFirst();

    }

    private void browseFreeAgents() {

        clearLast();

        setListViewWorkers(workersListView, gameController.freeAgents(gameController.playerPromotion()));

        gridPane.add(workersListView, 0, 1);
        GridPane.setRowSpan(workersListView, 2);
        gridPane.add(workerOverviewPane, 1, 1);

        lastListView = workersListView;
        lastDisplayNode = workerOverviewPane;
        lastSortedList = workerSortedList;

        workersListView.getSelectionModel().selectFirst();

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        workersListView = new ListView<>();
        eventsListView = new ListView<>();
        eventSummary = new Label();
        categoryButton = new Label();

        stablesButton.setDisable(true);
        staffButton.setDisable(true);
        teamsButton.setDisable(true);
        titlesButton.setDisable(true);

    }

    private void initializePromotionCombobox() {

        //set up the promotion combobox
        promotionComboBox.getItems().addAll(gameController.promotions);
        promotionComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Promotion>() {
            @Override
            public void changed(ObservableValue<? extends Promotion> observable, Promotion oldValue, Promotion newValue) {
                setCurrentPromotion(newValue);

            }
        });

    }

    private void prepareWorkerBrowsing() {

        //load the workeroverview pane
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/WorkerOverview.fxml"));
            workerOverviewPane = (AnchorPane) loader.load();

            workerOverviewPaneController = (WorkerOverviewController) loader.getController();

            workerOverviewPaneController.setMainApp(this.mainApp);
            workerOverviewPaneController.setGameController(this.gameController);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //get the listview ready
        workersListView.setItems(FXCollections.observableArrayList(gameController.playerPromotion().getRoster()));

        workersListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Worker>() {
            @Override
            public void changed(ObservableValue<? extends Worker> observable, Worker oldValue, Worker newValue) {
                //for switching between promotions we may get a null value
                //keep the old worker and wait until we're called again
                if (newValue != null) {
                    workerOverviewPaneController.setCurrentWorker(newValue);

                }

            }
        });

    }

    private void prepareEventBrowsing() {

        //get the listview ready
        eventsListView.setItems(FXCollections.observableArrayList(gameController.playerPromotion().getEventArchives()));

        eventsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EventArchive>() {
            @Override
            public void changed(ObservableValue<? extends EventArchive> observable, EventArchive oldValue, EventArchive newValue) {
                //for switching between promotions we may get a null value
                //keep the old worker and wait until we're called again
                if (newValue != null) {

                    eventSummary.setText(newValue.getSummary());

                }

            }
        });

    }

    /*    private void initializeSortBox() {
    workerComparators = FXCollections.observableArrayList(new NameComparator(),
    new PopularityComparator());
    sortBox.setItems(workerComparators);
    }*/
    private void initializeMore() {
        //right now this acts as the default view for the screen
        //set whatever we want the default view to be to the lastbutton
        //so it will fire later
        lastButton = rosterButton;
        lastDisplayNode = workerOverviewPane;

        initializePromotionCombobox();
        //initializeSortBox();

        prepareWorkerBrowsing();
        prepareEventBrowsing();

        promotionComboBox.setValue(gameController.playerPromotion());
        lastButton.fire();

    }

}
