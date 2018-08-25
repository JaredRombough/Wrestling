package wrestling.view.browser.controller;

import java.net.URL;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import wrestling.model.Promotion;
import wrestling.model.modelView.WorkerView;
import wrestling.model.utility.ModelUtils;
import wrestling.view.results.controller.ResultsCardController;
import wrestling.view.utility.GameScreen;
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
    private Label workrate;

    @FXML
    private Label ageLabel;

    @FXML
    private Label managerLabel;

    @FXML
    private Label mainRosterLabel;

    @FXML
    private Label genderLabel;

    @FXML
    private Label injury;

    @FXML
    private Label injuryLabel;

    @FXML
    private AnchorPane imageAnchor;

    @FXML
    private AnchorPane feedAnchor;
    private GameScreen feedPaneScreen;

    @FXML
    private AnchorPane contractPaneAnchor;
    private GameScreen contractPaneScreen;

    private WorkerView worker;
    private Promotion promotion;

    @Override
    public void setCurrent(Object obj) {

        WorkerView newWorker = (WorkerView) obj;

        worker = newWorker;
        contractPaneScreen.controller.setCurrent(newWorker);

        updateLabels();
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
        updateLabels();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this.getClass());
        mainRosterLabel.setText("");
    }

    @Override
    public void initializeMore() {
        contractPaneScreen = ViewUtils.loadScreenFromResource(ScreenCode.CONTRACT_PANE, mainApp, gameController, contractPaneAnchor);
        feedPaneScreen = ViewUtils.loadScreenFromResource(ScreenCode.SIMPLE_DISPLAY, mainApp, gameController, feedAnchor);
        injury.getStyleClass().add("lowStat");
    }

    @Override
    public void updateLabels() {

        if (gameController.getContractManager().getFullRoster(promotion).contains(worker)
                || gameController.getWorkerManager().freeAgents(promotion).contains(worker)) {
            nameLabel.setText(worker.getName());
            wrestlingLabel.setText(Integer.toString(worker.getWrestling()));
            flyingLabel.setText(Integer.toString(worker.getFlying()));
            strikingLabel.setText(Integer.toString(worker.getStriking()));
            behaviourLabel.setText(Integer.toString(worker.getBehaviour()));
            charismaLabel.setText(Integer.toString(worker.getCharisma()));
            popularityLabel.setText(Integer.toString(worker.getPopularity()));
            workrate.setText(ViewUtils.intToStars(ModelUtils.getMatchWorkRating(worker)));
            ageLabel.setText(Integer.toString(worker.getAge()));
            genderLabel.setText(worker.getGender().toString());

            if (worker.getInjury() != null) {
                injury.setText(String.format("%s days left",
                        DAYS.between(gameController.getDateManager().today(), worker.getInjury().getExpiryDate())));
            }
            injury.setVisible(worker.getInjury() != null);
            injuryLabel.setVisible(worker.getInjury() != null);

            imageAnchor.getChildren().clear();
            GameScreen card = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController, imageAnchor);
            card.controller.setCurrent(worker);
            ((ResultsCardController) card.controller).setNameLabelVisibile(false);

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

            Text text = new Text(gameController.getContractManager().contractString(worker));

            contractInfo.setContent(text);

            if (worker.isManager()) {
                managerLabel.setText("Manager");
            } else {
                managerLabel.setText("");
            }
            if (!worker.isMainRoster()) {
                managerLabel.setText("Development");
            }

            //only show the contract pane if the worker can negotiate with the player
            contractPaneScreen.pane.setVisible(gameController.getContractManager().canNegotiate(worker, promotion));

        } else if (!gameController.getContractManager().getFullRoster(promotion).contains(worker)) {
            //probably our roster is empty for some reason, should be a rare situation
            //try to eliminate this possibility if we haven't already
            worker = null;

            nameLabel.setText("");
            wrestlingLabel.setText("");
            flyingLabel.setText("");
            strikingLabel.setText("");
            behaviourLabel.setText("");
            popularityLabel.setText("");

            contractPaneScreen.controller.updateLabels();
        }

        feedPaneScreen.controller.setCurrent(worker);

    }

}
