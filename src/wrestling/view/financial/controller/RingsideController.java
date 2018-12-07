package wrestling.view.financial.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import wrestling.model.segmentEnum.BrowseMode;
import wrestling.model.segmentEnum.StaffType;
import wrestling.view.browser.controller.BrowseParams;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.interfaces.ControllerBase;

public class RingsideController extends ControllerBase {

    private StaffType staffType;

    @FXML
    private Label departmentNameLabel;

    @FXML
    private Label staffCount;

    @FXML
    private Button viewButton;

    @FXML
    private Button addButton;

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
            setButtonActions();
        }
    }

    @Override
    public void updateLabels() {
        if (staffType != null) {
            departmentNameLabel.setText(staffType.toString());
            staffCount.setText(Integer.toString(playerPromotion().getStaff(staffType).size()));
        }
    }

    private void setButtonActions() {
        viewButton.setText("\uD83D\uDC41");
        BrowseParams params = new BrowseParams();
        params.filter = staffType;
        params.promotion = playerPromotion();

        viewButton.setOnAction(e -> {
            params.browseMode = BrowseMode.STAFF;
            mainApp.show(ScreenCode.BROWSER, params);
        });

        if (staffType.equals(StaffType.OWNER)) {
            addButton.setVisible(false);
        } else {
            addButton.setOnAction(e -> {
                params.browseMode = BrowseMode.HIRE_STAFF;
                mainApp.show(ScreenCode.BROWSER, params);
            });
        }

    }

}
