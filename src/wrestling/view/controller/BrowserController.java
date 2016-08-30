package wrestling.view.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
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
    private Button eventsButton;

    @FXML
    private Pane mainListViewPane;

    @FXML
    private ComboBox promotionComboBox;

    @FXML
    private BorderPane displayPane;

    @FXML
    private Pane categoryPane;

    private Button lastButton;
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

        categoryPane.getChildren().clear();
        categoryPane.getChildren().add(categoryButton);

        //this is kind of a hack but it gets the main listview
        //to display whatever was last selected (roster, events, etc.)
        //for the newly selected promotion
        //might not work for more complex situations
        lastButton.fire();
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {

        if (event.getSource() == rosterButton) {

            setListViewToWorkers();
            displayPane.setCenter(workerOverviewPane);
            //displayPane = workerOverviewPane;
            lastButton = rosterButton;
        } else if (event.getSource() == eventsButton) {
            //displayPane.setCenter(null);
            setListViewToEvents();
            displayPane.setCenter(eventSummary);
            lastButton = eventsButton;
        }
    }

    private void setListViewToEvents() {
        mainListViewPane.getChildren().clear();

        eventsListView.setItems(FXCollections.observableArrayList(currentPromotion.events));

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
        eventsListView.getSelectionModel().selectFirst();
        mainListViewPane.getChildren().add(eventsListView);
    }

    public void showEvent(Event event) {
        //this would take an event, find the promotion, select it properly
        //ie so another screen can send to the browser with a particular event already selected on open
        //might want this with workers, etc
        eventsButton.fire();
        eventsListView.getSelectionModel().select(event);
    }

    private void setListViewToWorkers() {

        mainListViewPane.getChildren().clear();

        workersListView.setItems(FXCollections.observableArrayList(currentPromotion.roster));
        workersListView.getSelectionModel().selectFirst();
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

        mainListViewPane.getChildren().add(workersListView);

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        workersListView = new ListView<Worker>();
        eventsListView = new ListView<Event>();
        eventSummary = new Label();
        categoryButton = new Label();
        
        
    }

    private void initializeMore() {
        //right now this acts as the default view for the screen
        //set whatever we want the default view to be to the lastbutton
        //so it will fire later
        lastButton = rosterButton;

        //set up the promotion combobox
        promotionComboBox.getItems().addAll(gameController.promotions);
        promotionComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Promotion>() {
            @Override
            public void changed(ObservableValue<? extends Promotion> observable, Promotion oldValue, Promotion newValue) {
                setCurrentPromotion(newValue);
            }
        });

        //load the workeroverview pane (should be its own method)
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/fxml/WorkerOverview.fxml"));
            workerOverviewPane = (AnchorPane) loader.load();

            workerOverviewPaneController = (WorkerOverviewController) loader.getController();
            System.out.println("in browserController init more" + loader.getController().toString());
            workerOverviewPaneController.setMainApp(this.mainApp);
            workerOverviewPaneController.setGameController(this.gameController);

            displayPane.setCenter(workerOverviewPane);

        } catch (IOException e) {
            e.printStackTrace();
        }

        promotionComboBox.setValue(gameController.playerPromotion());
        lastButton.fire();

    }

}
