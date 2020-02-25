package openwrestling.view.start.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TitleScreenController extends ControllerBase implements Initializable {

    @FXML
    private Button newRandomGameButton;

    @FXML
    private Button newImportGameButton;

    @FXML
    private Button continueGameButton;

    @FXML
    private ImageView imageView;

    @FXML
    private Text versionText;

    @FXML
    private void handleButtonAction(ActionEvent event) {

        if (event.getSource() == newRandomGameButton) {
            try {
                mainApp.newRandomGame();
            } catch (IOException ex) {
                logger.log(Level.ERROR, "Exception on new random game", ex);
                ViewUtils.generateAlert("Error", "Import game data failed", ex.getMessage()).showAndWait();
            }
        } else if (event.getSource() == newImportGameButton) {
            showImportDialog();
        } else if (event.getSource() == continueGameButton) {
            mainApp.continueGame();
        }

    }

    private void showImportDialog() {
        Stage importPopup = new Stage();
        importPopup.setResizable(false);

        importPopup.initModality(Modality.APPLICATION_MODAL);
        importPopup.setTitle("New Import Game");

        GameScreen importDialog = ViewUtils.loadScreenFromFXML(ScreenCode.IMPORT_DIALOG, mainApp, gameController);
        importDialog.controller.updateLabels();
        ((ImportDialogController) importDialog.controller).setStage(importPopup);

        Scene importScene = new Scene(importDialog.pane);

        importScene.getStylesheets().add("style.css");

        importPopup.setScene(importScene);

        importPopup.showAndWait();

    }

    @Override
    public void initializeMore() {
        versionText.setText("Version " + mainApp.VERSION + "\n"
                + "For feedback and support contact " + mainApp.CONTACT);
    }

    public void setImage(Image image) {
        this.imageView.setImage(image);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        newRandomGameButton.setText("New Game\n(Random Data)");
        newImportGameButton.setText("New Game\n(Import Data)");
        continueGameButton.setText("Continue");
    }
}
