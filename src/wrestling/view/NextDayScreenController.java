package wrestling.view;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.apache.logging.log4j.LogManager;
import wrestling.model.Injury;
import wrestling.model.NewsItem;
import wrestling.model.modelView.SegmentView;
import wrestling.view.browser.controller.WorkerOverviewController;
import wrestling.view.utility.GameScreen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;
import wrestling.model.interfaces.iNewsItem;

public class NextDayScreenController extends ControllerBase implements Initializable {

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
    public ListView<iNewsItem> newsListView;

    private List<Button> timeButtons;

    private ChronoUnit chronoUnit;

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

        NewsItem newsItem = new NewsItem(
                "Welcome to Open Wrestling",
                "Have fun!");

        newsListView.getItems().add(0, newsItem);

    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
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

    public void nextDay() {
        updateTopMatches(chronoUnit, 1);
        gameController.getInjuryManager().getInjuries().stream().forEach((injury) -> {
            if (injury.getStartDate().equals(gameController.getDateManager().today().minusDays(1))) {
                newsListView.getItems().add(0, injury);
            }

        });
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
                    setText(null);
                } else {
                    setText(gameController.getSegmentManager().getIsolatedSegmentString(segmentView));
                }
            }
        });
    }

    public void addNews(iNewsItem newsItem) {
        newsListView.getItems().add(0, newsItem);
    }

}
