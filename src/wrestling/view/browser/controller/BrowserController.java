package wrestling.view.browser.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import wrestling.model.segmentEnum.BrowseMode;
import wrestling.view.utility.SortControlController;
import wrestling.view.utility.Screen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
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

        List currentListToBrowse = currentListToBrowse();
        if (currentListToBrowse != null) {

            Comparator comparator = sortControl != null ? ((SortControlController) sortControl.controller).getCurrentComparator() : null;
            FilteredList filteredList = new FilteredList<>(FXCollections.observableArrayList(currentListToBrowse), p
                    -> !((SortControlController) sortControl.controller).isFiltered(p));

            mainListView.setItems(new SortedList<>(filteredList, comparator));

            if (mainListView.getSelectionModel().getSelectedItem() == null && !mainListView.getItems().isEmpty()) {
                mainListView.getSelectionModel().selectFirst();
            }
        }

    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {

        Button button = (Button) event.getSource();

        if (button == myPromotionButton) {
            setCurrentPromotion(playerPromotion());
        } else {
            currentBrowseMode = BrowseMode.valueOf(button.getId());
            ViewUtils.updateSelectedButton(button, browseButtons);
        }

        browse();
    }

    private void browse() {

        mainDisplayPane.getChildren().clear();
        displaySubScreen = ViewUtils.loadScreenFromResource(
                currentBrowseMode.subScreenCode(), mainApp, gameController, mainDisplayPane);

        sortControl.controller.setCurrent(currentBrowseMode.comparators());

        updateLabels();

        mainListView.getSelectionModel()
                .selectFirst();

    }

    private List currentListToBrowse() {
        Promotion promotion = currentBrowseMode.equals(BrowseMode.FREE_AGENTS)
                ? playerPromotion() : currentPromotion;
        return currentBrowseMode.listToBrowse(gameController, promotion);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        logger = LogManager.getLogger(this.getClass());

        this.browseButtons = new ArrayList<>(Arrays.asList(
                eventsButton, freeAgentsButton, myPromotionButton, rosterButton,
                stablesButton, staffButton, teamsButton, titlesButton
        ));

        rosterButton.setId(BrowseMode.WORKERS.name());
        freeAgentsButton.setId(BrowseMode.WORKERS.name());
        eventsButton.setId(BrowseMode.EVENTS.name());
        teamsButton.setId(BrowseMode.TAG_TEAMS.name());
        titlesButton.setId(BrowseMode.TITLES.name());

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
