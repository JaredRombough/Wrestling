package wrestling.view.event.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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
    private GameScreen entouragePane;
    private TeamPaneController entouragePaneController;
    
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
            vBox.getChildren().retainAll(teamPane.pane, entouragePane.pane, header);
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
                    vBox.getChildren().retainAll(teamPane.pane, header);
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
                case REF:
                case TITLES:
                case BROADCAST:
                    vBox.getChildren().retainAll(teamPane.pane, header);
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
    
    public void setDragDroppedHandler(SegmentPaneController segmentPaneController) {
        teamPaneController.setDragDroppedHandler(segmentPaneController, this);
        entouragePaneController.setDragDroppedHandler(segmentPaneController, this);
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
    
    public void setHeaderVisible(boolean visible) {
        vBox.getChildren().remove(header);
    }
    
    public void setTeamNameVisible(boolean visible) {
        teamPaneController.getTeamNameLabel().setVisible(visible);
    }
    
    public void changeSegmentType() {
        if (!teamType.equals(TeamType.INTERFERENCE)) {
            vBox.getChildren().retainAll(teamPane.pane, entouragePane.pane, header);
        }
    }
    
    private void setInterference() {
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
            vBox.getChildren().retainAll(teamPane.pane, entouragePane.pane, header, wrapper.getGridPane());
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
        draggingTab = new SimpleObjectProperty<>();
        getDraggingTab().set(anchorPane);
        autoSet = true;
    }
    
    @Override
    public void initializeMore() {
        EventHandler<MouseEvent> mouseEvent = (MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                if (entouragePaneController.getSegmentItems().isEmpty()) {
                    entouragePaneController.setVisible(!entouragePaneController.getVisible());
                    entouragePaneController.updateLabels();
                }
            }
        };
        teamPane = ViewUtils.loadScreenFromResource(ScreenCode.TEAM_PANE, mainApp, gameController);
        entouragePane = ViewUtils.loadScreenFromResource(ScreenCode.TEAM_PANE, mainApp, gameController);
        vBox.getChildren().addAll(teamPane.pane, entouragePane.pane);
        
        teamPaneController = (TeamPaneController) teamPane.controller;
        entouragePaneController = (TeamPaneController) entouragePane.controller;
        
        teamPaneController.setLabelAction(mouseEvent);
        entouragePaneController.setLabelAction(mouseEvent);
        
        entouragePaneController.setTeamType(TeamType.ENTOURAGE);
        xButton = ViewUtils.getXButton();
        header.getChildren().add(xButton);
        
    }

    /**
     * @return the currentState
     */
    public TeamType getTeamType() {
        return teamType == null ? TeamType.DEFAULT : teamType;
    }
    
    public List<SegmentItem> getSegmentItems() {
        return teamPaneController.getSegmentItems();
    }
    
    public List<SegmentItem> getEntourage() {
        return entouragePaneController.getSegmentItems();
    }
    
    public SegmentTeam getSegmentTeam() {
        List<WorkerView> workers = ModelUtils.getWorkersFromSegmentItems(getSegmentItems());
        
        SegmentTeam segmentTeam = new SegmentTeam(workers, teamType);
        segmentTeam.setEntourage(ModelUtils.getWorkersFromSegmentItems(entouragePaneController.getItems()));
        
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
    
    public void removeSegmentItem(SegmentItem segmentItem) {
        removeSegmentItem(segmentItem, TeamType.DEFAULT, TeamType.DEFAULT);
    }
    
    public void removeSegmentItem(SegmentItem segmentItem, TeamType sourceType, TeamType targetType) {
        if (targetType.getShared().contains(teamType)
                || entouragePaneController.getItems().contains(segmentItem) && targetType.getShared().contains(TeamType.ENTOURAGE)) {
        } else {
            teamPaneController.removeSegmentItem(segmentItem);
            entouragePaneController.removeSegmentItem(segmentItem);
            if (segmentItem instanceof WorkerView) {
                WorkerView workerView = (WorkerView) segmentItem;
                workerView.getEntourage().forEach(item -> entouragePaneController.removeSegmentItem(item));
            }
        }
        
    }
    
    public void addSegmentItems(List<? extends SegmentItem> segmentItems) {
        segmentItems.forEach(item -> addSegmentItem(item));
    }
    
    public void setSegmentItems(List<? extends SegmentItem> segmentItems) {
        List<SegmentItem> toRemove = new ArrayList<>(teamPaneController.getSegmentItems());
        toRemove.forEach(item -> removeSegmentItem(item));
        segmentItems.forEach(item -> addSegmentItem(item));
    }
    
    public void addSegmentItem(SegmentItem segmentItem, TeamType targetType) {
        if (TeamType.ENTOURAGE.equals(targetType)) {
            entouragePaneController.addSegmentItem(segmentItem);
        } else {
            addSegmentItem(segmentItem);
        }
    }
    
    public void addSegmentItem(SegmentItem segmentItem) {
        teamPaneController.addSegmentItem(segmentItem);
        if (segmentItem instanceof WorkerView) {
            WorkerView workerView = (WorkerView) segmentItem;
            workerView.getEntourage().forEach(item -> entouragePaneController.addSegmentItem(item));
        }
    }
    
    public void setTeamNumber(int teamNumber) {
        teamPaneController.setTeamNumber(teamNumber);
    }
    
    public void setOnXButton(EventHandler<ActionEvent> value) {
        xButton.setOnAction(value);
    }
    
    public void setXButtonVisible(boolean visible) {
        xButton.setVisible(visible);
    }

    /**
     * @return the draggingTab
     */
    public ObjectProperty<AnchorPane> getDraggingTab() {
        return draggingTab;
    }
}
