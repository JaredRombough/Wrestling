package wrestling.view.browser.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import wrestling.model.modelView.StableView;
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

    private EditLabel editLabel;

    private StableView stable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
    }

}
