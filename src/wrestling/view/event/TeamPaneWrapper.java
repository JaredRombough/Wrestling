package wrestling.view.event;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
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
import javafx.scene.layout.VBox;
import wrestling.model.Worker;
import wrestling.model.modelView.SegmentTeam;
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
    private ComboBox targetComboBox;
    private ComboBox successComboBox;
    private List<SegmentTeam> targets;

    private TeamType teamType;

    public void setTeamType(TeamType state) {
        teamType = state;
        switch (teamType) {
            case CHALLENGER:
                break;
            case CHALLENGED:
                break;
            case INTERFERENCE:
                setInterference();
                break;
            case INTERVIEWER:
                break;
        }
    }

    public void setTargets(List<SegmentTeam> teams) {
        targets = teams;

        int previousIndex = -1;
        String previousName = "";
        if (!targetComboBox.getItems().isEmpty()) {
            previousIndex = targetComboBox.getSelectionModel().getSelectedIndex();
            previousName = targetComboBox.getSelectionModel().getSelectedItem().toString();
        }
        targetComboBox.getItems().clear();

        if (teams.size() > 1) {
            List<Worker> emptyList = new ArrayList<>();
            teams.add(new SegmentTeam(emptyList, TeamType.EVERYONE));
        }

        ObservableList list = FXCollections.observableArrayList(teams);

        targetComboBox.setItems(list);

        boolean nameMatch = false;
        for (int i = 0; i < targetComboBox.getItems().size(); i++) {
            if (targetComboBox.getItems().get(i).toString().equals(previousName)) {
                targetComboBox.getSelectionModel().select(i);
                nameMatch = true;
                break;
            }
        }
        if (!nameMatch && previousIndex != -1
                && targetComboBox.getItems().size() > previousIndex) {
            targetComboBox.getSelectionModel().select(previousIndex);
        } else if (!nameMatch) {
            targetComboBox.getSelectionModel().selectFirst();
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
        teamType = TeamType.INTERFERENCE;
        vBox.getChildren().retainAll(teamPane.pane, header);
        addTargetComboBox();
        addSuccessComboBox();
        addTimingComboBox();

    }

    private void addAngleComboBox() {
        angleComboBox = addComboBox(Arrays.asList("Promo", "Offer", "Challenge", "Announcement"));
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

    }

    private void clearControls() {
        vBox.getChildren().retainAll(Arrays.asList(angleComboBox, teamPane.pane, violenceComboBox, header));
    }

    private void addPromoComboBox() {
        addComboBox(Arrays.asList("Self Hype", "Target"));
    }

    private void addJoinTeamComboBox() {
        addComboBox(Arrays.asList("New Tag Team", "New Stable", "Existing Stable"));
    }

    private void addShowComboBox() {
        addComboBox(Arrays.asList("Tonight", "Next Show", "Next Big Show", "Show x"));
    }

    private void addViolenceComboBox() {
        violenceComboBox = addComboBox(FXCollections.observableArrayList(ViolenceType.values()));
    }

    private void addPresentComboBox() {
        addComboBox(Arrays.asList("Present", "Not Present"));
    }

    private void addSuccessComboBox() {
        successComboBox = addComboBox(FXCollections.observableArrayList(SuccessType.values()));
    }

    private void addTargetComboBox() {
        targetComboBox = addComboBox(FXCollections.observableArrayList(targets));
    }

    private void addTimingComboBox() {
        addComboBox(Arrays.asList("Before", "During", "After"));
    }

    private ComboBox addComboBox(ObservableList items) {
        ComboBox comboBox = new ComboBox();
        comboBox.setItems(
                items);
        vBox.getChildren().add(comboBox);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(comboBox, new Insets(5));
        comboBox.getSelectionModel().selectFirst();
        return comboBox;
    }

    private ComboBox addComboBox(List<String> items) {
        ComboBox comboBox = new ComboBox();
        comboBox.getItems().addAll(
                items);
        vBox.getChildren().add(comboBox);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(comboBox, new Insets(5));
        comboBox.getSelectionModel().selectFirst();
        return comboBox;
    }

    public void setTeamTypeLabel(String string) {
        teamTypeLabel.setText(string);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        targets = new ArrayList<>();
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

    /**
     * @return the currentState
     */
    public TeamType getTeamType() {
        return teamType == null ? TeamType.DEFAULT : teamType;
    }

    public SegmentTeam getTeam() {
        SegmentTeam segmentTeam = new SegmentTeam(getTeamPaneController().getWorkers(), teamType);

        segmentTeam.setTarget(targetComboBox != null
                ? (SegmentTeam) targetComboBox.getSelectionModel().getSelectedItem()
                : segmentTeam
        );

        segmentTeam.setSuccess(successComboBox != null
                ? (SuccessType) successComboBox.getSelectionModel().getSelectedItem()
                : SuccessType.DRAW);

        return segmentTeam;
    }

}
