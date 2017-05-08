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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
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

            DirectoryChooser dc = new DirectoryChooser();
            dc.titleProperty().set("Select the folder containing the scenario .dat files to import.");
            File importFolder = dc.showDialog(mainApp.getPrimaryStage());

            if (importFolder != null) {

                String errors = checkImportFolder(importFolder);

                if (errors.length() > 0) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("File not found");
                    alert.setHeaderText("Required files not found in " + importFolder);
                    alert.setContentText(errors);

                    alert.showAndWait();
                } else {
                    mainApp.newImportGame(importFolder);
                }
            }

        } else if (event.getSource() == continueGameButton) {
            mainApp.continueGame();
        }

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
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

    }
}
