package openwrestling.view.financial.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.segmentEnum.BrowseMode;
import openwrestling.model.segmentEnum.StaffType;
import openwrestling.model.utility.ModelUtils;
import openwrestling.view.browser.controller.BrowseParams;
import openwrestling.view.browser.controller.EditBroadcastTeamDialog;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.interfaces.ControllerBase;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static openwrestling.model.constants.UIConstants.EDIT_ICON;
import static openwrestling.model.constants.UIConstants.VIEW_ICON;

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
            if (staffType.equals(StaffType.OWNER)) {
                StaffMember owner = gameController.getStaffManager().getOwner(playerPromotion());
                staffCount.setText(owner != null ? owner.getName() : "None");
            } else {
                staffCount.setText(String.format("Count: %d", gameController.getStaffManager().getStaff(staffType, playerPromotion()).size()));
            }

            defaultBroadcastTeam.setText(ModelUtils.slashShortNames(gameController.getBroadcastTeamManager().getDefaultBroadcastTeam(playerPromotion())));
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
                Optional<List<StaffMember>> optionalResult = dialog.getDialog(
                        gameController,
                        playerPromotion(),
                        gameController.getBroadcastTeamManager().getDefaultBroadcastTeam(playerPromotion())
                ).showAndWait();
                optionalResult.ifPresent((List<StaffMember> broadcastTeam) -> {
                    gameController.getBroadcastTeamManager().setDefaultBroadcastTeam(playerPromotion(), broadcastTeam);
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
