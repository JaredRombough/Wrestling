package wrestling.view.financial.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import static wrestling.model.constants.UIConstants.VIEW_ICON;
import wrestling.model.segmentEnum.BrowseMode;
import wrestling.model.segmentEnum.StaffType;
import wrestling.model.utility.StaffUtils;
import wrestling.view.browser.controller.BrowseParams;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.interfaces.ControllerBase;

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

    /**
     * @param object
     */
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

            double coverage = StaffUtils.getStaffCoverage(playerPromotion(), staffType);
            progressBar.setProgress(coverage / 100);

            ratioLabel.setText(coverage > 100 ? "100%+" : String.format("%.0f%%", coverage));

            int avgSkill = StaffUtils.getStaffSkillAverage(staffType, playerPromotion());
            averageSkillLabel.setText(String.format("%d", avgSkill));

            modifierLabel.setText("" + StaffUtils.getStaffSkillModifier(staffType, playerPromotion()));
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
