package openwrestling.view.browser.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.view.results.controller.ResultsCardController;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;

import java.net.URL;
import java.util.ResourceBundle;

public class StaffViewController extends ControllerBase {

    @FXML
    private Label nameLabel;

    @FXML
    private Label contractLabel;

    @FXML
    private Label staffTypeLabel;

    @FXML
    private Label skillLabel;

    @FXML
    private Label behaviourLabel;

    @FXML
    private Label ageLabel;

    @FXML
    private Label genderLabel;

    @FXML
    private AnchorPane imageAnchor;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private AnchorPane contractAnchor;
    private GameScreen contractScreen;

    private StaffMember staffMember;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void initializeMore() {
        contractScreen = ViewUtils.loadScreenFromResource(ScreenCode.CONTRACT, mainApp, gameController, contractAnchor);
    }

    @Override
    public void setCurrent(Object object) {
        anchorPane.setVisible(object != null);
        if (object instanceof StaffMember) {
            this.staffMember = (StaffMember) object;
            contractScreen.controller.setCurrent(staffMember);
            updateLabels();
        }
    }

    @Override
    public void updateLabels() {
        if (staffMember != null) {
            nameLabel.setText(staffMember.getName());
            staffTypeLabel.setText(staffMember.getStaffType().toString());
            skillLabel.setText(Integer.toString(staffMember.getSkill()));
            behaviourLabel.setText(Integer.toString(staffMember.getBehaviour()));
            ageLabel.setText(Integer.toString(staffMember.getAge()));
            genderLabel.setText(staffMember.getGender().toString());

            imageAnchor.getChildren().clear();
            GameScreen card = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController, imageAnchor);
            card.controller.setCurrent(staffMember);
            ((ResultsCardController) card.controller).setNameLabelVisible(false);

            contractScreen.controller.updateLabels();
        }
    }

}
