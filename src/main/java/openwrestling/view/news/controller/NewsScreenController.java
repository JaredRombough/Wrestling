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
import openwrestling.model.gameObjects.GameObject;
import openwrestling.model.gameObjects.MonthlyReview;
import openwrestling.model.gameObjects.Segment;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segment.constants.browse.mode.BrowseMode;
import openwrestling.model.segment.constants.browse.mode.GameObjectQueryHelper;
import openwrestling.model.utility.MonthlyReviewUtils;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.SortControl;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class NewsScreenController extends ControllerBase implements Initializable {

    @FXML
    public ScrollPane displayPane;

    @FXML
    public ListView<GameObject> topListView;

    @FXML
    public ListView<NewsItem> newsListView;

    @FXML
    public Label ownerMessageText;

    @FXML
    private AnchorPane sortControlPane;

    @FXML
    private AnchorPane topListSortControlPane;

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

        GameScreen sortControlScreen = ViewUtils.loadScreenFromResource(ScreenCode.SORT_CONTROL, mainApp, gameController, sortControlPane);
        sortControl = (SortControl) sortControlScreen.controller;
        sortControl.setBrowseMode(BrowseMode.NEWS);
        sortControl.setUpdateAction(e -> updateLabels());
        newsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Text text = new Text(newValue.getSummary());
                text.wrappingWidthProperty().bind(displayPane.widthProperty());
                displayPane.setContent(text);
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
        List news = queryHelper.listToBrowse(BrowseMode.NEWS, playerPromotion());

        newsListView.setItems(sortControl.getSortedList(news));
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
