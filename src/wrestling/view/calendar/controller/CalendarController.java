package wrestling.view.calendar.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import wrestling.model.Event;
import wrestling.view.SimpleDisplayController;
import wrestling.view.utility.GameScreen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class CalendarController extends ControllerBase implements Initializable {

    private final ArrayList<CalendarNode> allCalendarDays = new ArrayList<>(35);

    @FXML
    private Text calendarTitle;

    @FXML
    private Button nextMonth;

    @FXML
    private Button previousMonth;

    @FXML
    private GridPane calendar;

    @FXML
    private GridPane dayLabels;

    @FXML
    private GridPane baseGridPane;

    @FXML
    private ListView listView;

    @FXML
    private AnchorPane displayPaneBase;

    @FXML
    private AnchorPane bookingPaneBase;

    private LocalDate selectedDate;
    private YearMonth currentYearMonth;

    private GameScreen simpleDisplayScreen;
    private GameScreen bookShowScreen;
    private BookShowController bookShowController;

    private final String SELECTED_CALENDAR_NODE = "selectedCalendarNode";
    private final String CURRENT_DATE = "currentDate";
    private final String DIFFERENT_MONTH = "differentMonth";
    private final String DIFFERENT_MONTH_TEXT = "differentMonthText";
    private final String DARK_BORDER = "darkBorder";
    private final String GREEN_BORDER = "greenBorder";
    private final int WEEK_DAYS = 7;
    private final int WEEKS = 6;

    private Event currentEvent;

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

        populateCalendar(currentYearMonth);

        simpleDisplayScreen = ViewUtils.loadScreenFromResource(ScreenCode.SIMPLE_DISPLAY, mainApp, gameController, displayPaneBase);
        bookShowScreen = ViewUtils.loadScreenFromResource(ScreenCode.BOOK_FUTURE_SHOW, mainApp, gameController, bookingPaneBase);
        bookShowController = (BookShowController) bookShowScreen.controller;
        SimpleDisplayController simpleDisplayController = (SimpleDisplayController) simpleDisplayScreen.controller;
        simpleDisplayController.setDefaultTitle("No event selected");
        Button viewTemplateButton = simpleDisplayController.getActionButton();
        viewTemplateButton.setText("View Event Series");
        viewTemplateButton.setVisible(true);
        viewTemplateButton.setOnAction(e -> {
            System.out.println(currentEvent.toString());
            mainApp.show(ScreenCode.BROWSER, currentEvent.getEventTemplate());
        });

        initializeButtons();

        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Event>() {
            @Override
            public void changed(ObservableValue<? extends Event> observable, Event oldValue, Event newValue) {
                simpleDisplayScreen.controller.setCurrent(newValue);
                currentEvent = newValue;
                viewTemplateButton.setDisable(newValue == null);
            }
        });

    }

    private void initializeButtons() {
        previousMonth.setOnAction(e -> previousMonth());

        nextMonth.setOnAction(e -> nextMonth());

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

            List<Event> eventsOnDay = gameController.getEventManager().getEventsOnDate(calendarDate);
            Text text = new Text(String.valueOf(calendarDate.getDayOfMonth()) + (eventsOnDay.size() > 0 ? ("\n" + eventsOnDay.size() + " events") : ""));
            calendarNode.setDate(calendarDate);

            setCalendarNodeStyle(calendarNode, text);

            calendarNode.getChildren().add(text);

            CalendarNode.setBottomAnchor(text, 5.0);
            CalendarNode.setTopAnchor(text, 5.0);
            CalendarNode.setLeftAnchor(text, 5.0);

            calendarDate = calendarDate.plusDays(1);
        }

    }

    private void setCalendarNodeStyle(CalendarNode calendarNode, Text text) {
        calendarNode.getStyleClass().clear();

        if (calendarNode.getDate().getMonth() != currentYearMonth.getMonth()) {
            calendarNode.getStyleClass().add(DIFFERENT_MONTH);
            text.getStyleClass().add(DIFFERENT_MONTH_TEXT);
        }

        if (calendarNode.getDate().equals(gameController.getDateManager().today())
                && calendarNode.getDate().getMonth() == currentYearMonth.getMonth()) {
            calendarNode.getStyleClass().add(CURRENT_DATE);
        }

        if (gameController.getEventManager().getEventOnDate(playerPromotion(), calendarNode.getDate()) != null) {
            calendarNode.getStyleClass().add(GREEN_BORDER);
        }

        if (calendarNode.getDate().equals(selectedDate)) {
            calendarNode.getStyleClass().add(SELECTED_CALENDAR_NODE);
        }
    }

    private void clicked(CalendarNode calendarNode) {
        selectedDate = checkCurrentMonth(calendarNode.getDate()).getDate();

        listView.setItems(FXCollections.observableArrayList(gameController.getEventManager().getEventsOnDate(selectedDate)).sorted());

        if (!listView.getItems().isEmpty()) {
            listView.getSelectionModel().selectFirst();
        }

        bookShowScreen.controller.setCurrent(selectedDate);

        updateLabels();
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

    private void selectNode(LocalDate date) {
        YearMonth yearMonth = YearMonth.from(date);
        if (!currentYearMonth.equals(yearMonth)) {
            currentYearMonth = yearMonth;
            populateCalendar(currentYearMonth);
        }
        CalendarNode node = selectNodeForCurrentMonth(date);
        clicked(node);
    }

    public void rescheduleEvent(Event event) {
        selectEvent(event);
        bookShowController.startReschedule(event);
    }

    private void selectEvent(Event event) {
        YearMonth yearMonth = YearMonth.from(event.getDate());
        if (!currentYearMonth.equals(yearMonth)) {
            currentYearMonth = yearMonth;
            populateCalendar(currentYearMonth);
        }
        CalendarNode node = selectNodeForCurrentMonth(event.getDate());
        clicked(node);
        listView.getSelectionModel().select(event);
    }

    private CalendarNode selectNodeForCurrentMonth(LocalDate date) {
        for (CalendarNode node : allCalendarDays) {
            if (node.getDate().equals(date)) {
                return node;
            }
        }
        return null;
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

    @Override
    public void focusLost() {
        if (bookShowController != null && bookShowController.isRescheduling()) {
            bookShowController.cancelReschedule();
        }
    }

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof LocalDate) {
            LocalDate date = (LocalDate) obj;
            selectNode(date);
        } else if (obj instanceof Event) {
            Event event = (Event) obj;
            selectEvent(event);
        } else {
            logger.log(Level.ERROR, "Invalid object passed to CalendarContoller");
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger = LogManager.getLogger(getClass());
        listView.setPlaceholder(new Label("No events on this date"));
        nextMonth.setText("Next");
        previousMonth.setText("Previous");
        ViewUtils.lockGridPane(baseGridPane);
        ViewUtils.inititializeRegion(nextMonth);
        ViewUtils.inititializeRegion(previousMonth);
        ViewUtils.inititializeRegion(dayLabels);
        bookingPaneBase.getStyleClass().add(DARK_BORDER);
        displayPaneBase.getStyleClass().add(DARK_BORDER);

    }
}
