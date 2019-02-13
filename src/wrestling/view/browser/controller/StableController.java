package wrestling.view.browser.controller;

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
import wrestling.model.modelView.StableView;
import wrestling.model.modelView.WorkerView;
import wrestling.view.utility.GameScreen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

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

    private StableView stable;

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
                    GameScreen stableMemberScreen = ViewUtils.loadScreenFromResource(ScreenCode.STABLE_MEMBER, mainApp, gameController);
                    StableMemberController cotroller = (StableMemberController) stableMemberScreen.controller;
                    cotroller.setCurrent(worker);
                    cotroller.getxButton().setOnAction(a -> {
                        String header = "Removing worker from stable";
                        String content = String.format("Really remove %s from %s?", worker.getName(), stable.getName());
                        if (ViewUtils.generateConfirmationDialogue(header, content)) {
                            stable.getWorkers().remove(worker);
                            updateLabels();
                        }
                    });
                    cotroller.getxButton().setVisible(stable.getOwner().equals(playerPromotion()));
                    setGraphic(stableMemberScreen.pane);
                }
            }
        });
    }

    @Override
    public void initializeMore() {
        GameScreen screen = ViewUtils.loadScreenFromResource(ScreenCode.EDIT_LABEL, mainApp, gameController, nameAnchor);
        editLabel = (EditLabel) screen.controller;

        addButton.setOnAction(a -> {

            List<WorkerView> workers = new ArrayList<>(playerPromotion().getFullRoster());
            workers.removeAll(stable.getWorkers());

            Optional<WorkerView> result = ViewUtils.selectWorkerDialog(
                    workers,
                    stable.getName(),
                    String.format("Select a worker to join %s", stable.getName())
            ).showAndWait();

            result.ifPresent(worker -> {
                stable.getWorkers().add(worker);
                updateLabels();
            });
        });
    }

    @Override
    public void setCurrent(Object object) {
        if (object instanceof StableView) {
            stable = (StableView) object;
            updateLabels();
        }
        editLabel.setCurrent(object);
        gridPane.setVisible(object != null);
        addButton.setVisible(stable.getOwner().equals(playerPromotion()));
    }

    @Override
    public void updateLabels() {
        ownerLabel.setText(stable.getOwner().getName());
        listView.setItems(FXCollections.observableArrayList(stable.getWorkers()));
    }

}
