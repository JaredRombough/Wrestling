package wrestling.view.financial.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import wrestling.model.segmentEnum.StaffType;
import wrestling.model.utility.ModelUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class DepartmentController extends ControllerBase {

    @FXML
    private Label departmentNameLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label ratioLabel;

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
                    sb.append(String.format("%d avg skill", avgSkill));
                    int diff = ModelUtils.getSkillDifferential(playerPromotion(), staffType);
                    if (diff > 0) {
                        sb.append(String.format(" (+%d)", diff));
                    } else if (diff < 0) {
                        sb.append(String.format(" (-%d)", Math.abs(diff)));
                    }
                    sb.append("\n");
                    sb.append(String.format("%%%.3f injury rate", ModelUtils.getInjuryRate(playerPromotion()) * 100));
                    effectsLabel.setText(sb.toString());
                    break;
            }

            departmentNameLabel.setText(staffType.toString());
        }

    }

}
