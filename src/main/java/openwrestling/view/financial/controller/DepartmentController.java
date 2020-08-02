package openwrestling.view.financial.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import openwrestling.model.segmentEnum.BrowseMode;
import openwrestling.model.segmentEnum.StaffType;
import openwrestling.view.browser.controller.BrowseParams;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.interfaces.ControllerBase;

import java.net.URL;
import java.util.ResourceBundle;

import static openwrestling.model.constants.UIConstants.VIEW_ICON;

public class DepartmentController extends ControllerBase {

    @FXML
    private Label departmentNameLabel;

    @FXML
    private Label averageSkillLabel;

    @FXML
    private Label modifierLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label ratioLabel;

    @FXML
    private Button viewButton;

    @FXML
    private Button addButton;

    private StaffType staffType;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setCurrent(Object object) {
        if (object instanceof StaffType) {
            this.staffType = (StaffType) object;
            setButtonActions();
        }

    }

    @Override
    public void updateLabels() {
        if (staffType != null) {
            departmentNameLabel.setText(staffType.toString());

            double coverage = gameController.getStaffManager().getStaffCoverage(playerPromotion(), staffType, gameController.getWorkerManager().selectRoster(playerPromotion()));
            progressBar.setProgress(coverage / 100);

            ratioLabel.setText(coverage > 100 ? "100%+" : String.format("%.0f%%", coverage));

            int avgSkill = gameController.getStaffManager().getStaffSkillAverage(staffType, playerPromotion());
            averageSkillLabel.setText(String.format("%d", avgSkill));

            modifierLabel.setText("" + gameController.getStaffManager().getStaffSkillModifier(staffType, playerPromotion(), gameController.getWorkerManager().selectRoster(playerPromotion())));
        }
    }

    private void setButtonActions() {
        viewButton.setText(VIEW_ICON);
        BrowseParams params = new BrowseParams();
        params.filter = staffType;
        params.promotion = playerPromotion();

        viewButton.setOnAction(e -> {
            params.browseMode = BrowseMode.STAFF;
            mainApp.show(ScreenCode.BROWSER, params);
        });

        addButton.setOnAction(e -> {
            params.browseMode = BrowseMode.HIRE_STAFF;
            mainApp.show(ScreenCode.BROWSER, params);
        });
    }

}
