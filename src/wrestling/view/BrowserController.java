package wrestling.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import wrestling.model.Event;
import wrestling.model.Promotion;
import wrestling.model.TagTeam;
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.view.comparators.EventDateComparator;
import wrestling.view.comparators.TagTeamNameComparator;
import wrestling.view.comparators.TitleNameComparator;
import wrestling.view.comparators.WorkerNameComparator;
import wrestling.view.comparators.WorkerPopularityComparator;
import wrestling.view.utility.BrowserMode;
import wrestling.view.utility.ViewUtils;

/**
 *
 * main browser, to be used for checking data for almost everything
 */
public class BrowserController extends ControllerBase implements Initializable {

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

    private List<Button> browseButtons;

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

    //keeps track of the last sortedlist so we can clear it when needed
    private SortedList lastSortedList;

    private BrowserMode<Worker> browseWorkers;
    private BrowserMode<Event> browseEvents;
    private BrowserMode<Title> browseTitles;
    private BrowserMode<TagTeam> browseTeams;

    /*
    sets the current promotion
    updates relevant labels
    fires the last button to update the list
     */
    private void setCurrentPromotion(Promotion newPromotion) {
        this.currentPromotion = newPromotion;

        categoryButton.setText(newPromotion.toString());

        //make sure the combobox is on the correct promotion
        //in case we have called this from somewhere programatically
        promotionComboBox.getSelectionModel().select(currentPromotion);

        currentPromotionLabel.setText(currentPromotion.getName() + "\n"
                + "Level " + currentPromotion.getLevel()
                + "\tPopularity " + currentPromotion.getPopulatirty()
                + "\tFunds: " + gameController.getPromotionManager().getBankAccount(currentPromotion).getFunds());

        //tell the workeroverviewcontroller which promotion we are looking at
        //other controllers would be notified here too if necessary
        WorkerOverviewController wo = (WorkerOverviewController) browseWorkers.getController();
        wo.setCurrentPromotion(currentPromotion);

        //this is kind of a hack but it gets the main listview
        //to display whatever was last selected (roster, events, etc.)
        //for the newly selected promotion
        //might not work for more complex situations
        lastButton.fire();

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

        browserMode.setSortedList(new SortedList<>(new FilteredList<>(FXCollections.observableArrayList(list), p -> true)));

        updateSortBox(browserMode.getComparators());

        browserMode.getSortedList().comparatorProperty().bind(sortBox.valueProperty());
    }

