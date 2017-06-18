package wrestling.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    private ImageView imageView;

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException, ClassNotFoundException {

        if (event.getSource() == newRandomGameButton) {
            mainApp.newRandomGame();
        } else if (event.getSource() == newImportGameButton) {

            showImportDialog();

        } else if (event.getSource() == continueGameButton) {
            mainApp.continueGame();
        }

    }

    private boolean showImportDialog() throws IOException {
        Stage importPopup = new Stage();

        importPopup.initModality(Modality.APPLICATION_MODAL);
        importPopup.setTitle("New Import Game");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/ImportDialog.fxml"));

        AnchorPane importDialog = (AnchorPane) loader.load();

        ImportDialogController controller = loader.getController();
        controller.setMainApp(this.mainApp);
        controller.updateLabels();
        controller.setStage(importPopup);

        Scene importScene = new Scene(importDialog);

        importScene.getStylesheets().add("style.css");
        
        importPopup.setScene(importScene);

        importPopup.showAndWait();

        return true;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

    }
    
    public void setImage(Image image)
    {
        this.imageView.setImage(image);
    }
}
