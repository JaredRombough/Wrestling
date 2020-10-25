package openwrestling.view.results.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import openwrestling.model.SegmentItem;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;

import java.net.URL;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setWorkerInfoMode() {
        setWorkerInfoMode(100);
    }

    public void setWorkerInfoMode(int imageSize) {
        width = imageSize;
        height = imageSize;
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);
        nameLabel.setVisible(false);
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
        String imgString = segmentItem.getImageString();
        nameLabel.setText(segmentItem.getLongName());
        ViewUtils.showImage(mainApp.getPicsFolder().toString() + "\\" + imgString,
                border,
                imageView,
                mainApp.getDefaultWorkerImage(segmentItem));
        if (!border.isVisible()) {
            border.setVisible(true);
        }
    }

    private void setCurrentString(String string) {
        Text text = new Text(string);
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

    public void setNameLabelVisible(boolean visible) {
        nameLabel.setVisible(visible);
    }

}
