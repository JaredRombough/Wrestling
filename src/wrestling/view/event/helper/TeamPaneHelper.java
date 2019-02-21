package wrestling.view.event.helper;

import javafx.beans.property.ObjectProperty;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import wrestling.view.event.controller.SegmentPaneController;

public class TeamPaneHelper {

    private static final String TEAM_PANE_TAB_DRAG_KEY = "anchorPane";

    public static void initTeamPaneForSorting(Pane teamPane,
            ObjectProperty<AnchorPane> draggingTab,
            SegmentPaneController segmentPaneController) {

        teamPane.setOnDragDetected(e -> {
            Dragboard dragboard = teamPane.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(TEAM_PANE_TAB_DRAG_KEY);
            dragboard.setContent(clipboardContent);
        });

        teamPane.setOnDragOver((DragEvent event) -> {
            final Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()
                    && TEAM_PANE_TAB_DRAG_KEY.equals(dragboard.getString())
                    && draggingTab.get() != null) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });

        teamPane.setOnDragDropped((final DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                Pane parent = (Pane) teamPane.getParent();
                Object source = event.getGestureSource();
                int sourceIndex = parent.getChildren().indexOf(source);
                int targetIndex = parent.getChildren().indexOf(teamPane);
                segmentPaneController.swapTeams(sourceIndex, targetIndex);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

}
