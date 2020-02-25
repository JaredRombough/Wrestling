package openwrestling.view.start.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import openwrestling.view.utility.interfaces.ControllerBase;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ImportDialogController extends ControllerBase implements Initializable {

    @FXML
    private Button picsPathButton;

    @FXML
    private Button logosPathButton;

    @FXML
    private Button dataPathButton;

    @FXML
    private Button startGameButton;

    @FXML
    private Text picsPathLabel;

    @FXML
    private Text logosPathLabel;

    @FXML
    private Text dataPathLabel;

    private File picsPath;
    private File logosPath;
    private File dataPath;

    private Stage stage;

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException, Exception {

        if (event.getSource() == picsPathButton) {
            picsPath = chooseDirectory(picsPath, "Worker Pictures");
        } else if (event.getSource() == logosPathButton) {
            logosPath = chooseDirectory(logosPath, "Promotion Logos");
        } else if (event.getSource() == dataPathButton) {
            dataPath = chooseDirectory(dataPath, "Scenario .DAT Files");
        } else if (event.getSource() == startGameButton) {
            stage.close();
            mainApp.newImportGame(dataPath, picsPath, logosPath);
        }
        updateLabels();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private File chooseDirectory(File original, String title) {

        DirectoryChooser dc = new DirectoryChooser();
        dc.titleProperty().set("Select Folder Containing " + title);

        File importFolder = dc.showDialog(mainApp.getPrimaryStage());

        if (importFolder == null) {
            importFolder = original;

        }

        return importFolder;
    }

    @Override
    public void updateLabels() {

        picsPathLabel.setText(picsPath.toString());
        logosPathLabel.setText(logosPath.toString());
        dataPathLabel.setText(dataPath.toString());

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        picsPath = new File(System.getProperty("user.dir") + "/PICS/");
        logosPath = new File(System.getProperty("user.dir") + "/LOGOS/");
        dataPath = new File(System.getProperty("user.dir") + "/DATA/");
    }
}
