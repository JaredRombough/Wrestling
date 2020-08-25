package openwrestling.view.event.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import openwrestling.model.gameObjects.Worker;
import openwrestling.view.results.controller.ResultsCardController;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;

import java.net.URL;
import java.util.ResourceBundle;

public class WorkerInfoController extends ControllerBase implements Initializable {

    @FXML
    private Label workerNameLabel;

    @FXML
    private AnchorPane imageAnchor;

    public void setWorker(Worker worker) {
        workerNameLabel.setText(worker.getName());

        imageAnchor.getChildren().clear();
        GameScreen card = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController, imageAnchor);
        ((ResultsCardController) card.controller).setWorkerInfoMode();
        card.controller.setCurrent(worker);
    }

    public void clearText() {
        workerNameLabel.setText("");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
