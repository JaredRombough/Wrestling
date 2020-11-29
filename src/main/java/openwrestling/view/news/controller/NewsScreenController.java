package openwrestling.view.news.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import openwrestling.model.NewsItem;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.GameObject;
import openwrestling.model.gameObjects.MonthlyReview;
import openwrestling.model.gameObjects.Segment;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.interfaces.iDate;
import openwrestling.model.segment.constants.browse.mode.BrowseMode;
import openwrestling.model.segment.constants.browse.mode.GameObjectQueryHelper;
import openwrestling.model.utility.ModelUtils;
import openwrestling.model.utility.MonthlyReviewUtils;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.SortControl;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static java.time.temporal.ChronoUnit.DAYS;
import static openwrestling.model.segment.constants.browse.mode.BrowseMode.*;

public class NewsScreenController extends ControllerBase implements Initializable {

    @FXML
    public ScrollPane displayPane;

    @FXML
    public ListView<GameObject> topListView;

    @FXML
    public ListView newsListView;

    @FXML
    public Label ownerMessageText;

    @FXML
    private AnchorPane sortControlPane;

    @FXML
    private AnchorPane topListSortControlPane;

    @FXML
    private ComboBox<BrowseMode> newsFeedBrowseMode;

    @FXML
    private ComboBox<BrowseMode> topListBrowseMode;

