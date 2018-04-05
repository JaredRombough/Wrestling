package wrestling.view.browser;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import wrestling.model.Promotion;
import wrestling.view.utility.SortControlController;
import wrestling.view.utility.Screen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.comparators.EventDateComparator;
import wrestling.view.utility.comparators.TagTeamNameComparator;
import wrestling.view.utility.comparators.TitleNameComparator;
import wrestling.view.utility.comparators.WorkerNameComparator;
import wrestling.view.utility.comparators.WorkerPopularityComparator;
import wrestling.view.utility.interfaces.ControllerBase;

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
    private AnchorPane sortControlPane;

    @FXML
    private ListView mainListView;

    @FXML
    private AnchorPane mainDisplayPane;
    private Screen displaySubScreen;

    private Screen sortControl;

    private Label categoryButton;

    private Promotion currentPromotion;

    private BrowseMode currentBrowseMode;

    private enum BrowseMode {
        FREE_AGENTS,
        WORKERS,
        TITLES,
        TAG_TEAMS,
        EVENTS
    }

    /*
    sets the current promotion
    updates relevant labels
    fires the last button to update the list
     */
    private void setCurrentPromotion(Promotion newPromotion) {
        this.currentPromotion = newPromotion;

        if (currentPromotion != null) {
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
            if (displaySubScreen != null && displaySubScreen.controller instanceof WorkerOverviewController) {
                ((WorkerOverviewController) displaySubScreen.controller).setPromotion(currentPromotion);
            }

            updateLabels();
        }

    }

    @Override
    public void updateLabels() {
        if (currentListToBrowse() != null) {
            mainListView.setItems(new SortedList<>(new FilteredList<>(FXCollections.observableArrayList(currentListToBrowse()), p -> true),
                    sortControl != null ? ((SortControlController) sortControl.controller).getCurrentComparator() : null));
            if (mainListView.getSelectionModel().getSelectedItem() == null && !mainListView.getItems().isEmpty()) {
                mainListView.getSelectionModel().selectFirst();
            }
        }

    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {

        Button button = (Button) event.getSource();

        if (button == rosterButton) {
            currentBrowseMode = BrowseMode.WORKERS;
        } else if (button == eventsButton) {
            currentBrowseMode = BrowseMode.EVENTS;
        } else if (button == freeAgentsButton) {
            currentBrowseMode = BrowseMode.FREE_AGENTS;
        } else if (button == myPromotionButton) {
            setCurrentPromotion(playerPromotion());
        } else if (button == titlesButton) {
            currentBrowseMode = BrowseMode.TITLES;
        } else if (button == teamsButton) {
            currentBrowseMode = BrowseMode.TAG_TEAMS;
        }

        if (!button.equals(myPromotionButton)) {
            ViewUtils.updateSelectedButton(button, browseButtons);
        }

        browse();
    }

    private void browse() {

        ScreenCode subScreenCode = ScreenCode.SIMPLE_DISPLAY;
        ObservableList comparators = null;

        switch (currentBrowseMode) {

            case WORKERS:
                comparators = FXCollections.observableArrayList(
                        new WorkerNameComparator(),
                        new WorkerPopularityComparator());
                subScreenCode = ScreenCode.WORKER_OVERVIEW;
                break;
            case FREE_AGENTS:
                comparators = FXCollections.observableArrayList(
                        new WorkerNameComparator(),
                        new WorkerPopularityComparator());
                subScreenCode = ScreenCode.WORKER_OVERVIEW;
                break;
            case EVENTS:
                comparators = FXCollections.observableArrayList(
                        new EventDateComparator());
                break;
            case TITLES:
                comparators = FXCollections.observableArrayList(
                        new TitleNameComparator());
                break;
            case TAG_TEAMS:
                comparators = FXCollections.observableArrayList(
                        new TagTeamNameComparator());
                break;
        }

        mainDisplayPane.getChildren().clear();
        displaySubScreen = ViewUtils.loadScreenFromResource(subScreenCode, mainApp, gameController, mainDisplayPane);

        sortControl.controller.setCurrent(comparators);

        SortedList sortedList = new SortedList<>(new FilteredList<>(FXCollections.observableArrayList(currentListToBrowse()), p -> true),
                sortControl != null ? ((SortControlController) sortControl.controller).getCurrentComparator() : null);

        mainListView.setItems(sortedList);

        mainListView.getSelectionModel()
                .selectFirst();

    }

    private List currentListToBrowse() {
        List listToBrowse = null;
        switch (currentBrowseMode) {

            case WORKERS:
                listToBrowse = gameController.getContractManager().getFullRoster(currentPromotion);
                break;
            case FREE_AGENTS:
                listToBrowse = gameController.getWorkerManager().freeAgents(playerPromotion());
                break;
            case EVENTS:
                listToBrowse = gameController.getEventManager().getEvents(currentPromotion);
                break;
            case TITLES:
                listToBrowse = gameController.getTitleManager().getTitles(currentPromotion);
                break;
            case TAG_TEAMS:
                listToBrowse = gameController.getTagTeamManager().getTagTeams(currentPromotion);
                break;
        }

        return listToBrowse;
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

        currentBrowseMode = BrowseMode.WORKERS;

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

            sortControl = ViewUtils.loadScreenFromResource(ScreenCode.SORT_CONTROL, mainApp, gameController, sortControlPane);
            ((SortControlController) sortControl.controller).setParentScreenCode(ScreenCode.BROWSER);

            mainListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
                    if (displaySubScreen != null && newValue != null) {
                        if (displaySubScreen.controller instanceof WorkerOverviewController && currentPromotion != null) {
                            ((WorkerOverviewController) displaySubScreen.controller).setPromotion(currentPromotion);
                        }
                        displaySubScreen.controller.setCurrent(newValue);
                    }
                }
            });

            promotionComboBox.setValue(playerPromotion());

            rosterButton.fire();

        } catch (Exception ex) {
            logger.log(Level.ERROR, "Error initializing broswerController", ex);
        }

    }

}
