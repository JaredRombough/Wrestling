package wrestling.view.browser.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import wrestling.model.modelView.WorkerView;
import wrestling.view.utility.interfaces.ControllerBase;

public class StableMemberController extends ControllerBase {

    private WorkerView worker;

    @FXML
    private Label nameLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setCurrent(Object object) {
        if (object instanceof WorkerView) {
            worker = (WorkerView) object;
            updateLabels();
        }
    }

    @Override
    public void updateLabels() {
        nameLabel.setText(worker.getName());
    }

}
