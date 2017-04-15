package wrestling.view;

import java.io.IOException;
import java.net.URL;
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
import javafx.geometry.Insets;
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
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.view.comparators.EventDateComparator;
import wrestling.view.comparators.TitleNameComparator;
import wrestling.view.comparators.WorkerNameComparator;
import wrestling.view.comparators.WorkerPopularityComparator;

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

    public void setGameController(GameController gameController) throws IOException {
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

    private Button lastButton;

    //for keeping track of the last nodes displayed 
    //so we can find it and remove it from the gridpane before replacing it
    private Node lastDisplayNode;
    private ListView lastListView;

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
                + "\tFunds: " + currentPromotion.bankAccount().getFunds());

        //tell the workeroverviewcontroller which promotion we are looking at
        //other controllers would be notified here too if necessary
        WorkerOverviewController wo = (WorkerOverviewController) browseWorkers.controller;
        wo.setCurrentPromotion(currentPromotion);

        //this is kind of a hack but it gets the main listview
        //to display whatever was last selected (roster, events, etc.)
        //for the newly selected promotion
        //might not work for more complex situations
        lastButton.fire();

    }

    //keeps track of the last sortedlist so we can clear it when needed
    private SortedList lastSortedList;

    /*
    internal class used to handle browsing of different object types
     */
    private class BrowserMode<T> {

        private SortedList sortedList;

        private final ListView listView = new ListView<>();

        private final AnchorPane displayPane;

        private Controller controller;

        private ObservableList comparators;

        public BrowserMode(Class<T> tClass, List initialItems, String fxmlPath) throws IOException {

            //load the display pane and its controller
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(fxmlPath));
            displayPane = (AnchorPane) loader.load();

            controller = loader.getController();

            controller.setMainApp(mainApp);
            controller.setGameController(gameController);

            //get the listview ready
            listView.setItems(FXCollections.observableArrayList(initialItems));

            //listen for changes in selection on the listview
            listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<T>() {
                @Override
                public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {

                    if (newValue != null) {

                        //tell the controller what object we're looking at
                        controller.setCurrent(newValue);

                    }

                }
            });

        }

    }

    //update the sortbox to match the browse mode we are in
    private void updateSortBox(ObservableList comparators) {
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

    //update the listview according to whatever browse mode we are in
    private void setListView(BrowserMode browserMode, List list) {
        FilteredList<Worker> filteredList
                = new FilteredList<>(FXCollections.observableArrayList(list), p -> true);

        browserMode.sortedList = new SortedList<>(filteredList);

        updateSortBox(browserMode.comparators);

        browserMode.sortedList.comparatorProperty().bind(sortBox.valueProperty());

        browserMode.listView.setItems(browserMode.sortedList);
    }

    public void updateLabels() {
        setCurrentPromotion(currentPromotion);

        browseWorkers.controller.updateLabels();
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {

        if (event.getSource() == rosterButton) {

            browse(browseWorkers, currentPromotion.getFullRoster());

            lastButton = rosterButton;

        } else if (event.getSource() == eventsButton) {

            browse(browseEvents, currentPromotion.getEventArchives());

            lastButton = eventsButton;

        } else if (event.getSource() == freeAgentsButton) {

            browse(browseWorkers, gameController.freeAgents(gameController.playerPromotion()));

            //this will send the user back to the roster browsing if they switch to another promotion
            lastButton = rosterButton;
        } else if (event.getSource() == myPromotionButton) {

            setCurrentPromotion(gameController.playerPromotion());

        } else if (event.getSource() == titlesButton) {

            browse(browseTitles, currentPromotion.getTitles());

            lastButton = titlesButton;
        }
    }

    /*
    meant to be called from the event booking screen (or perhaps elsewhere)
    and shows the most recent event. right now it just selects the first one
    on the list
     */
    public void showLastEvent() {
        setCurrentPromotion(gameController.playerPromotion());
        browseEvents.listView.getSelectionModel().selectFirst();
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

    private void browse(BrowserMode browserMode, List listToBrowse) {
        clearLast();

        setListView(browserMode, listToBrowse);

        gridPane.add(browserMode.listView, 0, 1);
        GridPane.setRowSpan(browserMode.listView, 2);

        gridPane.add(browserMode.displayPane, 1, 1);
        GridPane.setRowSpan(browserMode.listView, GridPane.REMAINING);
        GridPane.setColumnSpan(browserMode.displayPane, GridPane.REMAINING);
        

        lastListView = browserMode.listView;
        lastDisplayNode = browserMode.displayPane;
        lastSortedList = browserMode.sortedList;

        browserMode.listView.getSelectionModel().selectFirst();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        categoryButton = new Label();

        stablesButton.setDisable(true);
        staffButton.setDisable(true);
        teamsButton.setDisable(true);

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

    private BrowserMode<Worker> browseWorkers;
    private BrowserMode<EventArchive> browseEvents;
    private BrowserMode<Title> browseTitles;

    private void initializeMore() throws IOException {

        initializePromotionCombobox();

        browseWorkers = new BrowserMode<>(Worker.class,
                gameController.playerPromotion().getFullRoster(),
                "view/WorkerOverview.fxml");
        browseWorkers.comparators = FXCollections.observableArrayList(new WorkerNameComparator(),
                new WorkerPopularityComparator()
        );

        //right now this acts as the default view for the screen
        //set whatever we want the default view to be to the lastbutton
        //so it will fire later
        lastButton = rosterButton;
        lastDisplayNode = browseWorkers.displayPane;

        browseEvents = new BrowserMode<>(EventArchive.class,
                gameController.playerPromotion().getEventArchives(),
                "view/SimpleDisplay.fxml");
        browseEvents.comparators = FXCollections.observableArrayList(
                new EventDateComparator()
        );

        browseTitles = new BrowserMode<>(Title.class,
                gameController.playerPromotion().getTitles(),
                "view/SimpleDisplay.fxml");
        browseTitles.comparators = FXCollections.observableArrayList(
                new TitleNameComparator()
        );

        promotionComboBox.setValue(gameController.playerPromotion());

        lastButton.fire();

    }

}
