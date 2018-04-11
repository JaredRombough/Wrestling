package wrestling.view.event;

import wrestling.model.segmentEnum.TeamType;
import wrestling.model.segmentEnum.TimingType;
import wrestling.model.segmentEnum.SuccessType;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import wrestling.model.Worker;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.segmentEnum.OutcomeType;
import wrestling.model.segmentEnum.PresenceType;
import wrestling.model.segmentEnum.ResponseType;
import wrestling.view.utility.ButtonWrapper;
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

    private Button xButton;

    @FXML
    private Label teamTypeLabel;

    private Screen teamPane;
    private static final String TAB_DRAG_KEY = "anchorpane";
    private ObjectProperty<AnchorPane> draggingTab;

    private ComboBoxWrapper targetComboBox;

    private List<ButtonWrapper> buttonWrappers;

    private ResponseType responseType;
    private PresenceType presenceType;
    private SuccessType successType;
    private TimingType timingType;

    private List<SegmentTeam> targets;

    private TeamType teamType;
    private OutcomeType outcomeType;

    public void setTeamType(TeamType newTeamType) {
        if (teamType != newTeamType) {
            vBox.getChildren().retainAll(teamPane.pane, header);

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

    private void updateTargetComboBox() {
        int previousIndex = -1;
        String previousName = "";
        if (!targetComboBox.box.getItems().isEmpty()) {
            previousIndex = targetComboBox.box.getSelectionModel().getSelectedIndex();
            previousName = targetComboBox.box.getSelectionModel().getSelectedItem().toString();
        }
        targetComboBox.box.getItems().clear();

        if (targets.size() > 1) {
            List<Worker> emptyList = new ArrayList<>();
            targets.add(new SegmentTeam(emptyList, TeamType.EVERYONE));
        }

        ObservableList list = FXCollections.observableArrayList(targets);

        targetComboBox.box.setItems(list);

        boolean nameMatch = false;
        for (int i = 0; i < targetComboBox.box.getItems().size(); i++) {
            if (targetComboBox.box.getItems().get(i).toString().equals(previousName)) {
                targetComboBox.box.getSelectionModel().select(i);
                nameMatch = true;
                break;
            }
        }
        if (!nameMatch && previousIndex != -1
                && targetComboBox.box.getItems().size() > previousIndex) {
            targetComboBox.box.getSelectionModel().select(previousIndex);
        } else if (!nameMatch) {
            targetComboBox.box.getSelectionModel().selectFirst();
        }
    }

    public Button getXButton() {
        return xButton;
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
        targetComboBox = addComboBox(FXCollections.observableArrayList(targets), "Target: ");
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

    private ComboBoxWrapper addComboBox(ObservableList items, String text) {

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

        return new ComboBoxWrapper(gridPane, comboBox);
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
    }

    @Override
    public void initializeMore() {
        teamPane = ViewUtils.loadScreenFromResource(ScreenCode.TEAM_PANE, mainApp, gameController);
        vBox.getChildren().add(teamPane.pane);
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
                ? (SegmentTeam) targetComboBox.box.getSelectionModel().getSelectedItem()
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

    private class ComboBoxWrapper {

        public GridPane wrapper;
        public ComboBox box;

        public ComboBoxWrapper(GridPane gridPane, ComboBox comboBox) {
            this.wrapper = gridPane;
            this.box = comboBox;
        }
    }

}
