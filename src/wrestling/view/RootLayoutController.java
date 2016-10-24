/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wrestling.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import wrestling.MainApp;
import wrestling.model.GameController;

public class RootLayoutController implements Initializable {

    private MainApp mainApp;
    private GameController gameController;

    @FXML
    private Button eventButton;

    @FXML
    private Button nextDayButton;

    @FXML
    private Label currentDateLabel;

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

            mainApp.showEventScreen();

        } else if (event.getSource() == nextDayButton) {
            mainApp.nextDay();

        } else if (event.getSource() == browserButton) {

            mainApp.showBrowser();
        }

    }

    public void updateLabels() {

        updateCurrentDateLabel();
        updateCurrentFundsLabel();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        setButtonsDisable(true);
    }

    public void initializeMore() {
        updateCurrentDateLabel();

    }

    public void updateCurrentDateLabel() {

        currentDateLabel.setText("Day " + gameController.date());
    }

    public void updateCurrentFundsLabel() {
        currentFundsLabel.setText("Funds: $" + gameController.playerPromotion().getFunds());
    }

    public void setButtonsDisable(boolean disable) {
        browserButton.setDisable(disable);
        nextDayButton.setDisable(disable);
        eventButton.setDisable(disable);
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;

        initializeMore();

    }
}
