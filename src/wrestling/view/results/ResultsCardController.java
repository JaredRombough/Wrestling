package wrestling.view.results;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import wrestling.model.Worker;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class ResultsCardController extends ControllerBase implements Initializable {

    @FXML
    private StackPane border;

    @FXML
    private ImageView imageView;

    private int width;
    private int height;
    private Worker worker;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        width = 150;
        height = 210;
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
    }

    @Override
    public void setCurrent(Object obj) {

        if (obj instanceof Worker) {
            worker = (Worker) obj;
            String imgString = worker.getImageString();
            if (imgString != null && !imgString.isEmpty()) {
                ViewUtils.showImage(String.format(mainApp.getPicsFolder().toString() + "\\" + imgString),
                        border,
                        imageView);
            } else {
                setCurrent(worker.getName());
            }

        } else if (obj instanceof String) {
            Text text = new Text((String) obj);
            border.getChildren().clear();
            border.getChildren().add(text);
            border.getStyleClass().clear();
            border.getStyleClass().add("resultsCardText");
            if (worker == null) {
                border.setMaxWidth(text.getBoundsInParent().getWidth());
            } else {
                border.setMinWidth(text.getBoundsInParent().getWidth() + 20);
            }

        }
    }

}
