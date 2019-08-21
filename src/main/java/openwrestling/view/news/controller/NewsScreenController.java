package openwrestling.view.news.controller;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import openwrestling.model.NewsItem;
import openwrestling.model.interfaces.iNewsItem;
import openwrestling.model.modelView.SegmentView;
import openwrestling.model.segmentEnum.NewsFilter;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.SortControl;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.comparators.NewsItemComparator;
import openwrestling.view.utility.interfaces.ControllerBase;

public class NewsScreenController extends ControllerBase implements Initializable {

    @FXML
    public ScrollPane displayPane;

    @FXML
    public ListView rankingsListView;

    @FXML
    public Button weekButton;

    @FXML
    public Button monthButton;

    @FXML
    public Button yearButton;

    @FXML
    private AnchorPane sortControlPane;

    @FXML
    public ListView<iNewsItem> newsListView;

    private List<Button> timeButtons;
    private ChronoUnit chronoUnit;
    private GameScreen sortControl;
    private SortControl sortControlController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger = LogManager.getLogger(getClass());
        timeButtons = Arrays.asList(weekButton, monthButton, yearButton);
        rankingsListView.setPlaceholder(new Label("No matches for selected time period"));
        chronoUnit = ChronoUnit.WEEKS;
    }

    @Override
    public void initializeMore() {
        updateTopMatches(ChronoUnit.WEEKS, 1);
        ViewUtils.updateSelectedButton(weekButton, timeButtons);

        sortControl = ViewUtils.loadScreenFromResource(ScreenCode.SORT_CONTROL, mainApp, gameController, sortControlPane);
        sortControlController = (SortControl) sortControl.controller;
        sortControlController.setUpdateAction(e -> {
            updateLabels();
        });
        sortControlController.setFilter(NewsFilter.ALL);
        sortControlController.setNewsMode();
        newsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<iNewsItem>() {
            @Override
            public void changed(ObservableValue<? extends iNewsItem> observable, iNewsItem oldValue, iNewsItem newValue) {
                if (newValue != null) {
                    Text text = new Text(newValue.getSummary());
                    text.wrappingWidthProperty().bind(displayPane.widthProperty());
                    displayPane.setContent(text);
                }
            }
        });

        gameController.getNewsManager().addWelcomeNewsItem(playerPromotion());
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        if (timeButtons.contains(button)) {
            if (button.equals(weekButton)) {
                chronoUnit = ChronoUnit.WEEKS;
            } else if (button.equals(monthButton)) {
                chronoUnit = ChronoUnit.MONTHS;
            } else if (button.equals(yearButton)) {
                chronoUnit = ChronoUnit.YEARS;
            }
            updateTopMatches(chronoUnit, 1);
            ViewUtils.updateSelectedButton(button, timeButtons);
        }

        updateLabels();
    }

    @Override
    public void updateLabels() {
        FilteredList filteredList = new FilteredList<>(FXCollections.observableArrayList(gameController.getNewsManager().getNewsItems()),
                p -> !((SortControl) sortControl.controller).isNewsItemFiltered(p));

        newsListView.setItems(new SortedList<>(filteredList, new NewsItemComparator()));
        newsListView.getSelectionModel().selectFirst();

    }

    public void nextDay() {
        updateTopMatches(chronoUnit, 1);
    }

    public void updateTopMatches(ChronoUnit unit, int units) {

        int matchesToShow = 10;

        if (unit.equals(ChronoUnit.WEEKS) && units == 1
                && gameController.getDateManager().today().getDayOfWeek().equals(DayOfWeek.MONDAY)) {
            units++;
        }

        List<SegmentView> topMatches
                = gameController.getSegmentManager().getTopMatches(
                        gameController.getDateManager().today(),
                        unit,
                        units,
                        matchesToShow);

        ObservableList<SegmentView> items = FXCollections.observableArrayList(topMatches);

        rankingsListView.setItems(items);

        rankingsListView.setCellFactory(param -> new ListCell<SegmentView>() {
            @Override
            protected void updateItem(SegmentView segmentView, boolean empty) {
                super.updateItem(segmentView, empty);

                if (empty || segmentView == null || !(segmentView instanceof SegmentView)) {
                    setGraphic(null);
                } else {
                    Text text = new Text(gameController.getSegmentManager().getIsolatedSegmentString(segmentView));
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
