package openwrestling.view.browser.controller;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import openwrestling.model.gameObjects.Injury;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.utility.ModelUtils;
import openwrestling.view.results.controller.ResultsCardController;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.comparators.NameComparator;
import openwrestling.view.utility.interfaces.ControllerBase;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static openwrestling.model.constants.UIConstants.EDIT_ICON;

public class WorkerOverviewController extends ControllerBase implements Initializable {

    @FXML
    private Button relationshipButton;

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
    private Label genderLabel;

    @FXML
    private Label injury;

    @FXML
    private Label injuryLabel;

    @FXML
    private Label manager;

    @FXML
    private Label moraleLabel;

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
    private ListView<Worker> managedListView;

    @FXML
    private Button entourageButton;
    private ChangeListener<Boolean> entourageButtonHoverListener;

    @FXML
    private ListView<Worker> entourageListView;

    @FXML
    private AnchorPane contractAnchor;
    private GameScreen contractScreen;

    @FXML
    private AnchorPane feedAnchor;
    private GameScreen feedPaneScreen;

    private Worker worker;
    private Promotion promotion;

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof Worker) {
            worker = (Worker) obj;

            feedPaneScreen.controller.setCurrent(worker);
            contractScreen.controller.setCurrent(worker);

            updateLabels();
        }
    }

    public void setPromotion(Promotion promotion) {
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
            List<Worker> workers = new ArrayList<>(gameController.getWorkerManager().getRoster(promotion));
            workers.removeAll(gameController.getEntourageManager().getEntourage(worker));
            workers.remove(worker);
            Optional<Worker> result = ViewUtils.selectWorkerDialog(
                    workers,
                    resx.getString("JoinEntourageTitle"),
                    String.format(resx.getString("JoinEntourageText"), worker.getName())
            ).showAndWait();

            result.ifPresent(follower -> {
                gameController.getEntourageManager().addWorkerToEntourage(worker, follower);
                updateLabels();
            });
        });

        entourageListView.setCellFactory(c -> new ListCell<>() {
            @Override
            public void updateItem(Worker follower, boolean empty) {
                super.updateItem(follower, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    GameScreen clientScreen = ViewUtils.loadScreenFromFXML(ScreenCode.GROUP_MEMBER, mainApp, gameController);
                    GroupMemberController controller = (GroupMemberController) clientScreen.controller;
                    controller.setCurrent(follower);
                    setPrefHeight(40);
                    setMaxHeight(40);
                    controller.setEditable(Objects.equals(promotion, playerPromotion()));
                    if (Objects.equals(promotion, playerPromotion())) {

                        controller.getxButton().setOnAction(a -> {
                            String header = resx.getString("removing.worker.from.entourage");
                            String content = String.format("Really remove %s from %s's entourage?", follower.getName(), worker.getName());
                            if (ViewUtils.generateConfirmationDialogue(header, content)) {
                                gameController.getEntourageManager().removeWorkerFromEntourage(worker, follower);
                                updateLabels();
                            }
                        });
                    }

                    setGraphic(clientScreen.pane);
                }
            }
        });

        managedListView.setCellFactory(c -> new ListCell<>() {
            @Override
            public void updateItem(Worker client, boolean empty) {
                super.updateItem(client, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    GameScreen clientScreen = ViewUtils.loadScreenFromFXML(ScreenCode.GROUP_MEMBER, mainApp, gameController);
                    GroupMemberController controller = (GroupMemberController) clientScreen.controller;
                    controller.setCurrent(client);
                    setPrefHeight(40);
                    setMaxHeight(40);

                    if (Objects.equals(promotion, playerPromotion())) {
                        controller.getxButton().setOnAction(a -> {
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

        relationshipButton.setOnAction(e -> {
            ViewUtils.generateRelationshipDialog(worker, gameController.getRelationshipManager().getRelationships(worker)).showAndWait();
        });

    }

    @Override
    public void updateLabels() {
        List<Worker> roster = gameController.getWorkerManager().getRoster(promotion);
        if (worker != null && roster.stream().anyMatch(w -> w.getWorkerID() == worker.getWorkerID())
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
            moraleLabel.setText(Integer.toString(gameController.getRelationshipManager().getOrCreateMoraleRelationship(worker, promotion).getLevel()));

            updateManagerLabels();
            entourageListView.getItems().clear();
            entourageListView.getItems().addAll(gameController.getEntourageManager().getEntourage(worker));

            Injury workerInjury = gameController.getInjuryManager().getInjury(worker);
            if (workerInjury != null) {
                injury.setText(String.format("%s days left",
                        DAYS.between(gameController.getDateManager().today(), workerInjury.getExpiryDate()) + 1));
            }
            injury.setVisible(workerInjury != null);
            injuryLabel.setVisible(workerInjury != null);

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
                    popularityLabel,
                    moraleLabel);

            List<String> styleList = Arrays.asList("lowStat", "midStat", "highStat");

            for (Label l : statLabels) {
                styleList.stream().filter((s) -> (l.getStyleClass().contains(s))).forEach((s) -> {
                    l.getStyleClass().remove(s);
                });

                String style;

                int lowBound = 50;
                int midBound = 75;

                if (Objects.equals(moraleLabel, l)) {
                    lowBound = -50;
                    midBound = -1;
                }

                if (Integer.parseInt(l.getText()) < lowBound) {
                    style = "lowStat";
                } else if (Integer.parseInt(l.getText()) >= lowBound
                        && Integer.parseInt(l.getText()) < midBound) {
                    style = "midStat";
                } else {
                    style = "highStat";
                }

                l.getStyleClass().add(style);
            }

        }

        feedPaneScreen.controller.updateLabels();
        contractScreen.controller.updateLabels();

    }

    private void updateManagerLabels() {
        manager.setText(worker.getManager() == null ? "None" : worker.getManager().getName());

        if (playerPromotion().equals(promotion) && !gameController.getContractManager().canNegotiate(worker, promotion)) {
            managerButton.setOnAction(a -> {
                List<Worker> workers = new ArrayList<>(gameController.getWorkerManager().getRoster(playerPromotion()));
                workers.remove(worker);
                Optional<Worker> result = ViewUtils.selectWorkerDialog(
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
        List<Worker> managed = new ArrayList<>(gameController.getWorkerManager().getRoster(promotion).stream().filter(w -> worker.equals(w.getManager())).collect(Collectors.toList()));
        if (!managed.isEmpty()) {
            managedLabel.setVisible(true);
            managedListView.setVisible(true);
            managed.sort(new NameComparator());
            managedListView.setItems(FXCollections.observableArrayList(managed));
        } else {
            managedLabel.setVisible(false);
            managedListView.setVisible(false);
        }
    }
}
