package wrestling.view.results.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import wrestling.model.SegmentItem;
import wrestling.model.modelView.WorkerView;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class ResultsCardController extends ControllerBase implements Initializable {

    @FXML
    private StackPane border;

    @FXML
    private ImageView imageView;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label nameLabel;

    private int width;
    private int height;
    private int padding;
    private SegmentItem segmentItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        width = 180;
        height = 180;
        padding = 20;
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);

    }

    @Override
    public void setCurrent(Object obj) {

        if (obj instanceof SegmentItem) {
            setCurrentWorker((SegmentItem) obj);
        } else if (obj instanceof String) {
            setCurrentString((String) obj);
        }
    }

    private void setCurrentWorker(SegmentItem segmentItem) {
        this.segmentItem = segmentItem;
        String imgString = segmentItem.getImageString();
        nameLabel.setText(segmentItem.getLongName());
        border.setMinSize(width + padding, height + padding);
        border.setPrefSize(width + padding, height + padding);
        ViewUtils.showImage(String.format(mainApp.getPicsFolder().toString() + "\\" + imgString),
                border,
                imageView,
                mainApp.getDefaultWorkerImage(segmentItem));
        if (!border.isVisible()) {
            border.setVisible(true);
        }
    }

    private void setCurrentString(String string) {
        Text text = new Text((String) string);
        anchorPane.getChildren().clear();
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(text);
        ViewUtils.anchorRegionToParent(anchorPane, vbox);
        anchorPane.getStyleClass().clear();
        anchorPane.getStyleClass().add("resultsCardText");
        anchorPane.setMaxWidth(text.getBoundsInParent().getWidth());
        anchorPane.setMaxHeight(text.getBoundsInParent().getHeight());
    }

    public void setNameLabelVisibile(boolean visible) {
        nameLabel.setVisible(visible);
    }

}