    @Override
    public void updateLabels() {
        setCurrentPromotion(currentPromotion);

        browseWorkers.getController().updateLabels();
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {

        if (event.getSource() == rosterButton) {

            browse(browseWorkers, gameController.getContractManager().getFullRoster(currentPromotion));
            updateSelectedButton(rosterButton);
            lastButton = rosterButton;

        } else if (event.getSource() == eventsButton) {

            browse(browseEvents, gameController.getEventManager().getEvents(currentPromotion));
            updateSelectedButton(eventsButton);
            lastButton = eventsButton;

        } else if (event.getSource() == freeAgentsButton) {

            browse(browseWorkers, gameController.getWorkerManager().freeAgents(playerPromotion()));
            updateSelectedButton(freeAgentsButton);
            //this will send the user back to the roster browsing if they switch to another promotion
            lastButton = rosterButton;
        } else if (event.getSource() == myPromotionButton) {
            updateSelectedButton(myPromotionButton);
            setCurrentPromotion(playerPromotion());

        } else if (event.getSource() == titlesButton) {
            updateSelectedButton(titlesButton);
            browse(browseTitles, gameController.getTitleManager().getTitles(currentPromotion));

            lastButton = titlesButton;
        } else if (event.getSource() == teamsButton) {
            updateSelectedButton(teamsButton);
            browse(browseTeams, gameController.getTagTeamManager().getTagTeams(currentPromotion));
        }
    }

    private void updateSelectedButton(Button button) {

        String selectedButtonClass = "selectedButton";

        browseButtons.stream().filter((b) -> (b.getStyleClass().contains(selectedButtonClass))).forEach((b) -> {
            b.getStyleClass().remove(selectedButtonClass);
        });

        if (currentPromotion.equals(playerPromotion()) && !button.equals(freeAgentsButton)) {
            myPromotionButton.getStyleClass().add(selectedButtonClass);
        }

        button.getStyleClass().add(selectedButtonClass);

    }

    /*
    meant to be called from the event booking screen (or perhaps elsewhere)
    and shows the most recent event. right now it just selects the first one
    on the list
     */
    public void showLastEvent() {
        setCurrentPromotion(playerPromotion());
        browseEvents.getListView().getSelectionModel().selectFirst();
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

        placeListView(browserMode);

        placeDispalyPane(browserMode);

        lastListView = browserMode.getListView();
        lastDisplayNode = browserMode.getDisplayPane();
        lastSortedList = browserMode.getSortedList();

        browserMode.getListView().getSelectionModel().selectFirst();
    }

    private void placeListView(BrowserMode browserMode) {
        gridPane.add(browserMode.getListView(), 0, 1);
        GridPane.setRowSpan(browserMode.getListView(), GridPane.REMAINING);
        GridPane.setColumnSpan(browserMode.getListView(), 1);
    }

    private void placeDispalyPane(BrowserMode browserMode) {
        gridPane.add(browserMode.getDisplayPane(), 1, 1);
        GridPane.setColumnSpan(browserMode.getDisplayPane(), GridPane.REMAINING);
        GridPane.setRowSpan(browserMode.getDisplayPane(), GridPane.REMAINING);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        logger = LogManager.getLogger(this.getClass());

        this.browseButtons = new ArrayList<>(Arrays.asList(
                eventsButton, freeAgentsButton, myPromotionButton, rosterButton,
                stablesButton, staffButton, teamsButton, titlesButton
        ));

        ViewUtils.lockGridPane(gridPane);

        categoryButton = new Label();

        stablesButton.setDisable(true);
        staffButton.setDisable(true);

    }

    private void initializePromotionCombobox() {

        //set up the promotion combobox
        promotionComboBox.getItems().addAll(gameController.getPromotionManager().getPromotions());

        // show the promotion acronym
        Callback cellFactory = (Callback<ListView<Promotion>, ListCell<Promotion>>) (ListView<Promotion> p) -> new ListCell<Promotion>() {

            @Override
            protected void updateItem(Promotion item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getShortName());
                }
            }
        };

        promotionComboBox.setCellFactory(cellFactory);
        promotionComboBox.setButtonCell((ListCell) cellFactory.call(null));

        promotionComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Promotion>() {
            @Override
            public void changed(ObservableValue<? extends Promotion> observable, Promotion oldValue, Promotion newValue) {
                setCurrentPromotion(newValue);

            }
        });

    }

    @Override
    public void initializeMore() {
        try {
            initializePromotionCombobox();

            browseWorkers = new BrowserMode<>(
                    gameController.getContractManager().getFullRoster(playerPromotion()),
                    "view/WorkerOverview.fxml",
                    FXCollections.observableArrayList(new WorkerNameComparator(),
                            new WorkerPopularityComparator()),
                    mainApp,
                    gameController);

            //right now this acts as the default view for the screen
            //set whatever we want the default view to be to the lastbutton
            //so it will fire later
            lastButton = rosterButton;
            lastDisplayNode = browseWorkers.getDisplayPane();

            browseEvents = new BrowserMode<>(
                    gameController.getEventManager().getEvents(playerPromotion()),
                    "view/SimpleDisplay.fxml",
                    FXCollections.observableArrayList(
                            new EventDateComparator()
                    ),
                    mainApp,
                    gameController
            );

            browseTitles = new BrowserMode<>(
                    gameController.getTitleManager().getTitles(playerPromotion()),
                    "view/SimpleDisplay.fxml", FXCollections.observableArrayList(
                            new TitleNameComparator()
                    ),
                    mainApp,
                    gameController);

            browseTeams = new BrowserMode<>(
                    gameController.getTagTeamManager().getTagTeams(playerPromotion()),
                    "view/SimpleDisplay.fxml", FXCollections.observableArrayList(
                            new TagTeamNameComparator()
                    ),
                    mainApp,
                    gameController);

            promotionComboBox.setValue(playerPromotion());

            lastButton.fire();
        } catch (Exception ex) {
            logger.log(Level.ERROR, "Error initializing broswerController", ex);
        }

    }

}
