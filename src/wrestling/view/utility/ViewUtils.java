package wrestling.view.utility;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import wrestling.MainApp;
import wrestling.model.controller.GameController;

public final class ViewUtils {

    public static void lockGridPane(GridPane gridPane) {
        for (ColumnConstraints c : gridPane.getColumnConstraints()) {
            c.setPercentWidth(100);
            c.setMaxWidth(Double.MAX_VALUE);
        }

        for (RowConstraints c : gridPane.getRowConstraints()) {
            c.setPercentHeight(100);
            c.setMaxHeight(Double.MAX_VALUE);
        }
    }

    public static void inititializeRegion(Region region) {
        region.setMinWidth(Control.USE_COMPUTED_SIZE);
        region.setMinHeight(Control.USE_COMPUTED_SIZE);
        region.setPrefWidth(Control.USE_COMPUTED_SIZE);
        region.setPrefHeight(Control.USE_COMPUTED_SIZE);
        region.setMaxHeight(Double.MAX_VALUE);
        region.setMaxWidth(Double.MAX_VALUE);
    }

    //shows an image if it exists, handles hide/show of image frame
    public static void showImage(File imageFile, StackPane imageFrame, ImageView imageView) {

        if (imageFile.exists() && !imageFile.isDirectory()) {
            //show the border if it is not visible
            if (!imageFrame.visibleProperty().get()) {
                imageFrame.setVisible(true);
            }
            Image image = new Image("File:" + imageFile);
            imageView.setImage(image);
        } else //hide the border if it is visible
         if (imageFrame.visibleProperty().get()) {
                imageFrame.setVisible(false);
            }
    }

    public static Screen loadScreenFromResource(ScreenCode code, MainApp mainApp, GameController gameController) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Screen screen = new Screen();
        loader.setLocation(MainApp.class.getResource(code.resourcePath()));
        switch (code) {
            case ROOT: {
                screen.pane = (BorderPane) loader.load();
                break;
            }
            default:
                screen.pane = (AnchorPane) loader.load();
                break;
        }

        screen.controller = loader.getController();
        screen.controller.setDependencies(mainApp, gameController);
        screen.code = code;
        return screen;
    }

    public static Screen getByCode(List<Screen> screens, ScreenCode code) {
        for (Screen screen : screens) {
            if (screen.code == code) {
                return screen;
            }
        }
        return null;
    }

    public static Alert generateAlert(String title, String header, String content, AlertType type) {
        Alert alert = generateAlert(title, header, content);
        alert.setAlertType(type);
        return alert;
    }

    public static Alert generateAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add("style.css");
        return alert;
    }

    public static boolean generateConfirmationDialogue(String header, String content) {
        boolean response = true;
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText(header);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            response = true;
        } else {
            response = false;
        }
        return response;
    }

    public static void anchorPaneToParent(AnchorPane parent, Pane child) {

        parent.getChildren().add(child);

        AnchorPane.setTopAnchor(child, 0.0);
        AnchorPane.setRightAnchor(child, 0.0);
        AnchorPane.setLeftAnchor(child, 0.0);
        AnchorPane.setBottomAnchor(child, 0.0);
    }

}
