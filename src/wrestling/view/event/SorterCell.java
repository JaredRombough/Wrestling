package wrestling.view.event;

import java.util.Collections;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import wrestling.model.modelView.SegmentView;
import wrestling.view.utility.LocalDragboard;

public class SorterCell extends ListCell<EventScreenController.SegmentNameItem> {

    private Label myLabel;
    private ListView segmentListView;

    public SorterCell(
            List<Pane> segmentPanes,
            List<SegmentPaneController> segmentPaneControllers,
            List<SegmentView> segments,
            ListView listView,
            EventScreenController eventScreenController) {
        ListCell thisCell = this;
        segmentListView = listView;

        setOnDragDetected((MouseEvent event) -> {
            if (getItem() == null) {

                return;
            }

            Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            content.putString(getText());
            LocalDragboard.getINSTANCE().putValue(EventScreenController.SegmentNameItem.class, getItem());
            content.putString(getItem().name.get());

            dragboard.setContent(content);

            event.consume();
        });

        setOnDragOver(event -> {
            if (event.getGestureSource() != thisCell
                    && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }

            event.consume();
        });

        setOnDragEntered(event -> {
            if (event.getGestureSource() != thisCell
                    && event.getDragboard().hasString()) {
                setOpacity(0.3);
            }
        });

        setOnDragExited(event -> {
            if (event.getGestureSource() != thisCell
                    && event.getDragboard().hasString()) {
                setOpacity(1);
            }
        });

        setOnDragDropped((DragEvent event) -> {

            if (getGraphic() == null) {
                return;

            }

            boolean success = false;

            LocalDragboard ldb = LocalDragboard.getINSTANCE();
            if (ldb.hasType(EventScreenController.SegmentNameItem.class)) {
                EventScreenController.SegmentNameItem segmentNameItem = ldb.getValue(EventScreenController.SegmentNameItem.class);
                ObservableList<EventScreenController.SegmentNameItem> items = getListView().getItems();
                int draggedIdx = items.indexOf(segmentNameItem);
                int thisIdx = items.indexOf(getItem());

                //swap all parallel arrays associated with the segment
                Collections.swap(items, thisIdx, draggedIdx);
                Collections.swap(segmentPanes, thisIdx, draggedIdx);
                Collections.swap(segmentPaneControllers, thisIdx, draggedIdx);
                Collections.swap(segments, thisIdx, draggedIdx);

                eventScreenController.setCurrentSegmentNumber(thisIdx);

                segmentListView.getSelectionModel().select(segmentNameItem);
                success = true;
            }

            event.setDropCompleted(success);

            event.consume();
        });

        setOnDragDone(DragEvent::consume);
    }

    @Override
    protected void updateItem(EventScreenController.SegmentNameItem item, boolean empty) {

        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {

            myLabel = new Label(item.name.getValue());
            myLabel.setTextAlignment(TextAlignment.CENTER);

            myLabel.setWrapText(true);
            myLabel.setMaxWidth(segmentListView.getWidth() - 40);

            myLabel.getStyleClass().add("sorterLabel");
            setGraphic(myLabel);

        }

    }
}
