package wrestling.view.event.controller;

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
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import wrestling.model.SegmentItem;
import wrestling.model.segmentEnum.TeamType;
import wrestling.view.utility.LocalDragboard;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class TeamPaneController extends ControllerBase implements Initializable {

    private static final double CELL_HEIGHT = 33;

    @FXML
    private ListView teamListView;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Label teamNameLabel;

    private double defaultMainPaneHeight;

    private int teamNumber;

    private TeamType teamType;

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

    public void removeSegmentItem(SegmentItem segmentItem) {

        if (teamListView.getItems().contains(segmentItem)) {
            teamListView.getItems().remove(segmentItem);
            updateLabels();
        }
    }

    @Override
    public void updateLabels() {

        updateTeamListViewHeight();

        if (getSegmentItems() != null) {
            teamNameLabel.setText(gameController.getSegmentManager().generateTeamName(getSegmentItems()));
        }

    }

    private void updateTeamListViewHeight() {

        int multiplier = teamListView.getItems().isEmpty() ? 1 : teamListView.getItems().size();

        double height = CELL_HEIGHT * multiplier + 5;

        mainPane.setMinHeight(defaultMainPaneHeight + CELL_HEIGHT + height);
        teamListView.setMinHeight(height);
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

    public ObservableList<SegmentItem> getItems() {
        return teamListView.getItems();
    }

    public void setDragDropHandler(SegmentPaneController segmentPaneController, EventScreenController eventScreenController) {
        teamListView.setOnDragDropped(new SegmentItemDragDropHandler(segmentPaneController, eventScreenController, this));
    }

    private void setSegmentItemCellFactory(ListView listView) {
        listView.setCellFactory(lv -> new ListCell<SegmentItem>() {

            @Override
            public void updateItem(final SegmentItem segmentItem, boolean empty) {
                super.updateItem(segmentItem, empty);
                ViewUtils.initListCellForSegmentItemDragAndDrop(this, segmentItem, empty);
            }

        });
    }

    public List<SegmentItem> getSegmentItems() {

        return new ArrayList<>(teamListView.getItems());

    }

    public void setSegmentItems(List<SegmentItem> segmentItems) {
        teamListView.getItems().clear();
        teamListView.getItems().addAll(segmentItems);
        updateLabels();
    }

    public String getTeamName() {
        return teamNameLabel.getText();
    }

    /**
     * @param teamType the teamType to set
     */
    public void setTeamType(TeamType teamType) {
        this.teamType = teamType;
    }

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof TeamType) {
            setTeamType((TeamType) obj);
        }
    }

}
