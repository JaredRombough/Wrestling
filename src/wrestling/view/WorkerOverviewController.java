package wrestling.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import wrestling.MainApp;
import wrestling.model.GameController;
import wrestling.model.Worker;

public class WorkerOverviewController implements Initializable {

    private MainApp mainApp;
    private GameController gameController;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;

        initializeMore();
    }

    @FXML
    private Label nameLabel;

    @FXML
    private Label strikingLabel;

    @FXML
    private Label wrestlingLabel;

    @FXML
    private Label flyingLabel;

    @FXML
    private Label contractLabel;

    @FXML
    private Label popularityLabel;

    @FXML
    private Label reputationLabel;

    @FXML
    private Label proficiencyLabel;

    private Worker currentWorker;

    public void setCurrentWorker(Worker newWorker) {
        this.currentWorker = newWorker;

        updateLabels();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    private void initializeMore() {

    }

    private void updateLabels() {
        nameLabel.setText(currentWorker.getName());
        wrestlingLabel.setText(Integer.toString(currentWorker.getWrestling()));
        flyingLabel.setText(Integer.toString(currentWorker.getFlying()));
        strikingLabel.setText(Integer.toString(currentWorker.getStriking()));
        proficiencyLabel.setText(Integer.toString(currentWorker.getProficiency()));
        reputationLabel.setText(Integer.toString(currentWorker.getReputation()));
        popularityLabel.setText(Integer.toString(currentWorker.getPopularity()));

        contractLabel.setText(currentWorker.contractString());
    }

}
