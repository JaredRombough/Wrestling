package wrestling.view.start.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import wrestling.view.utility.Screen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

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

            try {
                showImportDialog();
            } catch (IOException ex) {
                logger.log(Level.ERROR, "Exception on import game", ex);
                ViewUtils.generateAlert("Error", "Import game data failed", ex.getMessage()).showAndWait();
            }

        } else if (event.getSource() == continueGameButton) {
            try {
                mainApp.continueGame();
            } catch (IOException ex) {
                logger.log(Level.ERROR, "Exception on continue game", ex);
                ViewUtils.generateAlert("Error", "Continue from saved game failed", ex.getMessage()).showAndWait();
            }

        }

    }

    private boolean showImportDialog() throws IOException {
        Stage importPopup = new Stage();

        importPopup.initModality(Modality.APPLICATION_MODAL);
        importPopup.setTitle("New Import Game");

        Screen importDialog = ViewUtils.loadScreenFromResource(ScreenCode.IMPORT_DIALOG, mainApp, gameController);

        importDialog.controller.updateLabels();
        ((ImportDialogController) importDialog.controller).setStage(importPopup);

        Scene importScene = new Scene(importDialog.pane);

        importScene.getStylesheets().add("style.css");

        importPopup.setScene(importScene);

        importPopup.showAndWait();

        return true;
    }

    @Override
    public void initializeMore() {
        versionText.setText("Version " + mainApp.getVERSION() + "\n"
                + "For feedback and support contact " + mainApp.getCONTACT());
    }

    public void setImage(Image image) {
        this.imageView.setImage(image);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
