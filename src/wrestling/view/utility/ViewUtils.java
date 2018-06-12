package wrestling.view.utility;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wrestling.MainApp;
import wrestling.model.SegmentItem;
import wrestling.model.controller.GameController;
import wrestling.view.RegionWrapper;
import wrestling.view.utility.comparators.WorkerAgeComparator;
import wrestling.view.utility.comparators.WorkerBehaviourComparator;
import wrestling.view.utility.comparators.WorkerCharismaComparator;
import wrestling.view.utility.comparators.WorkerFlyingComparator;
import wrestling.view.utility.comparators.NameComparator;
import wrestling.view.utility.comparators.WorkerPopularityComparator;
import wrestling.view.utility.comparators.WorkerStrikingComparator;
import wrestling.view.utility.comparators.WorkerWrestlingComparator;
import wrestling.view.utility.comparators.WorkrateComparator;

public final class ViewUtils {

    public static ComboBox updatePlayerComboBox(AnchorPane anchorPane, boolean isPlayerPromotion,
            Object[] objects, Object object) {
        return updatePlayerComboBox(anchorPane, isPlayerPromotion, Arrays.asList(objects), object);
    }

    public static ComboBox updatePlayerComboBox(AnchorPane anchorPane, boolean isPlayerPromotion,
            List objects, Object object) {
        anchorPane.getChildren().clear();

        ComboBox comboBox = new ComboBox();
        if (isPlayerPromotion) {

            ViewUtils.anchorRegionToParent(anchorPane, comboBox);
            comboBox.setItems(FXCollections.observableArrayList(objects));
            comboBox.getSelectionModel().select(object);
        } else {
            Label label = new Label(object.toString());
            label.getStyleClass().add("workerStat");
            ViewUtils.anchorRegionToParent(anchorPane, label);

        }

        return comboBox;
    }

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

