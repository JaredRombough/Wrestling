package wrestling.view.financial.controller;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import static wrestling.model.constants.UIConstants.EDIT_ICON;
import static wrestling.model.constants.UIConstants.VIEW_ICON;
import wrestling.model.modelView.StaffView;
import wrestling.model.segmentEnum.BrowseMode;
import wrestling.model.segmentEnum.StaffType;
import wrestling.model.utility.ModelUtils;
import wrestling.model.utility.StaffUtils;
import wrestling.view.browser.controller.BrowseParams;
import wrestling.view.browser.controller.EditBroadcastTeamDialog;
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

    @FXML
    private Button editButton;

    @FXML
    private Label defaultBroadcastTeamLabel;

    @FXML
    private Label defaultBroadcastTeam;

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
            if (staffType.equals(staffType.OWNER)) {

                staffCount.setText(playerPromotion().getOwner() != null ? playerPromotion().getOwner().getName() : "None");
            } else {
                staffCount.setText(String.format("Count: %d", StaffUtils.getStaff(staffType, playerPromotion()).size()));
            }

            defaultBroadcastTeam.setText(ModelUtils.slashShortNames(playerPromotion().getDefaultBroadcastTeam()));
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

        if (staffType.equals(StaffType.OWNER)) {
            addButton.setVisible(false);
        } else {
            addButton.setOnAction(e -> {
                params.browseMode = BrowseMode.HIRE_STAFF;
                mainApp.show(ScreenCode.BROWSER, params);
            });
        }

        editButton.setText(EDIT_ICON);

        if (staffType.equals(StaffType.BROADCAST)) {
            editButton.setOnAction(e -> {
                EditBroadcastTeamDialog dialog = new EditBroadcastTeamDialog();
                Optional<List<StaffView>> optionalResult = dialog.getDialog(
                        gameController,
                        playerPromotion(),
                        playerPromotion().getDefaultBroadcastTeam()
                ).showAndWait();
                optionalResult.ifPresent((List<StaffView> broadcastTeam) -> {
                    playerPromotion().setDefaultBroadcastTeam(broadcastTeam);
                    updateLabels();
                });
            });
        } else {
            defaultBroadcastTeamLabel.setVisible(false);
            defaultBroadcastTeam.setVisible(false);
            editButton.setVisible(false);
        }

    }

}