package openwrestling.view.browser.controller;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import openwrestling.model.SegmentItem;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.TagTeam;
import openwrestling.model.segment.constants.browse.mode.BrowseMode;
import openwrestling.model.segment.constants.browse.mode.GameObjectQueryHelper;
import openwrestling.model.utility.ModelUtils;
import openwrestling.view.results.controller.ResultsCardController;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.SortControl;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;
import org.apache.logging.log4j.Level;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static openwrestling.model.segment.constants.browse.mode.BrowseMode.*;


public class BrowserController extends ControllerBase implements Initializable {

    @FXML
    private ComboBox<BrowseMode> browseModeComboBox;

    @FXML
    private GridPane gridPane;

    @FXML
    private AnchorPane imageAnchor;

    @FXML
    private Button myPromotionButton;

    @FXML
    private ComboBox<Promotion> promotionComboBox;

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

    private Promotion currentPromotion;

    private SortControl sortControlController;
    private GameObjectQueryHelper queryHelper;

    private void setCurrentPromotion(Promotion newPromotion) {
        currentPromotion = gameController.getPromotionManager().getPromotion(newPromotion.getPromotionID());
        sortControlController.setCurrentPromotion(newPromotion);

        if (currentPromotion != null) {
            imageAnchor.getChildren().clear();
            GameScreen card = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController, imageAnchor);
            ((ResultsCardController) card.controller).setWorkerInfoMode(50);
            card.controller.setCurrent(currentPromotion);

            promotionComboBox.getSelectionModel().select(currentPromotion);


            if (displaySubScreen != null && displaySubScreen.controller instanceof WorkerOverviewController) {
                ((WorkerOverviewController) displaySubScreen.controller).setPromotion(currentPromotion);
            }

            updateLabels();
        }

    }

    @Override
    public void updateLabels() {

        if (currentPromotion != null) {
            long funds = gameController.getBankAccountManager().getBankAccount(currentPromotion).getFunds();

            currentPromotionLabel.setText(currentPromotion.getName() + "\n"
                    + "Level " + currentPromotion.getLevel()
                    + "\tPopularity " + currentPromotion.getPopularity()
                    + "\tFunds: " + ModelUtils.currencyString(funds));
        }

        List<? extends SegmentItem> currentListToBrowse = currentListToBrowse();

        if (currentListToBrowse != null) {
            mainListView.setItems(sortControlController.getSortedList(currentListToBrowse));

            if (mainListView.getSelectionModel().getSelectedItem() == null && !mainListView.getItems().isEmpty()) {
                mainListView.getSelectionModel().selectFirst();
            } else if (displaySubScreen != null && mainListView.getItems().isEmpty()) {
                displaySubScreen.controller.setCurrent(null);
            }
        }

        if (displaySubScreen != null) {
            displaySubScreen.controller.updateLabels();
        }
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {

        Button button = (Button) event.getSource();

        if (button == myPromotionButton) {
            setCurrentPromotion(playerPromotion());
        }

        browse();
    }

    private void browse() {
        BrowseMode currentBrowseMode = browseModeComboBox.getValue();

        mainDisplayPane.getChildren().clear();
        displaySubScreen = ViewUtils.loadScreenFromResource(
                currentBrowseMode.getScreenCode(), mainApp, gameController, mainDisplayPane);

        if (displaySubScreen.controller instanceof StableController) {
            ((StableController) displaySubScreen.controller).setBrowseMode(currentBrowseMode);
        }

        sortControlController.setBrowseMode(currentBrowseMode);

        updateLabels();

        mainListView.getSelectionModel()
                .selectFirst();

    }

    private List<? extends SegmentItem> currentListToBrowse() {
        Promotion promotion = browseModeComboBox.getValue().equals(FREE_AGENTS)
                ? playerPromotion() : currentPromotion;
        return queryHelper.segmentItemsToBrowse(browseModeComboBox.getValue(), promotion);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        browseModeComboBox.setItems(FXCollections.observableArrayList(
                List.of(
                        WORKERS,
                        FREE_AGENTS,
                        EVENTS,
                        TAG_TEAMS,
                        STABLES,
                        ROSTER_SPLIT,
                        TITLES,
                        STAFF,
                        HIRE_STAFF
                )
        ));

        ViewUtils.lockGridPane(gridPane);

        browseModeComboBox.setValue(WORKERS);
    }

    private void initializePromotionComboBox() {
        List<Promotion> promotions = gameController.getPromotionManager().getPromotions().stream()
                .sorted(Comparator.comparing(Promotion::getPopularity))
                .collect(Collectors.toList());

        promotionComboBox.getItems().addAll(promotions);

        promotionComboBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> setCurrentPromotion(newValue));

    }

    @Override
    public void initializeMore() {
        queryHelper = new GameObjectQueryHelper(gameController);

        myPromotionButton.setText(playerPromotion().getShortName());

        try {
            initializePromotionComboBox();

            sortControl = ViewUtils.loadScreenFromResource(ScreenCode.SORT_CONTROL, mainApp, gameController, sortControlPane);
            sortControlController = (SortControl) sortControl.controller;
            sortControlController.setUpdateAction(e -> {
                updateLabels();
            });

            mainListView.getSelectionModel().selectedItemProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
                if (displaySubScreen != null && newValue != null) {
                    if (displaySubScreen.controller instanceof WorkerOverviewController && currentPromotion != null) {
                        ((WorkerOverviewController) displaySubScreen.controller).setPromotion(currentPromotion);
                    }
                    displaySubScreen.controller.setCurrent(newValue);
                }
            });

            promotionComboBox.setValue(playerPromotion());

            browseModeComboBox.setOnAction(event -> browse());
            browseModeComboBox.setValue(WORKERS);
            browse();

        } catch (Exception ex) {
            logger.log(Level.ERROR, "Error initializing broswerController", ex);
            throw new RuntimeException(ex);
        }

    }

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof EventTemplate) {
            EventTemplate template = (EventTemplate) obj;
            setCurrentPromotion(template.getPromotion());
            selectSegmentItem(EVENTS, template);
        } else if (obj instanceof TagTeam) {
            TagTeam tagTeam = (TagTeam) obj;
            if (gameController.getTagTeamManager().getTagTeams(playerPromotion()).contains(tagTeam)) {
                setCurrentPromotion(playerPromotion());
            } else {
                for (Promotion promotion : gameController.getPromotionManager().getPromotions()) {
                    if (gameController.getTagTeamManager().getTagTeams(promotion).contains(tagTeam)) {
                        setCurrentPromotion(playerPromotion());
                        break;
                    }
                }
            }
            selectSegmentItem(BrowseMode.TAG_TEAMS, tagTeam);
        } else if (obj instanceof BrowseParams) {
            BrowseParams params = (BrowseParams) obj;
            setCurrentPromotion(params.promotion);
            setBrowseMode(params.browseMode);
            sortControlController.setFilter(params.filter);
        }
    }

    private void setBrowseMode(BrowseMode browseMode) {
        browseModeComboBox.setValue(browseMode);
        browse();
        sortControlController.clearFilters();
    }

    private void selectSegmentItem(BrowseMode browseMode, SegmentItem segmentItem) {
        setBrowseMode(browseMode);
        mainListView.scrollTo(segmentItem);
        mainListView.getSelectionModel().select(segmentItem);
    }

}
