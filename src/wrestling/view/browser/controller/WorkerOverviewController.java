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
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import static wrestling.model.constants.UIConstants.EDIT_ICON;
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
    private Label entourageLabel;

    @FXML
    private Button managerButton;
    private ChangeListener<Boolean> managerButtonHoverListener;

    @FXML
    private AnchorPane imageAnchor;

    @FXML
    private ListView<WorkerView> managedListView;

    @FXML
    private Button entourageButton;
    private ChangeListener<Boolean> entourageButtonHoverListener;

    @FXML
    private ListView<WorkerView> entourageListView;

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

        entourageButton.setVisible(false);
        managerButton.setVisible(false);

        if (playerPromotion().equals(promotion)) {
            entourageButton.hoverProperty().addListener(entourageButtonHoverListener);
            entourageLabel.hoverProperty().addListener(entourageButtonHoverListener);
            managerButton.hoverProperty().addListener(managerButtonHoverListener);
            manager.hoverProperty().addListener(managerButtonHoverListener);
        } else {
            entourageButton.hoverProperty().removeListener(entourageButtonHoverListener);
            entourageLabel.hoverProperty().removeListener(entourageButtonHoverListener);
            managerButton.hoverProperty().removeListener(managerButtonHoverListener);
            manager.hoverProperty().removeListener(managerButtonHoverListener);
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

        managerButtonHoverListener = ViewUtils.buttonHoverListener(managerButton);
        entourageButtonHoverListener = ViewUtils.buttonHoverListener(entourageButton);

        entourageButton.setOnAction(a -> {
            List<WorkerView> workers = new ArrayList<>(promotion.getFullRoster());
            workers.removeAll(worker.getEntourage());
            workers.remove(worker);
            Optional<WorkerView> result = ViewUtils.selectWorkerDialog(
                    workers,
                    resx.getString("JoinEntourageTitle"),
                    String.format(resx.getString("JoinEntourageText"), worker.getName())
            ).showAndWait();

            result.ifPresent(newMember -> {
                worker.getEntourage().add(newMember);
                updateLabels();
            });
        });

        entourageListView.setCellFactory(c -> new ListCell<WorkerView>() {
            @Override
            public void updateItem(WorkerView client, boolean empty) {
                super.updateItem(client, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    GameScreen clientScreen = ViewUtils.loadScreenFromFXML(ScreenCode.GROUP_MEMBER, mainApp, gameController);
                    GroupMemberController cotroller = (GroupMemberController) clientScreen.controller;
                    cotroller.setCurrent(client);
                    setPrefHeight(40);
                    setMaxHeight(40);
                    cotroller.setEditable(Objects.equals(promotion, playerPromotion()));
                    if (Objects.equals(promotion, playerPromotion())) {

                        cotroller.getxButton().setOnAction(a -> {
                            String header = "Removing worker from entourage";
                            String content = String.format("Really remove %s from %s's entourage?", client.getName(), worker.getName());
                            if (ViewUtils.generateConfirmationDialogue(header, content)) {
                                worker.getEntourage().remove(client);
                                updateLabels();
                            }
                        });
                    }

                    setGraphic(clientScreen.pane);
                }
            }
        });

        managedListView.setCellFactory(c -> new ListCell<WorkerView>() {
            @Override
            public void updateItem(WorkerView client, boolean empty) {
                super.updateItem(client, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    GameScreen clientScreen = ViewUtils.loadScreenFromFXML(ScreenCode.GROUP_MEMBER, mainApp, gameController);
                    GroupMemberController cotroller = (GroupMemberController) clientScreen.controller;
                    cotroller.setCurrent(client);
                    setPrefHeight(40);
                    setMaxHeight(40);

                    if (Objects.equals(promotion, playerPromotion())) {
                        cotroller.getxButton().setOnAction(a -> {
                            String header = "Removing client from manager";
                            String content = String.format("Really remove %s as a client for %s?", client.getName(), worker.getName());
                            if (ViewUtils.generateConfirmationDialogue(header, content)) {
                                client.setManager(null);
                                updateLabels();
                            }
                        });

                    }

                    setGraphic(clientScreen.pane);
                }
            }
        });

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
            entourageListView.getItems().clear();
            entourageListView.getItems().addAll(worker.getEntourage());

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
            managerButton.setOnAction(a -> {
                List<WorkerView> workers = new ArrayList<>(playerPromotion().getFullRoster());
                workers.remove(worker);
                Optional<WorkerView> result = ViewUtils.selectWorkerDialog(
                        workers,
                        "Select Manager",
                        String.format("Select a manager for %s", worker.getName()),
                        worker.getManager(),
                        resx.getString("None")
                ).showAndWait();

                result.ifPresent(newManager -> {
                    if (!StringUtils.equals(worker.getName(), resx.getString("None"))) {
                        worker.setManager(newManager);
                    } else {
                        worker.setManager(null);
                    }
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
