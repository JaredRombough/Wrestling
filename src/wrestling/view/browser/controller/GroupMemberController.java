package wrestling.view.browser.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import wrestling.model.modelView.WorkerView;
import wrestling.view.utility.interfaces.ControllerBase;

public class GroupMemberController extends ControllerBase {

    private WorkerView worker;

    @FXML
    private Label nameLabel;

    @FXML
    private Button xButton;

    @FXML
    private AnchorPane anchorPane;

    private boolean editable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        xButton.setVisible(false);
    }

    @Override
    public void initializeMore() {

    }

    public void setEditable(boolean editable) {
        anchorPane.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
            if (editable) {
                xButton.setVisible(editable && isNowHovered);
            }
        });

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

    /**
     * @return the xButton
     */
    public Button getxButton() {
        return xButton;
    }

}
