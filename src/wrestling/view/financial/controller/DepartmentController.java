package wrestling.view.financial.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import wrestling.model.segmentEnum.BrowseMode;
import wrestling.model.segmentEnum.StaffType;
import wrestling.model.utility.ModelUtils;
import wrestling.model.utility.StaffUtils;
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
            double coverage = StaffUtils.getStaffCoverage(playerPromotion(), staffType);
            progressBar.setProgress(coverage / 100);

            ratioLabel.setText(String.format("%.0f%%", coverage));

            int avgSkill = playerPromotion().getStaffSkillAverage(staffType);
            skillDifferentialLabel.setText(String.format("%d", avgSkill));

            StringBuilder sb = new StringBuilder();
            switch (staffType) {
                case MEDICAL:
                    sb.append(String.format("%.2f%% injury rate (coverage)", StaffUtils.getInjuryRate(playerPromotion()) * 100));
                    sb.append("\n");
                    int bonusDays = StaffUtils.getInjuryDurationBonusDays(playerPromotion());
                    sb.append(String.format("-%d day%s to injuries (skill)",
                            bonusDays,
                            bonusDays == 0 || Math.abs(bonusDays) > 1 ? "s" : ""));
                    break;
                case ROAD_AGENT:
                    double coverageModifer = StaffUtils.getCoverageMatchRatingModifier(playerPromotion());
                    sb.append(String.format("%.0f%% match rating %s (coverage)",
                            coverageModifer * 100,
                            coverageModifer >= 0 ? "bonus" : "penalty"
                    ));
                    sb.append("\n");
                    double skillModifier = StaffUtils.getSkillMatchRatingModifier(playerPromotion());
                    sb.append(String.format("%.0f%% match rating %s (skill)",
                            skillModifier * 100,
                            skillModifier >= 0 ? "bonus" : "penalty"
                    ));
                    sb.append("\n");
                    break;
            }
            effectsLabel.setText(sb.toString());
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
