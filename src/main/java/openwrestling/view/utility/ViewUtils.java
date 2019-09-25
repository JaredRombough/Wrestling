package openwrestling.view.utility;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
import openwrestling.MainApp;
import openwrestling.model.Relationship;
import openwrestling.model.SegmentItem;
import openwrestling.model.controller.GameController;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.RosterSplit;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.interfaces.iContract;
import openwrestling.model.interfaces.iPerson;
import openwrestling.model.interfaces.iRosterSplit;
import openwrestling.model.segmentEnum.TeamType;
import openwrestling.model.utility.ContractUtils;
import openwrestling.view.RegionWrapper;
import openwrestling.view.utility.comparators.NameComparator;
import openwrestling.view.utility.comparators.SegmentItemAgeComparator;
import openwrestling.view.utility.comparators.SegmentItemBehaviourComparator;
import openwrestling.view.utility.comparators.StaffSkillComparator;
import openwrestling.view.utility.comparators.WorkerCharismaComparator;
import openwrestling.view.utility.comparators.WorkerFlyingComparator;
import openwrestling.view.utility.comparators.WorkerPopularityComparator;
import openwrestling.view.utility.comparators.WorkerStrikingComparator;
import openwrestling.view.utility.comparators.WorkerWrestlingComparator;
import openwrestling.view.utility.comparators.WorkrateComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static openwrestling.model.constants.StringConstants.ALL_ROSTER_SPLITS;

public final class ViewUtils {

    public static ComboBox updatePlayerComboBox(AnchorPane anchorPane, boolean isPlayerPromotion,
                                                Object[] objects, Object object) {
        return updatePlayerComboBox(anchorPane, isPlayerPromotion, Arrays.asList(objects), object);
    }

    public static ComboBox updatePlayerComboBox(AnchorPane anchorPane, boolean isPlayerPromotion,
                                                List objects, Object object) {
        anchorPane.getChildren().clear();

        ComboBox comboBox = new ComboBox();
        ViewUtils.anchorRegionToParent(anchorPane, comboBox);
        comboBox.setItems(FXCollections.observableArrayList(objects));
        comboBox.getSelectionModel().select(object);
        comboBox.setDisable(!isPlayerPromotion);
        return comboBox;
    }

    public static ComboBox initComboBoxWithPlaceholder(ComboBox comboBox, List items, String placeholder) {
        List<Object> listForComobBox = new ArrayList<>(items);
        listForComobBox.add(0, placeholder);
        comboBox.setItems(FXCollections.observableArrayList(listForComobBox));
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
        GameScreen screen = loadScreenFromFXML(code, mainApp, gameController);
        anchorRegionToParent(parent, screen.pane);
        return screen;
    }

