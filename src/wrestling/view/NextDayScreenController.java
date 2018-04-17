package wrestling.view;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import wrestling.model.modelView.SegmentView;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class NextDayScreenController extends ControllerBase implements Initializable {

    @FXML
    public AnchorPane displayPane;

    @FXML
    public ListView rankingsListView;

    @FXML
    public Button weekButton;

    @FXML
    public Button monthButton;

    @FXML
    public Button yearButton;

    private List<Button> timeButtons;

    private ChronoUnit chornoUnit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger = LogManager.getLogger(getClass());
        timeButtons = Arrays.asList(weekButton, monthButton, yearButton);
        rankingsListView.setPlaceholder(new Label("No matches for selected time period"));
        chornoUnit = ChronoUnit.WEEKS;
    }

    @Override
    public void initializeMore() {
        updateTopMatches(ChronoUnit.WEEKS, 1);
        ViewUtils.updateSelectedButton(weekButton, timeButtons);

    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        if (button.equals(weekButton)) {
            chornoUnit = ChronoUnit.WEEKS;
        } else if (button.equals(monthButton)) {
            chornoUnit = ChronoUnit.MONTHS;
        } else if (button.equals(yearButton)) {
            chornoUnit = ChronoUnit.YEARS;
        }
        updateTopMatches(chornoUnit, 1);
        ViewUtils.updateSelectedButton(button, timeButtons);

    }

    public void setLoadingMessage(String string) {
        Text text = new Text(string);
        displayPane.getChildren().clear();
        displayPane.getChildren().add(text);
    }

    public void nextDay() {
        updateTopMatches(chornoUnit, 1);
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

}