    private SortControl sortControl;
    private SortControl topListSortControlController;
    private GameObjectQueryHelper queryHelper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger = LogManager.getLogger(getClass());
        topListView.setPlaceholder(new Label("No data for selected time period"));
    }

    @Override
    public void initializeMore() {
        queryHelper = new GameObjectQueryHelper(gameController);

        newsFeedBrowseMode.setItems(FXCollections.observableList(List.of(
                PAST_EVENTS,
                CONTRACTS_SIGNED,
                CONTRACTS_EXPIRED,
                NEWS
        )));
        newsFeedBrowseMode.setValue(PAST_EVENTS);
        newsFeedBrowseMode.setOnAction((event) -> {
            if (newsFeedBrowseMode.getValue() != null) {
                sortControl.setBrowseMode(newsFeedBrowseMode.getValue());
                updateLabels();
            }
        });

        GameScreen sortControlScreen = ViewUtils.loadScreenFromResource(ScreenCode.SORT_CONTROL, mainApp, gameController, sortControlPane);
        sortControl = (SortControl) sortControlScreen.controller;
        sortControl.setBrowseMode(PAST_EVENTS);
        sortControl.setUpdateAction(e -> updateLabels());
        newsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Text text = new Text();
                String string = "";
                if (newValue instanceof DateRow) {
                    string = ModelUtils.dateString(((DateRow) newValue).getDate());
                } else if (newValue instanceof Event) {
                    string = gameController.getSegmentStringService().generateSummaryString((Event) newValue);
                    text.setTextAlignment(TextAlignment.CENTER);
                } else if (newValue instanceof NewsItem) {
                    string = ((NewsItem) newValue).getSummary();
                } else if (newValue instanceof Contract) {
                    Contract contract = (Contract) newValue;
                    string = contract.isActive() ?
                            String.format("%s has signed a new contract with %s, starting %s. It expires on %s.",
                                    contract.getWorker().getName(),
                                    contract.getPromotion().getName(),
                                    ModelUtils.dateString(contract.getStartDate()),
                                    ModelUtils.dateString(contract.getEndDate())) :
                            String.format("The contract between %s and %s has expired as of %s.",
                                    contract.getWorker().getName(),
                                    contract.getPromotion().getName(),
                                    ModelUtils.dateString(contract.getEndDate()));
                }
                text.setText(string);
                text.wrappingWidthProperty().bind(displayPane.widthProperty());
                displayPane.setContent(text);
            }
        });
        newsListView.setCellFactory(lv -> new ListCell<>() {

            @Override
            public void updateItem(final Object object, boolean empty) {
                super.updateItem(object, empty);
                getStyleClass().remove("grey-background");
                if (object instanceof DateRow) {
                    getStyleClass().add("grey-background");
                }
                if (object != null) {
                    setText(object.toString());
                }
            }
        });

        topListBrowseMode.setItems(FXCollections.observableArrayList(List.of(
                BrowseMode.MATCHES,
                BrowseMode.TOP_POPULARITY,
                BrowseMode.TOP_CHARISMA,
                BrowseMode.TOP_WORKRATE,
                BrowseMode.TOP_STRIKING,
                BrowseMode.TOP_WRESTLING,
                BrowseMode.TOP_FLYING,
                BrowseMode.TOP_POPULARITY_MEN,
                BrowseMode.TOP_STRIKING_MEN,
                BrowseMode.TOP_WRESTLING_MEN,
                BrowseMode.TOP_FLYING_MEN,
                BrowseMode.TOP_CHARISMA_MEN,
                BrowseMode.TOP_WORKRATE_MEN,
                BrowseMode.TOP_POPULARITY_WOMEN,
                BrowseMode.TOP_STRIKING_WOMEN,
                BrowseMode.TOP_WRESTLING_WOMEN,
                BrowseMode.TOP_FLYING_WOMEN,
                BrowseMode.TOP_CHARISMA_WOMEN,
                BrowseMode.TOP_WORKRATE_WOMEN
        )));
        topListBrowseMode.setValue(BrowseMode.MATCHES);
        topListBrowseMode.setOnAction((event) -> {
            if (topListBrowseMode.getValue() != null) {
                topListSortControlController.setBrowseMode(topListBrowseMode.getValue());
                updateTopList();
            }
        });

        GameScreen sortControl2 = ViewUtils.loadScreenFromResource(ScreenCode.SORT_CONTROL, mainApp, gameController, topListSortControlPane);
        topListSortControlController = (SortControl) sortControl2.controller;
        topListSortControlController.setBrowseMode(BrowseMode.MATCHES);
        topListSortControlController.setUpdateAction(e -> updateLabels());
        updateTopList();
    }

    @Override
    public void updateLabels() {

        List rawList = queryHelper.listToBrowse(newsFeedBrowseMode.getValue(), playerPromotion());
        List listWithDateRows = new ArrayList<>();

        LocalDate lastDate = null;
        for (Object event : rawList) {
            if (event instanceof iDate) {
                LocalDate date = ((iDate) event).getDate();
                if (!date.equals(lastDate)) {
                    lastDate = date;
                    long daysAgo = DAYS.between(date, gameController.getDateManager().today());
                    String dateString = daysAgo == 1 ? "Yesterday" : String.format("%d days ago", daysAgo);
                    DateRow dateRow = new DateRow(date, dateString);
                    listWithDateRows.add(dateRow);
                }
            }
            listWithDateRows.add(event);
        }

        newsListView.setItems(FXCollections.observableList(listWithDateRows));
        newsListView.getSelectionModel().selectFirst();
        ownerMessageText.setText(getOwnerMessageText());

        updateTopList();
    }

    private String getOwnerMessageText() {
        StaffMember owner = gameController.getStaffManager().getOwner(playerPromotion());
        if (owner == null) {
            return "";
        }
        List<MonthlyReview> monthlyReviews = gameController.getMonthlyReviewManager().getSortedReviews();

        if (CollectionUtils.isEmpty(monthlyReviews)) {
            return String.format("%s will be reviewing your performance monthly.", owner.getName());
        }

        if (monthlyReviews.size() == 1) {
            return String.format("%s will be reviewing your performance monthly.", owner.getName());
        }

        return String.format("%s\n%s", MonthlyReviewUtils.fundsString(monthlyReviews, owner.getName()),
                MonthlyReviewUtils.popularityString(monthlyReviews, owner.getName()));
    }

    public void nextDay() {
        updateTopList();
    }

    public void updateTopList() {
        List list = queryHelper.listToBrowse(topListBrowseMode.getValue(), playerPromotion());
        topListView.setItems(topListSortControlController.getSortedList(list));
        topListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(GameObject gameObject, boolean empty) {
                super.updateItem(gameObject, empty);

                if (empty || gameObject == null) {
                    setGraphic(null);
                } else {
                    if (gameObject instanceof Worker) {
                        Worker worker = (Worker) gameObject;
                        GameScreen topWorkerScreen = ViewUtils.loadScreenFromFXML(ScreenCode.TOP_WORKER, mainApp, gameController);
                        TopWorkerController cotroller = (TopWorkerController) topWorkerScreen.controller;
                        cotroller.setWorker(topListBrowseMode.getValue(), worker, topListView.getItems().indexOf(worker) + 1);
                        setGraphic(topWorkerScreen.pane);
                    } else if (gameObject instanceof Segment) {
                        Segment segment = (Segment) gameObject;
                        String string = gameController.getSegmentStringService().getIsolatedSegmentString(segment, segment.getEvent());
                        Text text = new Text(string);
                        text.wrappingWidthProperty().bind(topListView.widthProperty());
                        text.setTextAlignment(TextAlignment.CENTER);
                        setGraphic(text);
                    }

                }
            }
        });
    }

    public void addNews(NewsItem newsItem) {
        newsListView.getItems().add(0, newsItem);
    }

}
