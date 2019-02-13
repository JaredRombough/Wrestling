package wrestling.view.browser.controller;

import java.net.URL;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import static wrestling.model.constants.GameConstants.EDIT_ICON;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.utility.ModelUtils;
import wrestling.view.results.controller.ResultsCardController;
import wrestling.view.utility.GameScreen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.comparators.NameComparator;
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
    private Label development;

    @FXML
    private Label genderLabel;

    @FXML
    private Label injury;

    @FXML
    private Label injuryLabel;

    @FXML
    private Label manager;

    @FXML
    private Label managerLabel;

    @FXML
    private Label managedLabel;

    @FXML
    private Button managerButton;

    @FXML
    private AnchorPane imageAnchor;

    @FXML
    private ListView managedListView;

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
        managerButton.setText(EDIT_ICON);
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

            updateManagerLabels();

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
                styleList.stream().filter((s) -> (l.getStyleClass().contains(s))).forEach((s) -> {
                    l.getStyleClass().remove(s);
                });

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

            if (!worker.isMainRoster()) {
                development.setText("Development");
            } else {
                development.setText("");
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

    private void updateManagerLabels() {
        manager.setText(worker.getManager() == null ? "None" : worker.getManager().getName());

        if (playerPromotion().equals(promotion) && !gameController.getContractManager().canNegotiate(worker, promotion)) {
            managerButton.setVisible(true);
            managerButton.setOnAction(a -> {
                List<WorkerView> workers = new ArrayList<>(playerPromotion().getFullRoster());
                workers.remove(worker);
                Optional<WorkerView> result = ViewUtils.selectWorkerDialog(
                        workers,
                        "Select Manager",
                        String.format("Select a manager for %s", worker.getName()),
                        worker.getManager()
                ).showAndWait();

                result.ifPresent(newManager -> {
                    worker.setManager(newManager);
                    updateLabels();
                });
            });
        } else {
            managerButton.setVisible(false);
            managedLabel.setVisible(false);
        }
        List<WorkerView> managed = new ArrayList<>(promotion.getFullRoster().stream().filter(w -> worker.equals(w.getManager())).collect(Collectors.toList()));
        if (!managed.isEmpty()) {
            managedLabel.setVisible(true);
            managedListView.setVisible(true);
            Collections.sort(managed, new NameComparator());
            managedListView.setItems(FXCollections.observableArrayList(managed));
        } else {
            managedLabel.setVisible(false);
            managedListView.setVisible(false);
        }
    }

}
