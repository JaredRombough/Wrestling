package wrestling.view.financial.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import wrestling.model.segmentEnum.BrowseMode;
import wrestling.model.segmentEnum.StaffType;
import wrestling.model.utility.ModelUtils;
import wrestling.view.browser.controller.BrowseParams;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.interfaces.ControllerBase;

public class DepartmentController extends ControllerBase {

    @FXML
    private Label departmentNameLabel;

    @FXML
    private Label skillDifferentialLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label ratioLabel;

    @FXML
    private Label actualLabel;

    @FXML
    private Label effectsLabel;

    @FXML
    private Button viewButton;

    @FXML
    private Button addButton;

    private StaffType staffType;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewButton.setText("\uD83D\uDC41");
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
            switch (staffType) {
                case MEDICAL:
                    double progress = (double) playerPromotion().getStaff(staffType).size()
                            / ModelUtils.getMedicsRequired(playerPromotion());
                    progressBar.setProgress(progress);

                    ratioLabel.setText(String.format("%d/%d",
                            playerPromotion().getStaff(staffType).size(),
                            ModelUtils.getMedicsRequired(playerPromotion())));

                    StringBuilder sb = new StringBuilder();
                    int avgSkill = playerPromotion().getStaffSkillAverage(staffType);
                    sb.append(String.format("%d", avgSkill));
                    int diff = ModelUtils.getSkillDifferential(playerPromotion(), staffType);
                    if (diff > 0) {
                        sb.append(String.format(" (+%d)", diff));
                    } else if (diff < 0) {
                        sb.append(String.format(" (-%d)", Math.abs(diff)));
                    }

                    skillDifferentialLabel.setText(sb.toString());

                    sb = new StringBuilder();
                    sb.append(String.format("%%%.2f injury rate", ModelUtils.getInjuryRate(playerPromotion()) * 100));
                    sb.append("\n");
                    int durationModifier = ModelUtils.getInjuryDurationModifier(playerPromotion());
                    if (durationModifier > 0) {
                        sb.append("+");
                    }
                    sb.append(String.format("%d day%s to injuries",
                            durationModifier,
                            Math.abs(durationModifier) > 1 ? "s" : ""));
                    effectsLabel.setText(sb.toString());
                    break;
            }

            departmentNameLabel.setText(staffType.toString());
        }

    }

    private void setButtonActions() {
        BrowseParams params = new BrowseParams();
        params.filter = staffType;
        params.promotion = playerPromotion();

        viewButton.setOnAction(e -> {
            params.broseMode = BrowseMode.STAFF;
            mainApp.show(ScreenCode.BROWSER, params);
        });

        addButton.setOnAction(e -> {
            params.broseMode = BrowseMode.HIRE_STAFF;
            mainApp.show(ScreenCode.BROWSER, params);
        });
    }

}
