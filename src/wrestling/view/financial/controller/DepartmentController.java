package wrestling.view.financial.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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
                case CREATIVE:
                    double crowdModifier = StaffUtils.getCreativeCrowdReactionModifer(playerPromotion());
                    sb.append(String.format("%.0f%% crowd reaction %s (coverage)",
                            crowdModifier * 100,
                            crowdModifier >= 0 ? "bonus" : "penalty"
                    ));
                    sb.append("\n");
                    double angleModifier = StaffUtils.getAngleRatingModifier(playerPromotion());
                    sb.append(String.format("%.0f%% angle rating %s (skill)",
                            angleModifier * 100,
                            angleModifier >= 0 ? "bonus" : "penalty"
                    ));
                    sb.append("\n");
                    break;
                case TRAINER:
                    double trainerRate = StaffUtils.getTrainerSuccessRate(playerPromotion());
                    sb.append(String.format("%.0f%% trainer success daily (coverage)",
                            trainerRate * 100));
                    break;
                case PRODUCTION:
                    double attendanceModifier = StaffUtils.getCoverageAttendanceModifier(playerPromotion());
                    sb.append(String.format("%.0f%% event attendance %s (coverage)",
                            attendanceModifier * 100,
                            attendanceModifier >= 0 ? "bonus" : "penalty"
                    ));
                    sb.append("\n");
                    double productionCrowdModifier = StaffUtils.getProductionCrowdRatingModifier(playerPromotion());
                    sb.append(String.format("%.0f%% crowd reaction %s (skill)",
                            productionCrowdModifier * 100,
                            productionCrowdModifier >= 0 ? "bonus" : "penalty"
                    ));
                    break;
            }
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

        addButton.setOnAction(e -> {
            params.browseMode = BrowseMode.HIRE_STAFF;
            mainApp.show(ScreenCode.BROWSER, params);
        });
    }

}
