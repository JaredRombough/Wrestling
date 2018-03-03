package wrestling.view.event;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import wrestling.view.utility.Screen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class TeamPaneWrapper extends ControllerBase implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private VBox vBox;

    private Screen teamPane;
    private SegmentPaneController segmentPaneController;
    private static final String TAB_DRAG_KEY = "anchorpane";
    private ObjectProperty<AnchorPane> draggingTab;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        anchorPane.setOnDragDropped((final DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                Pane parent = (Pane) anchorPane.getParent();
                Object source = event.getGestureSource();
                int sourceIndex = parent.getChildren().indexOf(source);

                int targetIndex = parent.getChildren().indexOf(anchorPane);

                List<Node> nodes = new ArrayList<>(parent.getChildren());
                if (sourceIndex < targetIndex) {
                    Collections.rotate(
                            nodes.subList(sourceIndex, targetIndex + 1), -1);
                } else {
                    Collections.rotate(
                            nodes.subList(targetIndex, sourceIndex + 1), 1);
                }
                parent.getChildren().clear();
                parent.getChildren().addAll(nodes);
                //tell the segmentPaneControlller that the team priority has changed
                segmentPaneController.swapTeamIndices(sourceIndex, targetIndex);

                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }
    
    public TeamPaneController getTeamPaneController() {
        return (TeamPaneController)teamPane.controller;
    }

}
