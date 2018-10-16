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
import wrestling.model.EventTemplate;
import wrestling.model.SegmentItem;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.TagTeamView;
import wrestling.model.segmentEnum.BrowseMode;
import wrestling.view.utility.GameScreen;
import wrestling.view.utility.RefreshSkin;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.SortControl;
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
    private Button hireStaffButton;

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
    private GameScreen displaySubScreen;

    private GameScreen sortControl;

    private Label categoryButton;

    private PromotionView currentPromotion;

    private BrowseMode currentBrowseMode;
    private SortControl sortControlController;

    /*
    sets the current promotion
    updates relevant labels
    fires the last button to update the list
     */
    private void setCurrentPromotion(PromotionView newPromotion) {
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

            Comparator comparator = sortControl != null ? ((SortControl) sortControl.controller).getCurrentComparator() : null;
            FilteredList filteredList = new FilteredList<>(FXCollections.observableArrayList(currentListToBrowse), p
                    -> !((SortControl) sortControl.controller).isFiltered(p));

            mainListView.setItems(new SortedList<>(filteredList, comparator));

            if (mainListView.getSelectionModel().getSelectedItem() == null && !mainListView.getItems().isEmpty()) {
                mainListView.getSelectionModel().selectFirst();
            } else if (mainListView.getItems().isEmpty()) {
                displaySubScreen.controller.setCurrent(null);
            }
        }

        if (displaySubScreen != null) {
            displaySubScreen.controller.updateLabels();
        }
        ((RefreshSkin) mainListView.getSkin()).refresh();

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

        sortControl.controller.setCurrent(currentBrowseMode);

        updateLabels();

        mainListView.getSelectionModel()
                .selectFirst();

    }

    private List currentListToBrowse() {
        PromotionView promotion = currentBrowseMode.equals(BrowseMode.FREE_AGENTS)
                ? playerPromotion() : currentPromotion;
        return currentBrowseMode.listToBrowse(gameController, promotion);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        logger = LogManager.getLogger(this.getClass());

        this.browseButtons = new ArrayList<>(Arrays.asList(
                eventsButton, freeAgentsButton, myPromotionButton, rosterButton,
                stablesButton, staffButton, teamsButton, titlesButton, hireStaffButton
        ));

        rosterButton.setId(BrowseMode.WORKERS.name());
        freeAgentsButton.setId(BrowseMode.FREE_AGENTS.name());
        eventsButton.setId(BrowseMode.EVENTS.name());
        teamsButton.setId(BrowseMode.TAG_TEAMS.name());
        titlesButton.setId(BrowseMode.TITLES.name());
        staffButton.setId(BrowseMode.STAFF.name());
        hireStaffButton.setId(BrowseMode.HIRE_STAFF.name());

        ViewUtils.lockGridPane(gridPane);

        categoryButton = new Label();

        stablesButton.setDisable(true);

        currentBrowseMode = BrowseMode.WORKERS;

    }

    private void initializePromotionCombobox() {

        //set up the promotion combobox
        promotionComboBox.getItems().addAll(gameController.getPromotionManager().getPromotions());

        // show the promotion acronym
        Callback cellFactory = (Callback<ListView<PromotionView>, ListCell<PromotionView>>) (ListView<PromotionView> p) -> new ListCell<PromotionView>() {

            @Override
            protected void updateItem(PromotionView item, boolean empty) {
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

        promotionComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PromotionView>() {
            @Override
            public void changed(ObservableValue<? extends PromotionView> observable, PromotionView oldValue, PromotionView newValue) {
                setCurrentPromotion(newValue);

            }
        });

    }

    @Override
    public void initializeMore() {
        try {
            initializePromotionCombobox();

            sortControl = ViewUtils.loadScreenFromResource(ScreenCode.SORT_CONTROL, mainApp, gameController, sortControlPane);
            ((SortControl) sortControl.controller).setParentScreenCode(ScreenCode.BROWSER);
            sortControlController = (SortControl) sortControl.controller;

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

            RefreshSkin skin = new RefreshSkin(mainListView);
            mainListView.setSkin(skin);

            promotionComboBox.setValue(playerPromotion());

            rosterButton.fire();

        } catch (Exception ex) {
            logger.log(Level.ERROR, "Error initializing broswerController", ex);
        }

    }

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof EventTemplate) {
            EventTemplate template = (EventTemplate) obj;
            setCurrentPromotion(template.getPromotion());
            selectSegmentItem(BrowseMode.EVENTS, template);
        } else if (obj instanceof TagTeamView) {
            TagTeamView tagTeamView = (TagTeamView) obj;
            if (gameController.getTagTeamManager().getTagTeamViews(playerPromotion()).contains(tagTeamView)) {
                setCurrentPromotion(playerPromotion());
            } else {
                for (PromotionView promotion : gameController.getPromotionManager().getPromotions()) {
                    if (gameController.getTagTeamManager().getTagTeamViews(promotion).contains(tagTeamView)) {
                        setCurrentPromotion(playerPromotion());
                        break;
                    }
                }
            }
            selectSegmentItem(BrowseMode.TAG_TEAMS, tagTeamView);
        } else if (obj instanceof BrowseParams) {
            BrowseParams params = (BrowseParams) obj;
            setCurrentPromotion(params.promotion);
            setBrowseMode(params.broseMode);
            sortControlController.setFilter(params.filter);
        }
    }

    private void setBrowseMode(BrowseMode browseMode) {
        currentBrowseMode = browseMode;
        ViewUtils.updateSelectedButton(
                browseButtons.stream().filter(button -> button.getId().equals(browseMode.name())).findFirst().get(),
                browseButtons);
        browse();
        sortControlController.clearFilters();
    }

    private void selectSegmentItem(BrowseMode browseMode, SegmentItem segmentItem) {
        setBrowseMode(browseMode);
        mainListView.scrollTo(segmentItem);
        mainListView.getSelectionModel().select(segmentItem);
    }

}
