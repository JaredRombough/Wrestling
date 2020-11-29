package openwrestling.view.results.controller;

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
import openwrestling.model.SegmentItem;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.Worker;
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
        if (obj instanceof Promotion) {
            setCurrentItem((Promotion) obj,
                    mainApp.getLogosFolder().toString(),
                    null);
        } else if (obj instanceof Worker) {
            setCurrentItem((Worker) obj,
                    mainApp.getPicsFolder().toString(),
                    mainApp.getDefaultWorkerImage((Worker) obj)
            );
        } else if (obj instanceof StaffMember) {
            setCurrentItem((StaffMember) obj,
                    mainApp.getPicsFolder().toString(),
                    mainApp.getDefaultWorkerImage((StaffMember) obj)
            );
        } else if (obj instanceof String) {
            setCurrentString((String) obj);
        }
    }


    private void setCurrentItem(SegmentItem segmentItem, String imageFolderName, Image defaultImage) {
        String imgString = segmentItem.getImageFileName();
        nameLabel.setText(segmentItem.getLongName());
        ViewUtils.showImage(imageFolderName + "\\" + imgString,
                border,
                imageView,
                defaultImage);
//        if (!border.isVisible()) {
//            border.setVisible(true);
//        }
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
