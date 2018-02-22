package wrestling.view.results;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import wrestling.model.modelView.EventView;
import wrestling.view.utility.Screen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
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

    private Screen currentResultsDisplay;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger = LogManager.getLogger(getClass());
        nextButton.setText("Next");

    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource() == nextButton) {
            if (currentSegmentViewIndex < currentEvent.getSegments().size()) {
                nextSegment();
            } else {
                gameController.getEventFactory().processEvent(currentEvent, gameController.getDateManager().today());
                try {
                    mainApp.nextDay();
                } catch (IOException ex) {
                    logger.log(Level.FATAL, ex);
                }
            }
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
        if (currentEvent != null) {
            titleText.setText(currentEvent.getEvent().toString());
            if (currentResultsDisplay != null) {
                currentResultsDisplay.controller.updateLabels();
            }
        }
    }

    private void nextSegment() {

        currentResultsDisplay = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_DISPLAY, mainApp, gameController);

        currentResultsDisplay.controller.setCurrent(currentEvent.getSegments().get(currentSegmentViewIndex));

        resultsDisplayPane.getChildren().clear();
        ViewUtils.anchorPaneToParent(resultsDisplayPane, currentResultsDisplay.pane);

        currentSegmentViewIndex++;
    }

}
