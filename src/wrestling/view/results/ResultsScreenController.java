package wrestling.view.results;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import wrestling.model.modelView.EventView;
import wrestling.view.utility.interfaces.ControllerBase;

public class ResultsScreenController extends ControllerBase implements Initializable {

    @FXML
    private Text titleText;

    @FXML
    private AnchorPane resultsDisplayPane;

    @FXML
    private Button nextButton;

    private EventView currentEvent;

    private int currentSegmentViewIndex;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nextButton.setText("Next");

    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource() == nextButton) {
        }
    }

    @Override
    public void setCurrent(Object object) {
        if (object instanceof EventView) {
            currentEvent = (EventView) object;
            currentSegmentViewIndex = 0;
            nextSegment();
            updateLabels();
        }
    }
    
    @Override
    public void updateLabels() {
        if(currentEvent != null) {
            titleText.setText(currentEvent.toString());
        }
    }
    
    private void nextSegment() {
        
        
        
    }

}
