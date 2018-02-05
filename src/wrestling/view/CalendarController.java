package wrestling.view;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import wrestling.model.Event;
import wrestling.view.utility.Screen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;

public class CalendarController extends ControllerBase implements Initializable {

    private final ArrayList<CalendarNode> allCalendarDays = new ArrayList<>(35);

    @FXML
    private Text calendarTitle;

    @FXML
    private Button nextMonth;

    @FXML
    private Button previousMonth;

    @FXML
    private Button bookShowButton;

    @FXML
    private GridPane calendar;

    @FXML
    private GridPane dayLabels;

    @FXML
    private ListView listView;

    @FXML
    private AnchorPane mainDisplayPane;

    private CalendarNode selected;
    private YearMonth currentYearMonth;
    private boolean bookingShow;

    private Screen simpleDisplayScreen;
    private Screen bookShowScreen;

    private final String SELECTED_CALENDAR_NODE = "selectedCalendarNode";
    private final String CURRENT_DATE = "currentDate";
    private final String DIFFERENT_MONTH = "differentMonth";
    private final String DIFFERENT_MONTH_TEXT = "differentMonthText";
    private final int WEEK_DAYS = 7;
    private final int WEEKS = 6;

    private final Text[] dayNames = new Text[]{
        new Text("SUN"),
        new Text("MON"),
        new Text("TUE"),
        new Text("WED"),
        new Text("THU"),
        new Text("FRI"),
        new Text("SAT")};

    @Override
    public void initializeMore() {
        currentYearMonth = YearMonth.from(gameController.getDateManager().today());

        calendar.setGridLinesVisible(true);

        for (int i = 0; i < WEEKS; i++) {
            for (int j = 0; j < WEEK_DAYS; j++) {
                CalendarNode caledarNode = new CalendarNode();
                caledarNode.setOnMouseClicked(e -> clicked(caledarNode));
                calendar.add(caledarNode, j, i);
                allCalendarDays.add(caledarNode);
            }
        }

        Integer col = 0;
        for (Text txt : dayNames) {
            AnchorPane ap = new AnchorPane();
            AnchorPane.setBottomAnchor(txt, 5.0);
            ap.getChildren().add(txt);
            dayLabels.add(ap, col++, 0);
        }

        previousMonth.setOnAction(e -> previousMonth());

        nextMonth.setOnAction(e -> nextMonth());

        populateCalendar(currentYearMonth);

        try {
            simpleDisplayScreen = ViewUtils.loadScreenFromResource(ScreenCode.SIMPLE_DISPLAY, mainApp, gameController);
            bookShowScreen = ViewUtils.loadScreenFromResource(ScreenCode.BOOK_FUTURE_SHOW, mainApp, gameController);
        } catch (IOException ex) {
            Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);
        }

        mainDisplayPane.getChildren().add(simpleDisplayScreen.pane);

        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Event>() {
            @Override
            public void changed(ObservableValue<? extends Event> observable, Event oldValue, Event newValue) {
                simpleDisplayScreen.controller.setCurrent(newValue);
            }
        });
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource() == bookShowButton) {
            bookShowClicked();
        }
    }

    private void bookShowClicked() {
        bookingShow = !bookingShow;
        bookShowButton.setText(bookingShow ? "Cancel" : "Book a show");
        mainDisplayPane.getChildren().clear();
        mainDisplayPane.getChildren().add(bookingShow ? bookShowScreen.pane : simpleDisplayScreen.pane);
    }

    private void populateCalendar(YearMonth yearMonth) {

        LocalDate calendarDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);
        calendarTitle.setText(calendarDate.getMonth().toString() + " " + String.valueOf(calendarDate.getYear()));

        while (!calendarDate.getDayOfWeek().toString().equals("SUNDAY")) {
            calendarDate = calendarDate.minusDays(1);
        }

        for (CalendarNode calendarNode : allCalendarDays) {
            if (!calendarNode.getChildren().isEmpty()) {
                calendarNode.getChildren().remove(0);
            }

            int eventCount = gameController.getEventManager().getEventsOnDate(calendarDate).size();
            Text txt = new Text(String.valueOf(calendarDate.getDayOfMonth()) + (eventCount > 0 ? ("\n" + eventCount + " events") : ""));
            calendarNode.setDate(calendarDate);

            calendarNode.getStyleClass().clear();

            if (calendarNode.getDate().getMonth() != currentYearMonth.getMonth()) {
                calendarNode.getStyleClass().add(DIFFERENT_MONTH);
                txt.getStyleClass().add(DIFFERENT_MONTH_TEXT);
            }

            if (calendarNode.getDate().equals(gameController.getDateManager().today())
                    && calendarNode.getDate().getMonth() == currentYearMonth.getMonth()) {
                calendarNode.getStyleClass().add(CURRENT_DATE);
            }

            calendarNode.getChildren().add(txt);

            CalendarNode.setBottomAnchor(txt, 5.0);
            CalendarNode.setTopAnchor(txt, 5.0);
            CalendarNode.setLeftAnchor(txt, 5.0);

            calendarDate = calendarDate.plusDays(1);
        }

    }

    private void clicked(CalendarNode calendarNode) {
        selected = checkCurrentMonth(calendarNode.getDate());

        if (!listView.getItems().isEmpty()) {
            listView.getItems().clear();
        }
        listView.setItems(FXCollections.observableArrayList(gameController.getEventManager().getEventsOnDate(selected.getDate())));

        ((BookShowController) bookShowScreen.controller).setDate(selected.getDate());

        setSelectedColor(selected);

    }

    private CalendarNode checkCurrentMonth(LocalDate date) {
        if (date.getMonth() != currentYearMonth.getMonth()) {
            if (date.getYear() > currentYearMonth.getYear()
                    || date.getYear() == currentYearMonth.getYear()
                    && date.getMonthValue() > currentYearMonth.getMonthValue()) {
                nextMonth();
            } else {
                previousMonth();
            }
        }
        return selectNodeForCurrentMonth(date);
    }

    private CalendarNode selectNodeForCurrentMonth(LocalDate date) {
        for (CalendarNode node : allCalendarDays) {
            if (node.getDate().equals(date)) {
                return node;
            }
        }
        return null;
    }

    private void setSelectedColor(CalendarNode calendarNode) {
        for (CalendarNode node : allCalendarDays) {
            if (node.getStyleClass().contains(SELECTED_CALENDAR_NODE)) {
                node.getStyleClass().remove(SELECTED_CALENDAR_NODE);
            }
        }
        calendarNode.getStyleClass().add(SELECTED_CALENDAR_NODE);
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
    public void updateLabels() {
        populateCalendar(currentYearMonth);
    }

    public void selectDate(LocalDate date) {
        YearMonth yearMonth = YearMonth.from(date);
        if (!currentYearMonth.equals(yearMonth)) {
            currentYearMonth = yearMonth;
            populateCalendar(currentYearMonth);
        }
        CalendarNode node = selectNodeForCurrentMonth(date);
        clicked(node);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listView.setPlaceholder(new Label("No events on this date"));
        nextMonth.setText("Next");
        previousMonth.setText("Previous");
        bookShowButton.setText("Book a show");
        bookingShow = false;
        ViewUtils.inititializeRegion(nextMonth);
        ViewUtils.inititializeRegion(previousMonth);
        ViewUtils.inititializeRegion(dayLabels);
    }
}
