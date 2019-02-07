package wrestling.view.browser.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import wrestling.model.modelView.StableView;
import wrestling.model.modelView.TitleReign;
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

    private EditLabel editLabel;

    private StableView stable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listView.setCellFactory(param -> new ListCell<WorkerView>() {

            @Override
            public void updateItem(WorkerView worker, boolean empty) {
                super.updateItem(worker, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    GameScreen titleReignScreen = ViewUtils.loadScreenFromResource(ScreenCode.STABLE_MEMBER, mainApp, gameController);
                    titleReignScreen.controller.setCurrent(worker);
                    setGraphic(titleReignScreen.pane);
                }
            }
        });
    }

    @Override
    public void initializeMore() {
        GameScreen screen = ViewUtils.loadScreenFromResource(ScreenCode.EDIT_LABEL, mainApp, gameController, nameAnchor);
        editLabel = (EditLabel) screen.controller;
    }

    @Override
    public void setCurrent(Object object) {
        if (object instanceof StableView) {
            stable = (StableView) object;
            updateLabels();
        }
        editLabel.setCurrent(object);
        gridPane.setVisible(object != null);
    }

    @Override
    public void updateLabels() {
        ownerLabel.setText(stable.getOwner().getName());
        listView.setItems(FXCollections.observableArrayList(stable.getWorkers()));
    }

}
