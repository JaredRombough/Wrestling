package wrestling.view.browser.controller;

import java.net.URL;
import java.util.ResourceBundle;
import wrestling.model.modelView.StaffView;
import wrestling.view.utility.interfaces.ControllerBase;

public class StaffViewController extends ControllerBase {

    private StaffView staffView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setStaff(StaffView staff) {
        this.staffView = staff;
    }

}
