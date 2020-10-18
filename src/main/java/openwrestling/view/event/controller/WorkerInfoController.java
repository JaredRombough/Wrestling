package openwrestling.view.event.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import openwrestling.model.gameObjects.Segment;
import openwrestling.model.gameObjects.Worker;
import openwrestling.view.results.controller.ResultsCardController;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class WorkerInfoController extends ControllerBase implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private AnchorPane imageAnchor;

    @FXML
    private Label strikingLabel;

    @FXML
    private Label wrestlingLabel;

    @FXML
    private Label flyingLabel;

    @FXML
    private Label popularityLabel;

    @FXML
    private Label moraleLabel;

    @FXML
    private Label lastMatchLabel;

    @FXML
    private Label charismaLabel;

    @FXML
    private Label workerRecordLabel;


    public void setWorker(Worker worker) {
        anchorPane.setVisible(true);

        wrestlingLabel.setText(Integer.toString(worker.getWrestling()));
        flyingLabel.setText(Integer.toString(worker.getFlying()));
        strikingLabel.setText(Integer.toString(worker.getStriking()));
        charismaLabel.setText(Integer.toString(worker.getCharisma()));
        popularityLabel.setText(Integer.toString(worker.getPopularity()));
        moraleLabel.setText(Integer.toString(gameController.getRelationshipManager().getOrCreateMoraleRelationship(worker, playerPromotion()).getLevel()));

        ViewUtils.updateWorkerMoraleLabel(moraleLabel);

        ViewUtils.updateWorkerStatLabels(List.of(wrestlingLabel,
                flyingLabel,
                strikingLabel,
                charismaLabel,
                popularityLabel));


        imageAnchor.getChildren().clear();
        GameScreen card = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController, imageAnchor);
        ((ResultsCardController) card.controller).setWorkerInfoMode();
        card.controller.setCurrent(worker);

        Segment lastSegment = gameController.getSegmentManager().getLastSegment(worker, playerPromotion());
        String lastMatchLabelText = "Last appearance:\n";
        if (lastSegment != null) {
            lastMatchLabelText += gameController.getSegmentStringService().getSegmentStringForWorkerInfo(lastSegment, lastSegment.getEvent());
        } else {
            lastMatchLabelText += "None";
        }

        lastMatchLabel.setText(lastMatchLabelText);
        workerRecordLabel.setText(gameController.getSegmentStringService().getWorkerRecord(worker, playerPromotion()));
    }

    public void clearText() {
        anchorPane.setVisible(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
