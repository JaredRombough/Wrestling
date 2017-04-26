package wrestling.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import wrestling.MainApp;

public class TitleScreenController implements Initializable {

    private MainApp mainApp;

    @FXML
    private Button newRandomGameButton;

    @FXML
    private Button newImportGameButton;

    @FXML
    private Button continueGameButton;

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException, ClassNotFoundException {

        if (event.getSource() == newRandomGameButton) {
            mainApp.newRandomGame();
        } else if (event.getSource() == newImportGameButton) {
            mainApp.newImportGame();
        } else if (event.getSource() == continueGameButton) {
            mainApp.continueGame();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

    }
}
