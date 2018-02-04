package wrestling.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;

public class NextDayScreenController extends ControllerBase implements Initializable {

    @FXML
    public AnchorPane displayPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger = LogManager.getLogger(getClass());
    }

    @Override
    public void initializeMore() {

    }

    public void setLoadingMessage(String string) {
        Text text = new Text(string);
        displayPane.getChildren().clear();
        displayPane.getChildren().add(text);

    }

}
