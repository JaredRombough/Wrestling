package wrestling.view.financial.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import wrestling.model.segmentEnum.StaffType;
import wrestling.view.utility.interfaces.ControllerBase;

public class RingsideController extends ControllerBase {

    private StaffType staffType;

    @FXML
    private Label departmentNameLabel;

    @FXML
    private Label staffCount;

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
            updateLabels();
        }
    }

    @Override
    public void updateLabels() {
        if (staffType != null) {
            departmentNameLabel.setText(staffType.toString());
            staffCount.setText(Integer.toString(playerPromotion().getStaff(staffType).size()));
        }
    }

}
