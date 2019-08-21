package openwrestling.view.event.controller;

import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import openwrestling.model.SegmentItem;
import openwrestling.model.modelView.StaffView;
import openwrestling.model.modelView.TitleView;
import openwrestling.model.segmentEnum.TeamType;
import openwrestling.model.utility.StaffUtils;
import openwrestling.view.utility.LocalDragboard;

public class SegmentItemDragDropHandler implements EventHandler<DragEvent> {

    private final TeamPaneWrapper teamPaneWrapper;
    private final SegmentPaneController segmentPaneController;
    private final TeamType teamType;

    public SegmentItemDragDropHandler(
            SegmentPaneController segmentPaneController,
            TeamPaneWrapper teamPaneController,
            TeamType teamType) {
        this.teamPaneWrapper = teamPaneController;
        this.segmentPaneController = segmentPaneController;
        this.teamType = teamType;
    }

    @Override
    public void handle(DragEvent event) {

        LocalDragboard ldb = LocalDragboard.getINSTANCE();
        if (ldb.hasInterface(SegmentItem.class)) {
            SegmentItem segmentItem = ldb.getValue(SegmentItem.class);
            TeamType sourceType = ldb.getValue(TeamType.class);

            segmentItem.getSegmentItems().forEach(item -> segmentPaneController.removeSegmentItem(item, sourceType, teamType));
            segmentItem.getSegmentItems().forEach(item -> teamPaneWrapper.addSegmentItem(item, teamType));

            if (StaffUtils.isRef(segmentItem)) {
                segmentPaneController.setRef((StaffView) segmentItem);
            }

            if (segmentItem instanceof TitleView) {
                TitleView titleView = (TitleView) segmentItem;
                if (!titleView.getChampions().isEmpty()) {
                    segmentPaneController.addTeam(((TitleView) segmentItem).getChampions(), true);
                }
            }

            segmentPaneController.itemDroppedInSegment();

            ldb.clearAll();
        }
    }
}
