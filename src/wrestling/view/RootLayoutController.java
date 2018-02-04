package wrestling.view;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
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

    public double rootLayoutMinWidth() {
        return buttonBar.getButtonMinWidth() * buttonBar.getButtons().size();

    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {

        if (event.getSource() == eventButton) {
            updateSelectedButton(eventButton);
            mainApp.show(ScreenCode.EVENT);
        } else if (event.getSource() == nextDayButton) {
            mainApp.nextDay();
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
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        setButtonsDisable(true);
        //calendarButton.setDisable(true);

        logger = LogManager.getLogger(getClass());
    }

    @Override
    public void initializeMore() {
        updateCurrentDateLabel();
        updateSelectedButton(browserButton);

    }

    public void updateCurrentDateLabel() {
        calendarButton.setText(gameController.getDateManager().today().format(DateTimeFormatter.ofPattern("MMM dd yyyy (cccc)")));
    }

    public void updateCurrentFundsLabel() {
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
}
