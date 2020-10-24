package openwrestling.view.news.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import openwrestling.model.gameObjects.MonthlyReview;
import openwrestling.model.gameObjects.Segment;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.interfaces.iNewsItem;
import openwrestling.model.segment.constants.browse.mode.BrowseMode;
import openwrestling.model.utility.MonthlyReviewUtils;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.SortControl;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;

import java.net.URL;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

public class NewsScreenController extends ControllerBase implements Initializable {

    @FXML
    public ScrollPane displayPane;

    @FXML
    public ListView<Segment> rankingsListView;

    @FXML
    public ListView<iNewsItem> newsListView;

    @FXML
    public Label ownerMessageText;

    @FXML
    private AnchorPane sortControlPane;

    @FXML
    private AnchorPane topListSortControlPane;

    private ChronoUnit chronoUnit;
    private SortControl sortControl;
    private SortControl topListSortControlController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger = LogManager.getLogger(getClass());
        rankingsListView.setPlaceholder(new Label("No data for selected time period"));
        chronoUnit = ChronoUnit.WEEKS;
    }

    @Override
    public void initializeMore() {


        GameScreen sortControlScreen = ViewUtils.loadScreenFromResource(ScreenCode.SORT_CONTROL, mainApp, gameController, sortControlPane);
        sortControl = (SortControl) sortControlScreen.controller;
        sortControl.setUpdateAction(e -> updateLabels());
        sortControl.setBrowseMode(BrowseMode.NEWS);
        newsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Text text = new Text(newValue.getSummary());
                text.wrappingWidthProperty().bind(displayPane.widthProperty());
                displayPane.setContent(text);
            }
        });


        GameScreen sortControl2 = ViewUtils.loadScreenFromResource(ScreenCode.SORT_CONTROL, mainApp, gameController, topListSortControlPane);
        topListSortControlController = (SortControl) sortControl2.controller;
        topListSortControlController.setUpdateAction(e -> updateLabels());
        topListSortControlController.setBrowseMode(BrowseMode.MATCHES);
        updateTopMatches();
    }

    @Override
    public void updateLabels() {
        List news = BrowseMode.NEWS.listToBrowse(gameController, playerPromotion());

        newsListView.setItems(sortControl.getSortedList(news));
        newsListView.getSelectionModel().selectFirst();
        ownerMessageText.setText(getOwnerMessageText());

        updateTopMatches();
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
        updateTopMatches();
    }

    public void updateTopMatches() {
        List matches = BrowseMode.MATCHES.listToBrowse(gameController, playerPromotion());
        rankingsListView.setItems(topListSortControlController.getSortedList(matches));
        rankingsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Segment segment, boolean empty) {
                super.updateItem(segment, empty);

                if (empty || segment == null) {
                    setGraphic(null);
                } else {
                    Text text = new Text(gameController.getSegmentStringService().getIsolatedSegmentString(segment, segment.getEvent()));
                    text.wrappingWidthProperty().bind(rankingsListView.widthProperty());
                    text.setTextAlignment(TextAlignment.CENTER);
                    setGraphic(text);
                }
            }
        });
    }

    public void addNews(iNewsItem newsItem) {
        newsListView.getItems().add(0, newsItem);
    }

}