    public static GridPane gridPaneWithColumns(int columns) {
        GridPane gridPane = new GridPane();
        for (int i = 0; i < columns; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100);
            gridPane.getColumnConstraints().add(colConst);
        }
        return gridPane;
    }

    public static GridPane gridPaneWithDimensions(int columns, int rows) {
        GridPane gridPane = new GridPane();
        for (int i = 0; i < columns; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100);
            gridPane.getColumnConstraints().add(colConst);
        }

        for (int i = 0; i < rows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100);
            gridPane.getRowConstraints().add(rowConst);
        }
        gridPane.setGridLinesVisible(true);
        return gridPane;
    }

    public static void inititializeRegion(Region region) {
        region.setMinWidth(Control.USE_COMPUTED_SIZE);
        region.setMinHeight(Control.USE_COMPUTED_SIZE);
        region.setPrefWidth(Control.USE_COMPUTED_SIZE);
        region.setPrefHeight(Control.USE_COMPUTED_SIZE);
        region.setMaxHeight(Double.MAX_VALUE);
        region.setMaxWidth(Double.MAX_VALUE);
    }

    public static void showImage(String fileString, StackPane imageFrame, ImageView imageView, Image defaultImage) {

        File imageFile = new File(fileString);

        if (imageFile.exists() && !imageFile.isDirectory()) {

            if (!imageFrame.visibleProperty().get()) {
                imageFrame.setVisible(true);
            }
            Image image = new Image("File:" + imageFile);
            imageView.setImage(image);
        } else if (defaultImage != null) {
            imageView.setImage(defaultImage);
        } else {
            imageFrame.setVisible(false);
        }
    }

    //shows an image if it exists, handles hide/show of image frame
    public static void showImage(String imageFile, StackPane imageFrame, ImageView imageView) {
        showImage(imageFile, imageFrame, imageView, null);
    }

    public static Image loadImage(InputStream inputStream) {
        Logger logger = LogManager.getLogger("ViewUtils loadImage()");
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(inputStream);
        } catch (IOException ex) {
            logger.log(Level.FATAL, "Error loading image", ex);
        }
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public static GameScreen loadScreenFromResource(ScreenCode code, MainApp mainApp, GameController gameController, AnchorPane parent) {
        GameScreen screen = loadScreenFromResource(code, mainApp, gameController);
        anchorRegionToParent(parent, screen.pane);
        return screen;
    }

    public static GameScreen loadScreenFromResource(ScreenCode code, MainApp mainApp, GameController gameController) {
        Logger logger = LogManager.getLogger("ViewUtils loadScreenFromResource()");
        FXMLLoader loader = new FXMLLoader();
        GameScreen screen = new GameScreen();
        loader.setLocation(MainApp.class.getResource(code.resourcePath()));
        try {
            switch (code) {
                case ROOT: {
                    screen.pane = (BorderPane) loader.load();
                    break;
                }
                default:
                    screen.pane = (AnchorPane) loader.load();
                    break;
            }
        } catch (IOException ex) {
            logger.log(Level.FATAL, String.format("Error loading Screen from %s", code.resourcePath()), ex);
        }

        screen.controller = loader.getController();
        screen.controller.setDependencies(mainApp, gameController);
        screen.code = code;
        return screen;
    }

    public static GameScreen getByCode(List<GameScreen> screens, ScreenCode code) {
        for (GameScreen screen : screens) {
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
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText(header);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public static String editTextDialog(String string) {
        TextInputDialog dialog = new TextInputDialog(string);
        dialog.setHeaderText("Edit this value");
        dialog.getDialogPane().getStylesheets().add("style.css");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return string;
        }
    }

    public static void anchorRegionToParent(AnchorPane parent, Region child) {

        parent.getChildren().add(child);

        AnchorPane.setTopAnchor(child, 0.0);
        AnchorPane.setRightAnchor(child, 0.0);
        AnchorPane.setLeftAnchor(child, 0.0);
        AnchorPane.setBottomAnchor(child, 0.0);
    }

    public static RegionWrapper addComboBoxWrapperToVBox(ObservableList items, String text, VBox vBox) {
        ComboBox comboBox = new ComboBox();
        comboBox.setItems(items);
        return addRegionWrapperToVBox(comboBox, text, vBox);
    }

    public static RegionWrapper addRegionWrapperToVBox(Region region, String text, VBox vBox) {

        Label label = new Label(text);
        label.setMaxWidth(Double.MAX_VALUE);
        GridPane.setConstraints(label, 0, 0);
        GridPane.setMargin(label, new Insets(5));

        region.setMaxWidth(Double.MAX_VALUE);
        GridPane.setConstraints(region, 1, 0);
        GridPane.setColumnSpan(region, 2);
        GridPane.setMargin(region, new Insets(5));

        GridPane gridPane = ViewUtils.gridPaneWithColumns(3);
        gridPane.getChildren().addAll(label, region);
        gridPane.setMaxWidth(Double.MAX_VALUE);

        vBox.getChildren().add(gridPane);
        VBox.setMargin(gridPane, new Insets(5));

        return new RegionWrapper(gridPane, region);
    }

    public static void initListCellForSegmentItemDragAndDrop(ListCell listCell, SegmentItem segmentItem, boolean empty) {
        if (empty) {
            listCell.setText(null);
            listCell.setGraphic(null);
            listCell.setOnDragDetected(null);
        } else {
            listCell.setText(segmentItem.toString());

            listCell.setOnDragDetected((MouseEvent event) -> {
                ClipboardContent cc = new ClipboardContent();
                cc.putString(listCell.getItem().toString());
                listCell.startDragAndDrop(TransferMode.MOVE).setContent(cc);
                LocalDragboard.getINSTANCE().putValue(SegmentItem.class, segmentItem);
                event.consume();
            });
        }
    }

    //update the sortbox to match the browse mode we are in
    public static void updateComboBoxComparators(ComboBox comboBox, ObservableList comparators) {
        //definitely update the box if the box is empty
        if (comboBox.getItems().isEmpty()) {
            comboBox.setItems(comparators);
            comboBox.getSelectionModel().selectFirst();
        }

        //if the box is not empty check if it has the same stuff we're trying to put in it
        if (!comboBox.getItems().get(0).getClass().equals(comparators.get(0).getClass())) {
            comboBox.setItems(comparators);
            comboBox.getSelectionModel().selectFirst();
        }
    }

    public static void updateSelectedButton(Button button, List<Button> buttons) {

        String selectedButtonClass = "selectedButton";

        buttons.stream().filter((b) -> (b.getStyleClass().contains(selectedButtonClass))).forEach((b) -> {
            b.getStyleClass().remove(selectedButtonClass);
        });

        button.getStyleClass().add(selectedButtonClass);

    }

    public static Button getXButton() {
        Button xButton = new Button();
        HBox.setMargin(xButton, new Insets(5));
        xButton.setText("X");
        xButton.setFont(Font.font("Verdana", 10));
        return xButton;
    }

    public static Button getSmallButton(String text) {
        Button xButton = new Button();
        HBox.setMargin(xButton, new Insets(5));
        xButton.setText(text);
        xButton.setFont(Font.font("Verdana", 10));
        return xButton;
    }

    public static String intToStars(int rating) {
        int stars = rating / 20;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stars; i++) {
            sb.append("*");
        }

        int quarterStars = (rating / 5) % 4;

        if (quarterStars != 0) {
            if (quarterStars == 2) {
                sb.append("1/2");
            } else {
                sb.append(String.format("%d/4", quarterStars));
            }
        }
        return sb.toString();
    }

    public static ObservableList getWorkerComparators() {
        return FXCollections.observableArrayList(new NameComparator(),
                new WorkerPopularityComparator(),
                new WorkrateComparator(),
                new WorkerCharismaComparator(),
                new WorkerWrestlingComparator(),
                new WorkerFlyingComparator(),
                new WorkerStrikingComparator(),
                new WorkerBehaviourComparator(),
                new WorkerAgeComparator()
        );
    }

}
