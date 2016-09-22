package wrestling.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
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

    private AnchorPane contractPane;
    private ContractPaneController contractPaneController;

    @FXML
    private GridPane gridPane;

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
        contractPaneController.setWorker(newWorker);

        updateLabels();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    private void initializeMore() {
        loadContractPane();
    }

    private void loadContractPane() {
        //load the contract pane
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/ContractPane.fxml"));
            contractPane = (AnchorPane) loader.load();

            contractPaneController = (ContractPaneController) loader.getController();

            contractPaneController.setMainApp(this.mainApp);

            contractPaneController.setGameController(this.gameController);

        } catch (IOException e) {
            e.printStackTrace();
        }

        gridPane.add(contractPane, 0, 6, 4, 1);
    }

    public void updateLabels() {

        if (gameController.playerPromotion().getRoster().contains(currentWorker)) {
            nameLabel.setText(currentWorker.getName());
            wrestlingLabel.setText(Integer.toString(currentWorker.getWrestling()));
            flyingLabel.setText(Integer.toString(currentWorker.getFlying()));
            strikingLabel.setText(Integer.toString(currentWorker.getStriking()));
            proficiencyLabel.setText(Integer.toString(currentWorker.getProficiency()));
            reputationLabel.setText(Integer.toString(currentWorker.getReputation()));
            popularityLabel.setText(Integer.toString(currentWorker.getPopularity()));
            contractLabel.setText(currentWorker.contractString());
            
            contractPaneController.updateLabels();
        } else if (!gameController.playerPromotion().getRoster().contains(currentWorker)) {
            //probably our roster is empty for some reason, should be a rare situation
            currentWorker = null;
            
            nameLabel.setText("");
            wrestlingLabel.setText("");
            flyingLabel.setText("");
            strikingLabel.setText("");
            proficiencyLabel.setText("");
            reputationLabel.setText("");
            popularityLabel.setText("");
            contractLabel.setText("");
            
            contractPaneController.updateLabels();
        }

        

    }

}
