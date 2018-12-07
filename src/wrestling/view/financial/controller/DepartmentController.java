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
            progressBar.getStyleClass().remove("progress-bar-level-2");
            double coverage = StaffUtils.getStaffCoverage(playerPromotion(), staffType);
            progressBar.setProgress(coverage / 100);

            ratioLabel.setText(coverage > 200 ? "200%+" : String.format("%.0f%%", coverage));
            if (coverage > 100) {
                progressBar.getStyleClass().add("progress-bar-level-2");
            }
            if (coverage > 100 && coverage < 200) {

                progressBar.setProgress((coverage - 100) / 100);
            }

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
                    double skillModifier = StaffUtils.getMatchRatingModifier(playerPromotion());
                    sb.append(String.format("%.0f%% match rating %s (skill)",
                            skillModifier * 100,
                            skillModifier >= 0 ? "bonus" : "penalty"
                    ));
                    sb.append("\n");
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
            effectsLabel.setText(sb.toString());

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
