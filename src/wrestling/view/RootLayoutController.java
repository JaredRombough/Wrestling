package wrestling.view;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private Label currentFundsLabel;

    @FXML
    private Button browserButton;

    @FXML
    private Button newsButton;

    @FXML
    private ButtonBar buttonBar;

    private final String SELECTED_BUTTON = "selectedButton";
    private transient Logger logger;

    private EventButtonState eventButtonState;

    public double rootLayoutMinWidth() {
        return buttonBar.getButtonMinWidth() * buttonBar.getButtons().size();

    }

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

    private void updateSelectedButton(Button button) {
        for (Node b : buttonBar.getButtons()) {
            if (b.getStyleClass().contains(SELECTED_BUTTON)) {
                b.getStyleClass().remove(SELECTED_BUTTON);
            }
        }

        button.getStyleClass().add(SELECTED_BUTTON);
    }

    @Override
    public void updateLabels() {

        updateCurrentDateLabel();
        updateCurrentFundsLabel();
        updateEventButton();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        setButtonsDisable(true);

        logger = LogManager.getLogger(getClass());
    }

    @Override
    public void initializeMore() {
        updateCurrentDateLabel();
        updateSelectedButton(browserButton);

    }

    private void updateEventButton() {

        Event nextEvent = gameController.getEventManager().getNextEvent(
                gameController.getPromotionManager().playerPromotion(), gameController.getDateManager().today());

        if (nextEvent == null) {
            eventButton.setText("No event booked!");
            eventButtonState = EventButtonState.NO_EVENT;
        } else if (nextEvent.getDate().equals(gameController.getDateManager().today())) {
            eventButton.setText("Book event");
            eventButtonState = EventButtonState.EVENT_TODAY;
        } else {
            long days = DAYS.between(gameController.getDateManager().today(), nextEvent.getDate());
            eventButton.setText(days + " days to next event");
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
                gameController.getPromotionManager().playerPromotion(),
                gameController.getDateManager().today());
    }

    private void updateCurrentDateLabel() {
        calendarButton.setText(gameController.getDateManager().today().format(DateTimeFormatter.ofPattern("MMM dd yyyy (cccc)")));
    }

    private void updateCurrentFundsLabel() {
        currentFundsLabel.setText("Funds: $" + gameController.getPromotionManager()
                .getBankAccount(gameController.getPromotionManager().playerPromotion()).getFunds());
    }

    public void setButtonsDisable(boolean disable) {
        browserButton.setDisable(disable);
        nextDayButton.setDisable(disable);
        eventButton.setDisable(disable);
        financialButton.setDisable(disable);
        calendarButton.setDisable(disable);
        newsButton.setDisable(disable);
    }

    private enum EventButtonState {
        EVENT_TODAY,
        FUTURE_EVENT,
        NO_EVENT
    }
}
