package wrestling.view.financial.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import wrestling.model.segmentEnum.StaffType;
import wrestling.view.utility.interfaces.ControllerBase;

public class DepartmentController extends ControllerBase {

    @FXML
    private Label departmentNameLabel;

    @FXML
    private Label idealLabel;

    @FXML
    private Label actualLabel;

    @FXML
    private Label effectsLabel;

    private StaffType staffType;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * @param object
     */
    @Override
    public void setCurrent(Object object) {
        if (object instanceof StaffType) {
            this.staffType = (StaffType) object;
        }

    }

    @Override
    public void updateLabels() {
        switch (staffType) {
            case MEDICAL:
                actualLabel.setText(Integer.toString(playerPromotion().getStaff(StaffType.MEDICAL).size()));
                break;
        }
    }

}
