package wrestling.view.event;

import wrestling.model.segmentEnum.TeamType;
import wrestling.model.segmentEnum.TimingType;
import wrestling.model.segmentEnum.PromoType;
import wrestling.model.segmentEnum.JoinTeamType;
import wrestling.model.segmentEnum.AngleType;
import wrestling.model.segmentEnum.ShowType;
import wrestling.model.segmentEnum.PresenceType;
import wrestling.model.segmentEnum.SuccessType;
import wrestling.model.segmentEnum.ViolenceType;
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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
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
    private ComboBox timingComboBox;
    private ComboBox joinTeamComboBox;
    private ComboBox showComboBox;
    private ComboBox presentComboBox;

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

    private void setInterference() {
        teamType = TeamType.INTERFERENCE;
        vBox.getChildren().retainAll(teamPane.pane, header);
        addTargetComboBox();
        addTimingComboBox();
        addSuccessComboBox();
    }

    private void addAngleComboBox() {
        angleComboBox = addComboBox(FXCollections.observableArrayList(AngleType.values()), AngleType.label());
        angleComboBox.valueProperty().addListener(new ChangeListener<AngleType>() {
            @Override
            public void changed(ObservableValue ov, AngleType t, AngleType t1) {
                if (t1 != null) {
                    clearControls();
                    switch (t1) {
                        case PROMO:
                            addPromoComboBox();
                            break;
                        case OFFER:
                            addJoinTeamComboBox();
                            break;
                        case CHALLENGE:
                            addShowComboBox();
                            break;
                        default:
                            break;
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
        promoComboBox = addComboBox(FXCollections.observableArrayList(PromoType.values()), PromoType.label());
    }

    private void addJoinTeamComboBox() {
        joinTeamComboBox = addComboBox(FXCollections.observableArrayList(JoinTeamType.values()), JoinTeamType.label());
    }

    private void addShowComboBox() {
        showComboBox = addComboBox(FXCollections.observableArrayList(ShowType.values()), ShowType.label());
    }

    private void addViolenceComboBox() {
        violenceComboBox = addComboBox(FXCollections.observableArrayList(ViolenceType.values()), ViolenceType.label());
    }

    private void addPresentComboBox() {
        presentComboBox = addComboBox(FXCollections.observableArrayList(PresenceType.values()), PresenceType.label());
    }

    private void addSuccessComboBox() {
        successComboBox = addComboBox(FXCollections.observableArrayList(SuccessType.values()), SuccessType.label());
    }

    private void addTargetComboBox() {
        targetComboBox = addComboBox(FXCollections.observableArrayList(targets), "Target: ");
    }

    private void addTimingComboBox() {
        timingComboBox = addComboBox(FXCollections.observableArrayList(TimingType.values()), TimingType.label());
    }

    private ComboBox addComboBox(ObservableList items, String text) {

        Label label = new Label(text);
        label.setMaxWidth(Double.MAX_VALUE);
        GridPane.setConstraints(label, 0, 0);
        GridPane.setMargin(label, new Insets(5));

        ComboBox comboBox = new ComboBox();
        comboBox.setMaxWidth(Double.MAX_VALUE);
        GridPane.setConstraints(comboBox, 1, 0);
        GridPane.setColumnSpan(comboBox, 2);
        GridPane.setMargin(comboBox, new Insets(5));
        comboBox.setItems(items);

        GridPane gridPane = ViewUtils.gridPaneWithColumns(3);
        gridPane.getChildren().addAll(label, comboBox);
        gridPane.setMaxWidth(Double.MAX_VALUE);

        vBox.getChildren().add(gridPane);
        VBox.setMargin(gridPane, new Insets(5));

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

        segmentTeam.setTiming(timingComboBox != null
                ? (TimingType) timingComboBox.getSelectionModel().getSelectedItem()
                : TimingType.DURING);

        return segmentTeam;
    }

}
