package wrestling.view.results;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
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
            Worker currentWorker = (Worker) obj;

            ViewUtils.showImage(String.format(mainApp.getPicsFolder().toString() + "\\" + currentWorker.getImageString()),
                    border,
                    imageView);
        } else if (obj instanceof String) {
            border.getChildren().clear();
            border.getChildren().add(new Text((String) obj));
        }
    }

}