    public static GameScreen loadScreenFromFXML(ScreenCode code, MainApp mainApp, GameController gameController) {
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
                case DEPARTMENT:
                case CONTRACT: {
                    screen.pane = (GridPane) loader.load();
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

    public static Dialog generateRelationshipDialog(SegmentItem segmentItem, List<Relationship> relationships) {
        Dialog<Title> dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();

        dialog.setTitle("Relationships");
        dialog.setHeaderText("Relationships for " + segmentItem.getLongName());
        dialogPane.getButtonTypes().addAll(ButtonType.OK);

        TableView tableView = new TableView();

        TableColumn<Relationship, String> column1 = new TableColumn<>("Name");
        column1.setCellValueFactory((CellDataFeatures<Relationship, String> p)
                -> new SimpleStringProperty(p.getValue().getOtherSegmentItem(segmentItem).getLongName()));
        TableColumn<Relationship, Integer> column2 = new TableColumn<>("Value");
        column2.setCellValueFactory((CellDataFeatures<Relationship, Integer> p)
                -> new SimpleIntegerProperty(p.getValue().getLevel()).asObject());

        column1.setMaxWidth(1f * Integer.MAX_VALUE * 75);
        column2.setMaxWidth(1f * Integer.MAX_VALUE * 25);

        tableView.getColumns().setAll(column1, column2);
        tableView.setItems(FXCollections.observableArrayList(relationships));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        dialogPane.setContent(tableView);
        dialogPane.setMinWidth(400);
        dialogPane.getStylesheets().add("style.css");

        return dialog;
    }

    public static boolean generateConfirmationDialogue(String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText(header);
        alert.setContentText(content);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add("style.css");

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public static String editTextDialog(String string, String header) {
        TextInputDialog dialog = new TextInputDialog(string);
        dialog.setHeaderText(header);
        dialog.getDialogPane().getStylesheets().add("style.css");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return string;
        }
    }

    public static String editTextDialog(String string) {
        return editTextDialog(string, "Edit this value");
    }

    public static Dialog<Title> createTitleViewDialog(GameController gameController) {
        Dialog<Title> dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        TextField titleName = new TextField();
        VBox vBox = new VBox(8);

        dialog.setTitle("Create Title");
        dialog.setHeaderText("Title Details");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        titleName.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        ViewUtils.addRegionWrapperToVBox(titleName, "Title Name:", vBox);

        dialogPane.setContent(vBox);
        dialogPane.getStylesheets().add("style.css");

        Platform.runLater(titleName::requestFocus);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                Title newTitle = Title.builder().
                        promotion(gameController.getPromotionManager().getPlayerPromotion())
                        .name(titleName.getText())
                        .build();
                gameController.getTitleManager().createTitle(newTitle);
                return newTitle;
            }
            return null;
        });
        return dialog;
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
                LocalDragboard
                        .getINSTANCE().putValue(SegmentItem.class, segmentItem);
                event.consume();
            });
        }
    }

    public static void initListCellForSegmentItemDragAndDrop(ListCell listCell, SegmentItem segmentItem, boolean empty, TeamType teamType) {
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
                LocalDragboard
                        .getINSTANCE().putValue(SegmentItem.class, segmentItem);
                LocalDragboard
                        .getINSTANCE().putValue(TeamType.class, teamType);
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
                new SegmentItemBehaviourComparator(),
                new SegmentItemAgeComparator()
        );
    }

    public static ObservableList getStaffComparators() {
        return FXCollections.observableArrayList(new NameComparator(),
                new StaffSkillComparator(),
                new SegmentItemBehaviourComparator(),
                new SegmentItemAgeComparator()
        );
    }

    public static boolean releaseWorkerDialog(iPerson person, Promotion promotion, LocalDate date) {
        String prompt;
        iContract contract = person.getContract(promotion);
        if (!person.getContract(promotion).isExclusive()) {
            prompt = String.format("%s has an open contract with %s, and can be released at no cost.",
                    person.getName(),
                    promotion.getShortName());
        } else {
            prompt = String.format("%s has an exclusive contract with %s, and can be released for %s.",
                    person.getName(),
                    promotion.getShortName(),
                    ContractUtils.calculateTerminationFee(contract, date) > 0
                            ? "$" + ContractUtils.calculateTerminationFee(contract, date)
                            : "no additional cost");
        }
        return ViewUtils.generateConfirmationDialogue("Really terminate this contract?", prompt);
    }

    public static ChoiceDialog<Worker> selectWorkerDialog(List<Worker> workers, String title, String header) {
        return selectWorkerDialog(workers, title, header, null);
    }

    public static ChoiceDialog<Worker> selectWorkerDialog(List<Worker> workers, String title, String header, Worker defaultSelection) {
        return selectWorkerDialog(workers, title, header, null, null);
    }

    public static ChoiceDialog<Worker> selectWorkerDialog(List<Worker> workers,
                                                          String title, String header, Worker defaultSelection, String emptyName) {

        Collections.sort(workers, new NameComparator());
        if (!StringUtils.isEmpty(emptyName)) {
            Worker empty = new Worker();
            empty.setName(emptyName);
            workers.add(0, empty);
        }
        ChoiceDialog<Worker> dialog;
        if (workers.contains(defaultSelection)) {
            dialog = new ChoiceDialog<>(defaultSelection, workers);
        } else {
            dialog = new ChoiceDialog<>();
            dialog.getItems().addAll(workers);
            if (!workers.isEmpty()) {
                dialog.setSelectedItem(workers.get(0));
            }
        }
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.getDialogPane().getStylesheets().add("style.css");
        return dialog;
    }

    public static void initializeButtonHover(Region region, Button button) {
        button.setVisible(false);
        region.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
            button.setVisible(isNowHovered);
        });
        button.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
            button.setVisible(isNowHovered);
        });
    }

    public static ChangeListener<Boolean> buttonHoverListener(Button button) {
        return (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            button.setVisible(newValue);
        };
    }

    public static void updateRosterSplitComboBox(ComboBox comboBox, List<RosterSplit> allRosterSplits, iRosterSplit item, Promotion promotion, Promotion playerPromotion) {
        List rosterSplits = allRosterSplits.stream()
                .filter(split -> split.getOwner().equals(promotion))
                .collect(Collectors.toList());
        comboBox.setOnAction(null);
        ViewUtils.initComboBoxWithPlaceholder(comboBox, rosterSplits, ALL_ROSTER_SPLITS);
        comboBox.setDisable(!playerPromotion.equals(promotion));
        if (item.getRosterSplit() != null) {
            comboBox.getSelectionModel().select(item.getRosterSplit());
        } else {
            comboBox.getSelectionModel().selectFirst();
        }
        comboBox.setOnAction(e -> {
            if (comboBox.getSelectionModel().getSelectedItem() instanceof Stable) {
                item.setRosterSplit((RosterSplit) comboBox.getSelectionModel().getSelectedItem());
            }
        });
    }

}
