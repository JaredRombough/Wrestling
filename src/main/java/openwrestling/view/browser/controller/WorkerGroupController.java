package openwrestling.view.browser.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import openwrestling.model.gameObjects.RosterSplit;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.gameObjects.WorkerGroup;
import openwrestling.model.segment.constants.browse.mode.BrowseMode;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class WorkerGroupController extends ControllerBase {

    @FXML
    private AnchorPane nameAnchor;

    @FXML
    private Label ownerLabel;

    @FXML
    private GridPane gridPane;

    @FXML
    private ListView<Worker> listView;

    @FXML
    private Button addButton;

    private EditLabel editLabel;

    private WorkerGroup workerGroup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listView.setCellFactory(c -> new ListCell<>() {
            @Override
            public void updateItem(Worker worker, boolean empty) {
                super.updateItem(worker, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    GameScreen groupMemberScreen = ViewUtils.loadScreenFromFXML(ScreenCode.GROUP_MEMBER, mainApp, gameController);
                    GroupMemberController controller = (GroupMemberController) groupMemberScreen.controller;
                    controller.setCurrent(worker);
                    controller.getxButton().setOnAction(a -> {
                        String header = String.format("Removing worker from %s", workerGroup.getName());
                        String content = String.format("Really remove %s from %s?", worker.getName(), workerGroup.getName());
                        if (ViewUtils.generateConfirmationDialogue(header, content)) {
                            workerGroup.getWorkers().remove(worker);

                            if (workerGroup instanceof Stable) {
                                gameController.getStableManager().removeStableMember(worker, (Stable) workerGroup);
                            } else if (workerGroup instanceof RosterSplit) {
                                gameController.getRosterSplitManager().removeFromRosterSplit(worker, (RosterSplit) workerGroup);
                            }

                            updateLabels();
                        }
                    });
                    controller.setEditable(workerGroup.getOwner().equals(playerPromotion()));
                    setGraphic(groupMemberScreen.pane);
                }
            }
        });
    }

    public void setBrowseMode(BrowseMode browseMode) {
        editLabel.setCurrent(browseMode);
    }

    @Override
    public void initializeMore() {
        GameScreen screen = ViewUtils.loadScreenFromResource(ScreenCode.EDIT_LABEL, mainApp, gameController, nameAnchor);
        editLabel = (EditLabel) screen.controller;

        addButton.setOnAction(a -> {
            List<Worker> workers = new ArrayList<>(gameController.getWorkerManager().getRoster(playerPromotion()));
            workers.removeAll(workerGroup.getWorkers());

            Optional<Worker> result = ViewUtils.selectWorkerDialog(
                    workers,
                    workerGroup.getName(),
                    String.format("Select a worker to join %s", workerGroup.getName())
            ).showAndWait();

            result.ifPresent(worker -> {
                workerGroup.getWorkers().add(worker);

                if (workerGroup instanceof Stable) {
                    gameController.getStableManager().addMemberToStable(worker, (Stable) workerGroup);
                } else if (workerGroup instanceof RosterSplit) {
                    gameController.getRosterSplitManager().addWorkerToRosterSplit(worker, (RosterSplit) workerGroup);
                }

                updateLabels();
            });
        });
    }

    @Override
    public void setCurrent(Object object) {
        if (workerGroup != object) {
            workerGroup = (WorkerGroup) object;
            workerGroup.setOwner(gameController.getPromotionManager().refreshPromotion(workerGroup.getOwner()));
            editLabel.setCurrent(object);
            updateLabels();
        }
    }

    @Override
    public void updateLabels() {
        if (workerGroup != null) {
            ownerLabel.setText(workerGroup.getOwner().getName());
            listView.setItems(FXCollections.observableArrayList(workerGroup.getWorkers()));
        }

        gridPane.setVisible(workerGroup != null);
        addButton.setVisible(workerGroup != null && workerGroup.getOwner().equals(playerPromotion()));
    }

}
