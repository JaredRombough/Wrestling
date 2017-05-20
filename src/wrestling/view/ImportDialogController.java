package wrestling.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import wrestling.MainApp;

/*
popup dialog window for setting import paths and starting import game
 */
public class ImportDialogController extends Controller implements Initializable {

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
    private MainApp mainApp;
    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {

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
    
    /**
     * @param import folder to check
     * @return String containing errors found
     */
    private String checkImportFolder(File importFolder) {
        File promos = new File(importFolder.getPath() + "\\promos.dat");
        File workers = new File(importFolder.getPath() + "\\wrestler.dat");
        File belts = new File(importFolder.getPath() + "\\belt.dat");

        List<File> filesNeeded = new ArrayList();
        filesNeeded.addAll(Arrays.asList(
                promos, workers, belts
        ));

        String errors = "";

        for (File f : filesNeeded) {
            if (!promos.exists() || promos.isDirectory()) {
                errors += f.getName() + " not found!\n";
            }
        }

        return errors;
    }

    @Override
    void initializeMore() {

    }

    @Override
    void setCurrent(Object obj) {

    }

    @Override
    void updateLabels() {

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


    @Override
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

}
