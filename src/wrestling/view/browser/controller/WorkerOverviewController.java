package wrestling.view.browser.controller;

import java.net.URL;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import wrestling.model.Contract;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.utility.ContractUtils;
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
    private Label genderLabel;

    @FXML
    private Label injury;

    @FXML
    private Label injuryLabel;

    @FXML
    private AnchorPane imageAnchor;

    @FXML
    private AnchorPane contractAnchor;
    private GameScreen contractScreen;

    @FXML
    private AnchorPane feedAnchor;
    private GameScreen feedPaneScreen;

    private WorkerView worker;
    private PromotionView promotion;

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof WorkerView) {
            WorkerView newWorker = (WorkerView) obj;

            worker = newWorker;

            feedPaneScreen.controller.setCurrent(worker);
            contractScreen.controller.setCurrent(worker);

            updateLabels();
        }
    }

    public void setPromotion(PromotionView promotion) {
        if (!Objects.equals(this.promotion, promotion)) {
            this.promotion = promotion;
            updateLabels();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logger = LogManager.getLogger(this.getClass());
    }

    @Override
    public void initializeMore() {

        feedPaneScreen = ViewUtils.loadScreenFromResource(ScreenCode.SIMPLE_DISPLAY, mainApp, gameController, feedAnchor);
        contractScreen = ViewUtils.loadScreenFromResource(ScreenCode.CONTRACT, mainApp, gameController, contractAnchor);
        injury.getStyleClass().add("lowStat");
    }

    @Override
    public void updateLabels() {

        if (promotion.getFullRoster().contains(worker)
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
                        DAYS.between(gameController.getDateManager().today(), worker.getInjury().getExpiryDate()) + 1));
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

            if (worker.isManager()) {
                managerLabel.setText("Manager");
            } else {
                managerLabel.setText("");
            }
            if (!worker.isMainRoster()) {
                managerLabel.setText("Development");
            }

        } else if (!promotion.getFullRoster().contains(worker)) {
            //probably our roster is empty for some reason, should be a rare situation
            //try to eliminate this possibility if we haven't already
            worker = null;

            nameLabel.setText("");
            wrestlingLabel.setText("");
            flyingLabel.setText("");
            strikingLabel.setText("");
            behaviourLabel.setText("");
            popularityLabel.setText("");
        }

        feedPaneScreen.controller.updateLabels();
        contractScreen.controller.updateLabels();

    }

}
