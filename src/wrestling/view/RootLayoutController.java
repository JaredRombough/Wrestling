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
    private ButtonBar buttonBar;

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
        }

        }

    private void updateSelectedButton(Button button) {
        for (Node b : buttonBar.getButtons()) {
            if (b.getStyleClass().contains("selectedButton")) {
                b.getStyleClass().remove("selectedButton");
            }
        }

        button.getStyleClass().add("selectedButton");

    }

    @Override
    public void updateLabels() {

        updateCurrentDateLabel();
        updateCurrentFundsLabel();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        setButtonsDisable(true);
    }

    @Override
    public void initializeMore() {
        updateCurrentDateLabel();
        updateSelectedButton(browserButton);
        calendarButton.setDisable(true);

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
    }
}
