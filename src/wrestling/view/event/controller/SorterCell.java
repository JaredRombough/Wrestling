package wrestling.view.event.controller;

import java.util.Collections;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import wrestling.model.manager.SegmentManager;
import wrestling.model.segmentEnum.SegmentValidation;
import wrestling.view.utility.LocalDragboard;
import wrestling.view.utility.ViewUtils;

public class SorterCell extends ListCell<EventScreenController.SegmentNameItem> {

    private Label myLabel;
    private ListView segmentListView;

    private Button xButton;
    private SegmentManager segmentManager;

    public SorterCell(
            List<Pane> segmentPanes,
            List<SegmentPaneController> segmentPaneControllers,
            ListView listView,
            EventScreenController eventScreenController,
            SegmentManager segmentManager) {
        ListCell thisCell = this;
        segmentListView = listView;
        this.segmentManager = segmentManager;
        xButton = ViewUtils.getXButton();
        xButton.setOnAction(e -> {
            eventScreenController.removeSegment(getListView().getItems().indexOf(getItem()));
        });

        setOnDragDetected((MouseEvent event) -> {
            if (getItem() == null) {

                return;
            }

            Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            content.putString(getText());
            LocalDragboard.getINSTANCE().putValue(EventScreenController.SegmentNameItem.class, getItem());
            content.putString(getItem().segment.get().toString());

            dragboard.setContent(content);

            event.consume();
        });

        setOnDragOver(event -> {
            if (event.getGestureSource() != thisCell
                    && event.getDragboard().hasString()
                    && event.getGestureSource() instanceof SorterCell) {
                event.acceptTransferModes(TransferMode.MOVE);
            }

            event.consume();
        });

        setOnDragEntered(event -> {
            if (event.getGestureSource() != thisCell
                    && event.getDragboard().hasString()
                    && event.getGestureSource() instanceof SorterCell) {
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

                eventScreenController.segmentsChanged();

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

            SegmentValidation segmentValidation = item.segment.get().getValidationStatus();
            Label validation = new Label(segmentValidation.getSymbol());
            validation.getStyleClass().add(segmentValidation.getCss());

            myLabel = new Label(
                    segmentManager.getSegmentTitle(item.segment.get())
                    + String.format(" (%d min)", item.segment.get().getSegment().getSegmentLength())
                    + "\n"
                    + segmentManager.getSegmentString(item.segment.get()));
            myLabel.setTextAlignment(TextAlignment.CENTER);

            myLabel.setWrapText(true);
            myLabel.setMaxWidth(segmentListView.getWidth() - 90);

            myLabel.getStyleClass().add("sorterLabel");
            HBox.setHgrow(myLabel, Priority.ALWAYS);
            HBox hBox = new HBox();
            hBox.getChildren().add(validation);
            hBox.getChildren().add(myLabel);
            hBox.getChildren().add(xButton);
            setGraphic(hBox);

        }

    }
}
