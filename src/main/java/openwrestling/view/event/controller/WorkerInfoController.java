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

    private Worker worker;


    public void setWorker(Worker worker) {
        this.worker = worker;
        anchorPane.setVisible(true);

        wrestlingLabel.setText(Integer.toString(worker.getWrestling()));
        flyingLabel.setText(Integer.toString(worker.getFlying()));
        strikingLabel.setText(Integer.toString(worker.getStriking()));
        charismaLabel.setText(Integer.toString(worker.getCharisma()));
        popularityLabel.setText(Integer.toString(worker.getPopularity()));
        moraleLabel.setText(Integer.toString(gameController.getRelationshipManager().getOrCreateMoraleRelationship(worker, playerPromotion()).getLevel()));

        ViewUtils.updateWorkerMoraleLabel(moraleLabel);

        ViewUtils.updateWorkerStatLabelStyle(List.of(wrestlingLabel,
                flyingLabel,
                strikingLabel,
                charismaLabel,
                popularityLabel));


        imageAnchor.getChildren().clear();
        GameScreen card = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController, imageAnchor);
        ((ResultsCardController) card.controller).setWorkerInfoMode();
        card.controller.setCurrent(worker);

        lastMatchLabel.setText(getAppearanceString());
        workerRecordLabel.setText(getRecordString());
    }

    public void clearText() {
        anchorPane.setVisible(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    private String getRecordString() {
        String overallRecord = gameController.getSegmentStringService().getOverallWorkerRecord(worker, playerPromotion());
        String singlesRecord = gameController.getSegmentStringService().getWorkerRecord(worker, playerPromotion(), 1);
        String tagTeamRecord = gameController.getSegmentStringService().getWorkerRecord(worker, playerPromotion(), 2);
        String streak = gameController.getSegmentStringService().getWorkerStreak(worker, playerPromotion());
        return String.format("Singles:\t\t%s\nTag Team:\t%s\nOverall:\t\t%s\nStreak:\t\t%s",
                singlesRecord,
                tagTeamRecord,
                overallRecord,
                streak
        );
    }

    private String getAppearanceString() {
        Segment lastSegment = gameController.getSegmentManager().getLastSegment(worker, playerPromotion());
        String lastMatchString = "Last appearance:\n";
        if (lastSegment != null) {
            lastMatchString += gameController.getSegmentStringService().getSegmentStringForWorkerInfo(lastSegment,
                    lastSegment.getEvent(),
                    gameController.getDateManager().today()
            );
        } else {
            lastMatchString += "None";
        }

        String percentOfShowsString = gameController.getSegmentStringService().getPercentOfShowsString(worker,
                playerPromotion(),
                gameController.getDateManager().today()
        );

        String missedShowStreakString = gameController.getSegmentStringService().getMissedShowStreakString(worker,
                playerPromotion(),
                gameController.getDateManager().today()
        );

        return String.format("%s\n%s\n%s",
                lastMatchString,
                percentOfShowsString,
                missedShowStreakString
        );
    }

}
