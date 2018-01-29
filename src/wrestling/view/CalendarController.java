package wrestling.view;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class CalendarController extends ControllerBase implements Initializable {

    private final ArrayList<CalendarNode> allCalendarDays = new ArrayList<>(35);

    @FXML
    private Text calendarTitle;

    private YearMonth currentYearMonth;

    @FXML
    private Button nextMonth;

    @FXML
    private Button previousMonth;

    @FXML
    private GridPane calendar;

    private CalendarNode selected;

    private final Text[] dayNames = new Text[]{
        new Text("Sunday"),
        new Text("Monday"),
        new Text("Tuesday"),
        new Text("Wednesday"),
        new Text("Thursday"),
        new Text("Friday"),
        new Text("Saturday")};

    @Override
    public void initializeMore() {
        currentYearMonth = YearMonth.from(gameController.getDateManager().today());

        calendar.setGridLinesVisible(true);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                CalendarNode caledarNode = new CalendarNode();
                caledarNode.setOnMouseClicked(e -> clicked(caledarNode));
                caledarNode.setPrefSize(200, 200);
                calendar.add(caledarNode, j, i);
                allCalendarDays.add(caledarNode);
            }
        }

        GridPane dayLabels = new GridPane();
        dayLabels.setPrefWidth(600);
        Integer col = 0;
        for (Text txt : dayNames) {
            AnchorPane ap = new AnchorPane();
            ap.setPrefSize(200, 10);
            AnchorPane.setBottomAnchor(txt, 5.0);
            ap.getChildren().add(txt);
            dayLabels.add(ap, col++, 0);
        }

        previousMonth.setOnAction(e -> previousMonth());

        nextMonth.setOnAction(e -> nextMonth());

        populateCalendar(currentYearMonth);
    }

    public void populateCalendar(YearMonth yearMonth) {

        LocalDate calendarDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);

        while (!calendarDate.getDayOfWeek().toString().equals("SUNDAY")) {
            calendarDate = calendarDate.minusDays(1);
        }

        for (CalendarNode ap : allCalendarDays) {
            if (!ap.getChildren().isEmpty()) {
                ap.getChildren().remove(0);
            }

            ap.setPrefSize(200, 10);

            Text txt = new Text(String.valueOf(calendarDate.getDayOfMonth()) + "\nhoooo");
            ap.setDate(calendarDate);
            ap.getChildren().add(txt);

            CalendarNode.setBottomAnchor(txt, 5.0);
            CalendarNode.setTopAnchor(txt, 5.0);
            CalendarNode.setLeftAnchor(txt, 5.0);

            calendarDate = calendarDate.plusDays(1);
        }

        calendarTitle.setText(calendarDate.getMonth().toString() + " " + String.valueOf(calendarDate.getYear()));
    }

    private void clicked(CalendarNode calendarNode) {
        selected = calendarNode;
        
        for (CalendarNode node : allCalendarDays) {
            if (node.getStyle().contains("-fx-background-color: blue;")) {
                node.setStyle("");
            }
        }
        calendarNode.setStyle("-fx-background-color: blue;");

    }

    private void previousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        populateCalendar(currentYearMonth);
    }

    private void nextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        populateCalendar(currentYearMonth);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
