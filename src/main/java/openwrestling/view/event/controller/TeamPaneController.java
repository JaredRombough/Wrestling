package openwrestling.view.event.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import openwrestling.model.SegmentItem;
import openwrestling.model.segmentEnum.TeamType;
import openwrestling.view.utility.LocalDragboard;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;

public class TeamPaneController extends ControllerBase implements Initializable {

    private static final double CELL_HEIGHT = 33;

    @FXML
    private ListView teamListView;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label teamNameLabel;

    private double defaultMainPaneHeight;

    private int teamNumber;

    private TeamType teamType;


    boolean toggle = true;
    public int getTeamNumber() {
        return this.teamNumber;
    }

    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        defaultMainPaneHeight = mainPane.getHeight();

    }

    public boolean removeSegmentItem(SegmentItem segmentItem) {

        if (teamListView.getItems().contains(segmentItem)) {
            teamListView.getItems().remove(segmentItem);
            if (teamType.equals(TeamType.ENTOURAGE)) {
                setVisible(false);
            }
            updateLabels();
            return true;
        }
        return false;
    }

    public void addSegmentItem(SegmentItem segmentItem) {
        teamListView.getItems().add(segmentItem);
        if (TeamType.ENTOURAGE.equals(teamType)) {
            setVisible(true);

        }
        updateLabels();
    }

    @Override
    public void updateLabels() {
        if (getSegmentItems() != null) {
            if (teamType != null) {
                switch (teamType) {
                    case TITLES:
                    case REF:
                    case BROADCAST:
                    case ENTOURAGE:
                        teamNameLabel.setText(teamType.description());
                        break;
                    default:
                        teamNameLabel.setText(gameController.getSegmentManager().generateTeamName(getSegmentItems(), teamType));
                        break;
                }
            }
        }
        updateTeamListViewHeight();
    }

    private void updateTeamListViewHeight() {
        if (mainPane.getChildren().isEmpty()) {
            mainPane.setMinHeight(0);
            mainPane.setMaxHeight(0);
        } else {
            int multiplier = teamListView.getItems().isEmpty() ? 1 : teamListView.getItems().size();

            double height = CELL_HEIGHT * multiplier + 5;

            mainPane.setMinHeight(defaultMainPaneHeight + CELL_HEIGHT + height);
            teamListView.setMinHeight(height);
        }

    }

    @Override
    public void initializeMore() {

        final EventHandler<DragEvent> dragOverHandler = new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                LocalDragboard ldb = LocalDragboard.getINSTANCE();
                if (ldb.hasInterface(SegmentItem.class) && teamType != null
                        && teamType.droppable(ldb.getValue(SegmentItem.class))) {
                    dragEvent.acceptTransferModes(TransferMode.MOVE);
                }
            }
        };

        setSegmentItemCellFactory(teamListView);

        teamListView.setOnDragOver(dragOverHandler);

        double height = CELL_HEIGHT;
        teamListView.setPrefHeight(height);
        mainPane.setPrefHeight(defaultMainPaneHeight + CELL_HEIGHT + height);

        updateLabels();
    }

    public void setVisible(boolean visible) {
        if (mainPane.getChildren().isEmpty() && visible) {
            mainPane.getChildren().add(gridPane);
        } else if (teamListView.getItems().isEmpty() && !visible) {
            mainPane.getChildren().clear();
        }
    }

    public void setLabelAction(EventHandler<MouseEvent> mouseEvent) {
        teamNameLabel.setOnMouseClicked(mouseEvent);
    }

    public boolean getVisible() {
        return !mainPane.getChildren().isEmpty();
    }

    public ObservableList<SegmentItem> getItems() {
        return teamListView.getItems();
    }

    public void setDragDroppedHandler(SegmentPaneController segmentPaneController, TeamPaneWrapper teamPaneWrapper) {
        teamListView.setOnDragDropped(new SegmentItemDragDropHandler(segmentPaneController, teamPaneWrapper, teamType));
    }

    private void setSegmentItemCellFactory(ListView listView) {
        listView.setCellFactory(lv -> new ListCell<SegmentItem>() {

            @Override
            public void updateItem(final SegmentItem segmentItem, boolean empty) {
                super.updateItem(segmentItem, empty);
                ViewUtils.initListCellForSegmentItemDragAndDrop(this, segmentItem, empty, teamType);
            }
        });
    }

    public List<SegmentItem> getSegmentItems() {
        return new ArrayList<>(teamListView.getItems());
    }

    public String getTeamName() {
        return teamNameLabel.getText();
    }

    /**
     * @param teamType the teamType to set
     */
    public void setTeamType(TeamType teamType) {
        this.teamType = teamType;
        if (teamType.equals(TeamType.ENTOURAGE)) {
            setVisible(false);
        }
        updateLabels();
    }

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof TeamType) {
            setTeamType((TeamType) obj);
        }
    }

    /**
     * @return the teamNameLabel
     */
    public Label getTeamNameLabel() {
        return teamNameLabel;
    }

    /**
     * @return the mainPane
     */
    public AnchorPane getMainPane() {
        return mainPane;
    }

}
