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
import java.util.List;
import java.util.ResourceBundle;

public class WorkerInfoController extends ControllerBase implements Initializable {


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
    private Label charismaLabel;

    public void setWorker(Worker worker) {
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
    }

    public void clearText() {
        //aka hide?
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
