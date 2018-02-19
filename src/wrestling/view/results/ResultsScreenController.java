package wrestling.view.results;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import wrestling.model.Event;
import wrestling.view.interfaces.ControllerBase;

public class ResultsScreenController extends ControllerBase implements Initializable {

    @FXML
    private AnchorPane resultsDisplayPane;

    @FXML
    private Button nextButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nextButton.setText("Next");

    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource() == nextButton) {
        }
    }

}
