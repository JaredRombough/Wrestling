package openwrestling.view.results.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.modelView.Segment;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;
import org.apache.logging.log4j.LogManager;

import java.net.URL;
import java.util.ResourceBundle;

public class ResultsScreenController extends ControllerBase implements Initializable {

    @FXML
    private Text titleText;

    @FXML
    private AnchorPane resultsDisplayPane;

    @FXML
    private Button nextButton;

    private Event event;

    private int currentSegmentIndex;

    private GameScreen currentResultsDisplay;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger = LogManager.getLogger(getClass());
        nextButton.setText("Next");

    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource() == nextButton) {
            if (currentSegmentIndex < this.event.getSegments().size()) {
                nextSegment();
            } else if (this.event.getAttendance() == 0) {
                showSummary();
            } else {
                mainApp.nextDay();
            }
        }
    }

    @Override
    public void setCurrent(Object object) {
        if (object instanceof Event) {
            event = (Event) object;
            currentSegmentIndex = 0;
            nextSegment();
            updateLabels();
        }
    }

    @Override
    public void updateLabels() {
        if (event != null) {
            titleText.setText(event.toString());
            if (currentResultsDisplay != null) {
                currentResultsDisplay.controller.updateLabels();
            }
        }
    }

    private void showSummary() {
        gameController.getEventFactory().processEventView(event, false);

        showNextDisplay(event);
    }

    private void nextSegment() {
        Segment current = gameController.getEventFactory().processSegment(event, event.getSegments().get(currentSegmentIndex));
        currentSegmentIndex++;
        showNextDisplay(current);
        if (current.getNewStable() != null) {
            String name = ViewUtils.editTextDialog("", "Enter name for new stable");
            current.getNewStable().setName(name);
        }
    }

    private void showNextDisplay(Object obj) {
        resultsDisplayPane.getChildren().clear();
        currentResultsDisplay = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_DISPLAY, mainApp, gameController, resultsDisplayPane);
        currentResultsDisplay.controller.setCurrent(obj);
    }

}
