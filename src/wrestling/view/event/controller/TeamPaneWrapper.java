package wrestling.view.event.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import wrestling.model.SegmentItem;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.modelView.WorkerView;
import wrestling.model.segmentEnum.OutcomeType;
import wrestling.model.segmentEnum.PresenceType;
import wrestling.model.segmentEnum.ResponseType;
import wrestling.model.segmentEnum.SuccessType;
import wrestling.model.segmentEnum.TeamType;
import wrestling.model.segmentEnum.TimingType;
import wrestling.model.utility.ModelUtils;
import wrestling.view.utility.ButtonWrapper;
import wrestling.view.utility.GameScreen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class TeamPaneWrapper extends ControllerBase implements Initializable {

    private static final String TAB_DRAG_KEY = "anchorpane";

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private VBox vBox;

    @FXML
    private HBox header;

    private Button xButton;

    @FXML
    private Label teamTypeLabel;

    private GameScreen teamPane;
    private TeamPaneController teamPaneController;
    private ObjectProperty<AnchorPane> draggingTab;

    private ResponseType responseType;
    private PresenceType presenceType;
    private SuccessType successType;
    private TimingType timingType;

    private ComboBox<SegmentTeam> targetComboBox;
    private List<SegmentTeam> targets;

    private TeamType teamType;
    private OutcomeType outcomeType;

    private boolean autoSet;

    public void setTeamType(TeamType newTeamType) {
        if (teamType != newTeamType) {
            vBox.getChildren().retainAll(teamPane.pane, header);
            teamPane.controller.setCurrent(newTeamType);

            teamType = newTeamType;
            setTeamTypeLabel(newTeamType.toString());
            switch (teamType) {
                case PROMO_TARGET:
                    setPromoTarget();
                    break;
                case OFFEREE:
                case CHALLENGED:
                    setResponse();
                    break;
                case INTERFERENCE:
                    setInterference();
                    break;
                case INTERVIEWER:
                    break;
                case WINNER:
                    break;
                case LOSER:
                    break;
                case DRAW:
                    break;
            }
        }
    }

    public void setTargets(List<SegmentTeam> teams) {
        targets = teams;

        if (targetComboBox != null) {
            updateTargetComboBox();
        }
    }

    public void setDragDropHandler(SegmentPaneController segmentPaneController,
            EventScreenController eventScreenController) {
        getTeamPaneController().setDragDropHandler(
                segmentPaneController,
                eventScreenController);
    }

    @Override
    public void updateLabels() {
        teamPaneController.updateLabels();
    }

    private void updateTargetComboBox() {
        int previousIndex = -1;
        String previousName = "";
        if (!targetComboBox.getItems().isEmpty()) {
            previousIndex = targetComboBox.getSelectionModel().getSelectedIndex();
            previousName = targetComboBox.getSelectionModel().getSelectedItem().toString();
        }
        targetComboBox.getItems().clear();

        if (targets.size() > 1) {
            List<WorkerView> emptyList = new ArrayList<>();
            targets.add(new SegmentTeam(emptyList, TeamType.EVERYONE));
        }

        ObservableList list = FXCollections.observableArrayList(targets);

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

    public void setHeaderVisible(boolean visible) {
        vBox.getChildren().remove(header);
    }

    public void setTeamNameVisible(boolean visible) {
        getTeamPaneController().getTeamNameLabel().setVisible(visible);
    }

    public void changeSegmentType() {
        if (!teamType.equals(TeamType.INTERFERENCE)) {
            vBox.getChildren().retainAll(teamPane.pane, header);
        }
    }

    private void setInterference() {
        teamType = TeamType.INTERFERENCE;
        addTargetComboBox();
        addTimingButtons();
        addSuccessButtons();
    }

    private void setResponse() {
        ButtonWrapper wrapper = addButtonWrapper(FXCollections.observableArrayList(ResponseType.values()));
        for (Button button : wrapper.getButtons()) {
            button.setOnAction((ActionEvent event) -> {
                responseType = (ResponseType) wrapper.updateSelected(button);
            });
        }
        wrapper.updateSelected(wrapper.getItems().indexOf(responseType));
    }

    private void setPromoTarget() {
        ButtonWrapper wrapper = addButtonWrapper(FXCollections.observableArrayList(PresenceType.values()));
        for (Button button : wrapper.getButtons()) {
            button.setOnAction((ActionEvent event) -> {
                if (!((PresenceType) wrapper.updateSelected(button)).equals(presenceType)) {
                    presenceType = (PresenceType) wrapper.updateSelected(button);
                    setPresent(wrapper);
                }

            });
        }
        wrapper.updateSelected(wrapper.getItems().indexOf(presenceType));
        setPresent(wrapper);

    }

    private void setPresent(ButtonWrapper wrapper) {
        if (presenceType.equals(PresenceType.PRESENT)) {
            addSuccessButtons();
        } else {
            vBox.getChildren().retainAll(teamPane.pane, header, wrapper.getGridPane());
        }
    }

    private void addSuccessButtons() {
        ButtonWrapper wrapper = addButtonWrapper(FXCollections.observableArrayList(SuccessType.values()));
        for (Button button : wrapper.getButtons()) {
            button.setOnAction((ActionEvent event) -> {
                successType = (SuccessType) wrapper.updateSelected(button);
            });
        }
        wrapper.updateSelected(wrapper.getItems().indexOf(successType));

    }

    private void addTargetComboBox() {
        targetComboBox = (ComboBox) ViewUtils.addComboBoxWrapperToVBox(
                FXCollections.observableArrayList(targets),
                "Target: ",
                vBox).region;
        targetComboBox.getSelectionModel().selectFirst();
    }

    private void addTimingButtons() {
        ButtonWrapper wrapper = addButtonWrapper(FXCollections.observableArrayList(TimingType.values()));
        for (Button button : wrapper.getButtons()) {
            button.setOnAction((ActionEvent event) -> {
                timingType = (TimingType) wrapper.updateSelected(button);
            });
        }
        wrapper.updateSelected(wrapper.getItems().indexOf(timingType));
    }

    private ButtonWrapper addButtonWrapper(ObservableList items) {

        ButtonWrapper wrapper = new ButtonWrapper(items);
        vBox.getChildren().add(wrapper.getGridPane());
        VBox.setMargin(wrapper.getGridPane(), new Insets(5));

        return wrapper;
    }

    public void setTeamTypeLabel(String string) {
        teamTypeLabel.setText(string);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        targets = new ArrayList<>();
        responseType = ResponseType.YES;
        presenceType = PresenceType.ABSENT;
        successType = SuccessType.WIN;
        timingType = TimingType.DURING;
        autoSet = true;
    }

    @Override
    public void initializeMore() {
        teamPane = ViewUtils.loadScreenFromResource(ScreenCode.TEAM_PANE, mainApp, gameController);
        vBox.getChildren().add(teamPane.pane);
        teamPaneController = (TeamPaneController) teamPane.controller;
        preparePaneForSorting();
        xButton = ViewUtils.getXButton();
        header.getChildren().add(xButton);

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

    /**
     * @return the currentState
     */
    public TeamType getTeamType() {
        return teamType == null ? TeamType.DEFAULT : teamType;
    }

    public List<SegmentItem> getSegmentItems() {
        return getTeamPaneController().getSegmentItems();
    }

    public SegmentTeam getSegmentTeam() {
        List<WorkerView> workers = ModelUtils.getWorkersFromSegmentItems(getSegmentItems());

        SegmentTeam segmentTeam = new SegmentTeam(workers, teamType);

        segmentTeam.setTarget(targetComboBox != null
                ? targetComboBox.getSelectionModel().getSelectedItem()
                : segmentTeam
        );

        segmentTeam.setOutcome(outcomeType != null
                ? outcomeType
                : null);

        segmentTeam.setTiming(timingType);
        segmentTeam.setSuccess(successType);
        segmentTeam.setPresence(presenceType);

        return segmentTeam;
    }

    /**
     * @param outcomeType the outcomeType to set
     */
    public void setOutcomeType(OutcomeType outcomeType) {
        this.outcomeType = outcomeType;
    }

    /**
     * @return the teamPaneController
     */
    public TeamPaneController getTeamPaneController() {
        return teamPaneController;
    }

    /**
     * @return the autoSet
     */
    public boolean isAutoSet() {
        return autoSet;
    }

    /**
     * @param autoSet the autoSet to set
     */
    public void setAutoSet(boolean autoSet) {
        this.autoSet = autoSet;
    }

}
