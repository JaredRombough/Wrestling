package openwrestling.view.browser.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import openwrestling.model.modelView.WorkerGroup;
import openwrestling.model.modelView.WorkerView;
import openwrestling.model.segmentEnum.BrowseMode;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;

public class StableController extends ControllerBase {

    @FXML
    private AnchorPane nameAnchor;

    @FXML
    private Label ownerLabel;

    @FXML
    private GridPane gridPane;

    @FXML
    private ListView listView;

    @FXML
    private Button addButton;

    private EditLabel editLabel;

    private WorkerGroup workerGroup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listView.setCellFactory(c -> new ListCell<WorkerView>() {
            @Override
            public void updateItem(WorkerView worker, boolean empty) {
                super.updateItem(worker, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    GameScreen stableMemberScreen = ViewUtils.loadScreenFromFXML(ScreenCode.GROUP_MEMBER, mainApp, gameController);
                    GroupMemberController cotroller = (GroupMemberController) stableMemberScreen.controller;
                    cotroller.setCurrent(worker);
                    cotroller.getxButton().setOnAction(a -> {
                        String header = String.format("Removing worker from %s", workerGroup.getName());
                        String content = String.format("Really remove %s from %s?", worker.getName(), workerGroup.getName());
                        if (ViewUtils.generateConfirmationDialogue(header, content)) {
                            workerGroup.getWorkers().remove(worker);
                            updateLabels();
                        }
                    });
                    cotroller.setEditable(workerGroup.getOwner().equals(playerPromotion()));
                    setGraphic(stableMemberScreen.pane);
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

            List<WorkerView> workers = new ArrayList<>(playerPromotion().getFullRoster());
            workers.removeAll(workerGroup.getWorkers());

            Optional<WorkerView> result = ViewUtils.selectWorkerDialog(
                    workers,
                    workerGroup.getName(),
                    String.format("Select a worker to join %s", workerGroup.getName())
            ).showAndWait();

            result.ifPresent(worker -> {
                workerGroup.getWorkers().add(worker);
                updateLabels();
            });
        });
    }

    @Override
    public void setCurrent(Object object) {
        workerGroup = (WorkerGroup) object;
        editLabel.setCurrent(object);
        updateLabels();
    }

    @Override
    public void updateLabels() {
        if (workerGroup != null) {
            ownerLabel.setText(workerGroup.getOwner().getName());
            listView.setItems(FXCollections.observableArrayList(workerGroup.getWorkers()));
        }

        gridPane.setVisible(workerGroup != null);
        addButton.setVisible(workerGroup != null && workerGroup.getOwner().equals(playerPromotion()));
        ownerLabel.setVisible(workerGroup != null && workerGroup.getOwner().equals(playerPromotion()));
    }

}
