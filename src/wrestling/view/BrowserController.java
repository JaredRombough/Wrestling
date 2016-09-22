package wrestling.view;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import wrestling.model.Event;
import wrestling.model.GameController;
import wrestling.model.Promotion;
import wrestling.model.Worker;

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
                + "\tPopularity " + currentPromotion.getPopulatirty());

        //this is kind of a hack but it gets the main listview
        //to display whatever was last selected (roster, events, etc.)
        //for the newly selected promotion
        //might not work for more complex situations
        lastButton.fire();

    }

    private void setListViewContent(ListView listView, List list) {
        listView.getItems().clear();
        listView.setItems(FXCollections.observableArrayList(list));
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

    public void showEvent(Event event) {
        //this would take an event, find the promotion, select it properly
        //ie so another screen can send to the browser with a particular event already selected on open
        //might want this with workers, etc
        setCurrentPromotion(event.getPromotion());
        eventsListView.getSelectionModel().select(event);
        eventSummary.setText(event.getSummary());
        eventsButton.fire();

    }

    /*
    clear the last listview and display node
     */
    private void clearLast() {
        gridPane.getChildren().remove(lastListView);
        gridPane.getChildren().remove(lastDisplayNode);
    }

    private void browseEvents() {
        clearLast();

        setListViewContent(eventsListView, currentPromotion.getEvents());

        gridPane.add(eventSummary, 1, 1);
        GridPane.setRowSpan(eventSummary, 2);
        gridPane.add(eventsListView, 0, 1);
        GridPane.setRowSpan(eventsListView, 2);

        lastListView = eventsListView;
        lastDisplayNode = eventSummary;

        eventsListView.getSelectionModel().selectFirst();
    }

    private void browseWorkers() {

        clearLast();

        setListViewContent(workersListView, currentPromotion.getRoster());

        gridPane.add(workersListView, 0, 1);
        GridPane.setRowSpan(workersListView, 2);
        gridPane.add(workerOverviewPane, 1, 1);

        lastListView = workersListView;
        lastDisplayNode = workerOverviewPane;

        workersListView.getSelectionModel().selectFirst();

    }

    private void browseFreeAgents() {

        browseWorkers();
        //slight inefficient hack, updates the sets the listview content after we've already
        //set it in browseFreeAgents()
        setListViewContent(workersListView, gameController.freeAgents(gameController.playerPromotion()));

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        workersListView = new ListView<Worker>();
        eventsListView = new ListView<Event>();
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
        eventsListView.setItems(FXCollections.observableArrayList(gameController.playerPromotion().getEvents()));

        eventsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Event>() {
            @Override
            public void changed(ObservableValue<? extends Event> observable, Event oldValue, Event newValue) {
                //for switching between promotions we may get a null value
                //keep the old worker and wait until we're called again
                if (newValue != null) {

                    eventSummary.setText(newValue.getSummary());

                }

            }
        });

    }

    private void initializeMore() {
        //right now this acts as the default view for the screen
        //set whatever we want the default view to be to the lastbutton
        //so it will fire later
        lastButton = rosterButton;
        lastDisplayNode = workerOverviewPane;

        initializePromotionCombobox();

        prepareWorkerBrowsing();
        prepareEventBrowsing();

        promotionComboBox.setValue(gameController.playerPromotion());
        lastButton.fire();

    }

}
