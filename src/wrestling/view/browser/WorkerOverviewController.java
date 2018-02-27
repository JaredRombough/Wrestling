package wrestling.view.browser;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import wrestling.model.Promotion;
import wrestling.model.Worker;
import wrestling.view.utility.Screen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class WorkerOverviewController extends ControllerBase implements Initializable {

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
    private ScrollPane contractInfo;

    @FXML
    private Label popularityLabel;

    @FXML
    private Label behaviourLabel;

    @FXML
    private Label charismaLabel;

    @FXML
    private Label managerLabel;

    @FXML
    private Label mainRosterLabel;

    @FXML
    private ImageView imageView;

    @FXML
    private StackPane workerImageBorder;

    @FXML
    private AnchorPane feedAnchor;
    private Screen feedPaneScreen;

    @FXML
    private AnchorPane contractPaneAnchor;
    private Screen contractPaneScreen;

    private Worker currentWorker;
    private Promotion currentPromotion;

    @Override
    public void setCurrent(Object obj) {

        Worker newWorker = (Worker) obj;

        currentWorker = newWorker;
        contractPaneScreen.controller.setCurrent(newWorker);

        updateLabels();
    }

    public void setCurrentPromotion(Promotion promotion) {
        this.currentPromotion = promotion;
        updateLabels();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this.getClass());
    }

    @Override
    public void initializeMore() {
        contractPaneScreen = ViewUtils.loadScreenFromResource(ScreenCode.CONTRACT_PANE, mainApp, gameController, contractPaneAnchor);
        feedPaneScreen = ViewUtils.loadScreenFromResource(ScreenCode.SIMPLE_DISPLAY, mainApp, gameController, feedAnchor);
    }

    @Override
    public void updateLabels() {

        if (gameController.getContractManager().getFullRoster(currentPromotion).contains(currentWorker)
                || gameController.getWorkerManager().freeAgents(currentPromotion).contains(currentWorker)) {
            nameLabel.setText(currentWorker.getName());
            wrestlingLabel.setText(Integer.toString(currentWorker.getWrestling()));
            flyingLabel.setText(Integer.toString(currentWorker.getFlying()));
            strikingLabel.setText(Integer.toString(currentWorker.getStriking()));
            behaviourLabel.setText(Integer.toString(currentWorker.getBehaviour()));
            charismaLabel.setText(Integer.toString(currentWorker.getCharisma()));
            popularityLabel.setText(Integer.toString(currentWorker.getPopularity()));

            ViewUtils.showImage(String.format(mainApp.getPicsFolder().toString() + "\\" + currentWorker.getImageString()),
                    workerImageBorder,
                    imageView);

            List<Label> statLabels = Arrays.asList(
                    wrestlingLabel,
                    flyingLabel,
                    strikingLabel,
                    behaviourLabel,
                    charismaLabel,
                    popularityLabel);

            List<String> styleList = Arrays.asList("lowStat", "midStat", "highStat");

            for (Label l : statLabels) {
                //strip previous styles
                for (String s : styleList) {
                    if (l.getStyleClass().contains(s)) {
                        l.getStyleClass().remove(s);
                    }
                }

                String style;
                if (Integer.parseInt(l.getText()) < 50) {
                    style = "lowStat";
                } else if (Integer.parseInt(l.getText()) >= 50
                        && Integer.parseInt(l.getText()) < 75) {
                    style = "midStat";
                } else {
                    style = "highStat";
                }

                l.getStyleClass().add(style);
            }

            Text text = new Text(gameController.getContractManager().contractString(currentWorker));

            contractInfo.setContent(text);

            if (currentWorker.isManager()) {
                managerLabel.setText("Manager");
            } else {
                managerLabel.setText("");
            }
            if (currentWorker.isMainRoster()) {
                if (currentWorker.isFullTime()) {
                    mainRosterLabel.setText("Full Time");
                } else {
                    mainRosterLabel.setText("Part Time");
                }

            } else {
                managerLabel.setText("Development");
            }

            //only show the contract pane if the worker can negotiate with the player
            contractPaneScreen.pane.setVisible(gameController.getContractManager().canNegotiate(currentWorker, currentPromotion));

        } else if (!gameController.getContractManager().getFullRoster(currentPromotion).contains(currentWorker)) {
            //probably our roster is empty for some reason, should be a rare situation
            //try to eliminate this possibility if we haven't already
            currentWorker = null;

            nameLabel.setText("");
            wrestlingLabel.setText("");
            flyingLabel.setText("");
            strikingLabel.setText("");
            behaviourLabel.setText("");
            popularityLabel.setText("");

            contractPaneScreen.controller.updateLabels();
        }

        feedPaneScreen.controller.setCurrent(currentWorker);

    }

}
