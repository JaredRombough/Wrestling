package wrestling.view.results;

import java.io.IOException;
import java.net.URL;
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
import wrestling.model.modelView.SegmentView;
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

    private EventView eventView;

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
            if (currentSegmentViewIndex < eventView.getSegments().size()) {
                nextSegment();
            } else if (eventView.getEvent().getAttendance() == 0) {
                showSummary();
            } else {
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
            eventView = (EventView) object;
            currentSegmentViewIndex = 0;
            nextSegment();
            updateLabels();
        }
    }

    @Override
    public void updateLabels() {
        if (eventView != null) {
            titleText.setText(eventView.getEvent().toString());
            if (currentResultsDisplay != null) {
                currentResultsDisplay.controller.updateLabels();
            }
        }
    }

    private void showSummary() {
        gameController.getEventFactory().processEventView(eventView, gameController.getDateManager().today(), false);
        showNextDisplay(eventView);
    }

    private void nextSegment() {
        SegmentView current = eventView.getSegments().get(currentSegmentViewIndex);
        current.setSegment(gameController.getEventFactory().processSegmentView(eventView.getEvent(), current));
        currentSegmentViewIndex++;
        showNextDisplay(current);
    }

    private void showNextDisplay(Object obj) {
        resultsDisplayPane.getChildren().clear();
        currentResultsDisplay = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_DISPLAY, mainApp, gameController, resultsDisplayPane);
        currentResultsDisplay.controller.setCurrent(obj);
    }

}
