package wrestling.view.event;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import wrestling.view.utility.Screen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class TeamPaneWrapper extends ControllerBase implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private VBox vBox;

    @FXML
    private HBox header;

    @FXML
    private Button xButton;

    @FXML
    private Label teamTypeLabel;

    private Screen teamPane;
    private static final String TAB_DRAG_KEY = "anchorpane";
    private ObjectProperty<AnchorPane> draggingTab;

    private ComboBox angleComboBox;
    private ComboBox promoComboBox;
    private ComboBox violenceComboBox;

    private TeamPaneState currentState;

    private enum TeamPaneState {
        CHALLENGER,
        CHALLENGED,
        INTERFERENCE,
        INTERVIEWER
    }

    private void setCurrentState(TeamPaneState state) {
        currentState = state;
        switch (currentState) {
            case CHALLENGER:
                break;
            case CHALLENGED:
                break;
            case INTERFERENCE:
                break;
            case INTERVIEWER:
                break;
        }
    }

    public Button getXButton() {
        return xButton;
    }

    public void setMatch() {
        vBox.getChildren().retainAll(teamPane.pane, header);
    }

    public void setAngle() {
        vBox.getChildren().retainAll(teamPane.pane, header);
        addAngleComboBox();
        addViolenceComboBox();

    }

    public void setInterference() {
        vBox.getChildren().retainAll(teamPane.pane, header);
        addTargetComboBox();
        addSuccessComboBox();
        addTimingComboBox();

    }

    private void addAngleComboBox() {
        angleComboBox = new ComboBox();
        angleComboBox.getItems().addAll(
                "Promo",
                "Offer",
                "Challenge",
                "Announcement");

        vBox.getChildren().add(angleComboBox);
        angleComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                if (t1 != null) {
                    clearControls();
                    if (t1.equals("Promo")) {
                        addPromoComboBox();
                    } else if (t1.equals("Offer")) {
                        addJoinTeamComboBox();
                    } else if (t1.equals("Challenge")) {
                        addShowComboBox();
                    }
                    if (violenceComboBox != null && vBox.getChildren().contains(violenceComboBox)) {
                        violenceComboBox.toFront();
                    }
                }
            }
        });
        angleComboBox.getSelectionModel().selectFirst();

    }

    private void clearControls() {
        vBox.getChildren().retainAll(Arrays.asList(angleComboBox, teamPane.pane, violenceComboBox, header));
    }

    private void addPromoComboBox() {
        ComboBox comboBox = new ComboBox();
        comboBox.getItems().addAll(
                "Self Hype", "Target");
        vBox.getChildren().add(comboBox);
        comboBox.getSelectionModel().selectFirst();
    }

    private void addJoinTeamComboBox() {
        ComboBox comboBox = new ComboBox();
        comboBox.getItems().addAll(
                "New Tag Team", "New Stable", "Existing Stable");
        vBox.getChildren().add(comboBox);
        comboBox.getSelectionModel().selectFirst();
    }

    private void addShowComboBox() {
        ComboBox comboBox = new ComboBox();
        comboBox.getItems().addAll(
                "Tonight", "Next Show", "Next Big Show", "Show x");
        vBox.getChildren().add(comboBox);
        comboBox.getSelectionModel().selectFirst();
    }

    private void addViolenceComboBox() {
        violenceComboBox = new ComboBox();
        violenceComboBox.getItems().addAll(
                "No Bump", "Attack", "Defend");
        vBox.getChildren().add(violenceComboBox);
        violenceComboBox.getSelectionModel().selectFirst();
    }

    private void addPresentComboBox() {
        ComboBox comboBox = new ComboBox();
        comboBox.getItems().addAll(
                "Present", "Not Present");
        vBox.getChildren().add(comboBox);
        comboBox.getSelectionModel().selectFirst();
    }

    private void addSuccessComboBox() {
        ComboBox comboBox = new ComboBox();
        comboBox.getItems().addAll(
                "Win", "Lose", "Draw");
        vBox.getChildren().add(comboBox);
        comboBox.getSelectionModel().selectFirst();
    }

    private void addTargetComboBox() {
        ComboBox comboBox = new ComboBox();
        comboBox.getItems().addAll(
                "Target A", "Target B", "Target C");
        vBox.getChildren().add(comboBox);
        comboBox.getSelectionModel().selectFirst();
    }

    private void addTimingComboBox() {
        ComboBox comboBox = new ComboBox();
        comboBox.getItems().addAll(
                "Before", "During", "After");
        vBox.getChildren().add(comboBox);
        comboBox.getSelectionModel().selectFirst();
    }

    public void setTeamTypeLabel(String string) {
        teamTypeLabel.setText(string);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void initializeMore() {
        teamPane = ViewUtils.loadScreenFromResource(ScreenCode.TEAM_PANE, mainApp, gameController);
        vBox.getChildren().add(teamPane.pane);
        preparePaneForSorting();
    }

    private void preparePaneForSorting() {
        draggingTab = new SimpleObjectProperty<>();
        anchorPane.setOnDragDetected((MouseEvent event) -> {
            Dragboard dragboard = anchorPane.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(TAB_DRAG_KEY);
            dragboard.setContent(clipboardContent);
            draggingTab.set(anchorPane);
            event.consume();
        });
        anchorPane.setOnDragOver((DragEvent event) -> {
            final Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()
                    && TAB_DRAG_KEY.equals(dragboard.getString())
                    && draggingTab.get() != null) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });
    }

    public TeamPaneController getTeamPaneController() {
        return (TeamPaneController) teamPane.controller;
    }

}
