package wrestling.view;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import wrestling.model.Event;
import wrestling.view.utility.ScreenCode;

public class RootLayoutController extends ControllerBase implements Initializable {

    @FXML
    private Button eventButton;

    @FXML
    private Button financialButton;

    @FXML
    private Button nextDayButton;

    @FXML
    private Button calendarButton;

    @FXML
    private Button browserButton;

    @FXML
    private Button newsButton;

    private final String SELECTED_BUTTON = "selectedButton";
    private final String SHOW_TODAY = "bookShowButtonShowToday";
    private final String SHOW_TODAY_SELECTED = "bookShowButtonShowTodaySelected";

    private EventButtonState eventButtonState;
    private List<Button> buttons;

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {

        if (event.getSource() == eventButton) {
            updateSelectedButton(eventButton);
            eventButtonClicked();
        } else if (event.getSource() == nextDayButton) {
            nextDayButtonClicked();
        } else if (event.getSource() == browserButton) {
            updateSelectedButton(browserButton);
            mainApp.show(ScreenCode.BROWSER);
        } else if (event.getSource() == financialButton) {
            updateSelectedButton(financialButton);
            mainApp.show(ScreenCode.FINANCIAL);
        } else if (event.getSource() == calendarButton) {
            updateSelectedButton(calendarButton);
            mainApp.show(ScreenCode.CALENDAR);
        } else if (event.getSource() == newsButton) {
            updateSelectedButton(newsButton);
            mainApp.show(ScreenCode.NEXT_DAY_SCREEN);
        }

    }

    public void updateSelectedButton(ScreenCode screenCode) {
        switch (screenCode) {
            case EVENT:
                updateSelectedButton(eventButton);
                break;
            case BROWSER:
                updateSelectedButton(browserButton);
                break;
            case FINANCIAL:
                updateSelectedButton(financialButton);
                break;
            case CALENDAR:
                updateSelectedButton(calendarButton);
                break;
            case NEXT_DAY_SCREEN:
                updateSelectedButton(newsButton);
                break;
            default:
                logger.log(Level.ERROR, "Invalid button to select " + screenCode);
        }
    }

    private void updateSelectedButton(Button selectedButton) {
        for (Button button : buttons) {
            if (button.getStyleClass().contains(SELECTED_BUTTON)) {
                button.getStyleClass().remove(SELECTED_BUTTON);
            }
            if (button.getStyleClass().contains(SHOW_TODAY_SELECTED)) {
                button.getStyleClass().remove(SHOW_TODAY_SELECTED);
            }
        }
        if (selectedButton.equals(eventButton) && eventButtonState.equals(EventButtonState.EVENT_TODAY)) {
            selectedButton.getStyleClass().add(SHOW_TODAY_SELECTED);
        } else {
            selectedButton.getStyleClass().add(SELECTED_BUTTON);
        }

    }

    @Override
    public void updateLabels() {

        updateCalendarButton();
        updateCurrentFundsButton();
        updateEventButton();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        buttons = new ArrayList<>(Arrays.asList(
                browserButton,
                calendarButton,
                eventButton,
                financialButton,
                newsButton,
                nextDayButton
        ));

        setButtonsDisable(true);

        logger = LogManager.getLogger(getClass());
    }

    @Override
    public void initializeMore() {
        updateCalendarButton();
        updateSelectedButton(browserButton);

    }

    private void updateEventButton() {

        Event nextEvent = gameController.getEventManager().getNextEvent(
                playerPromotion(), gameController.getDateManager().today());

        if (eventButton.getStyleClass().contains(SHOW_TODAY)) {
            eventButton.getStyleClass().remove(SHOW_TODAY);
        }

        if (nextEvent == null) {
            eventButton.setText("No event booked!");
            eventButtonState = EventButtonState.NO_EVENT;
        } else if (nextEvent.getDate().equals(gameController.getDateManager().today())) {
            eventButton.setText("Book today's event: \n" + nextEvent.toString());
            eventButton.getStyleClass().add(SHOW_TODAY);
            eventButtonState = EventButtonState.EVENT_TODAY;
        } else {
            long days = DAYS.between(gameController.getDateManager().today(), nextEvent.getDate());
            eventButton.setText(days + " days until:\n" + nextEvent.toString());
            eventButtonState = EventButtonState.FUTURE_EVENT;
        }

    }

    private void nextDayButtonClicked() throws IOException {
        if (eventButtonState.equals(EventButtonState.EVENT_TODAY)) {
            mainApp.show(ScreenCode.EVENT, nextPlayerEvent());
        } else {
            mainApp.nextDay();
        }

    }

    private void eventButtonClicked() {
        switch (eventButtonState) {
            case EVENT_TODAY:
                mainApp.show(ScreenCode.EVENT,
                        nextPlayerEvent());
                break;
            case NO_EVENT:
                mainApp.show(ScreenCode.CALENDAR);
                break;
            case FUTURE_EVENT:
                mainApp.show(
                        ScreenCode.CALENDAR,
                        nextPlayerEvent());
                break;
        }
    }

    private Event nextPlayerEvent() {
        return gameController.getEventManager().getNextEvent(
                playerPromotion(),
                gameController.getDateManager().today());
    }

    private void updateCalendarButton() {
        calendarButton.setText(gameController.getDateManager().today().format(DateTimeFormatter.ofPattern("MMM dd yyyy (cccc)")));
    }

    private void updateCurrentFundsButton() {
        financialButton.setText("Funds: $" + gameController.getPromotionManager()
                .getBankAccount(playerPromotion()).getFunds());
    }

    public void setButtonsDisable(boolean disable) {
        for (Button button : buttons) {
            button.setDisable(disable);
        }
    }

    private enum EventButtonState {
        EVENT_TODAY,
        FUTURE_EVENT,
        NO_EVENT
    }
}
