package wrestling.view.browser.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import wrestling.model.StaffContract;
import wrestling.model.modelView.StaffView;
import wrestling.view.results.controller.ResultsCardController;
import wrestling.view.utility.GameScreen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class StaffViewController extends ControllerBase {

    @FXML
    private Label nameLabel;

    @FXML
    private Label staffTypeLabel;

    @FXML
    private Label contractText;

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

    private StaffView staffView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setCurrent(Object object) {
        if (object instanceof StaffView) {
            this.staffView = (StaffView) object;
            updateLabels();
        }
    }

    @Override
    public void updateLabels() {
        if (staffView != null) {
            nameLabel.setText(staffView.getName());
            staffTypeLabel.setText(staffView.getStaffType().toString());
            skillLabel.setText(Integer.toString(staffView.getSkill()));
            behaviourLabel.setText(Integer.toString(staffView.getBehaviour()));
            ageLabel.setText(Integer.toString(staffView.getAge()));
            genderLabel.setText(staffView.getGender().toString());

            StaffContract contract = gameController.getContractManager().getContract(staffView);
            if (contract != null) {
                contractText.setText(gameController.getContractManager().getTerms(contract));
            }

            imageAnchor.getChildren().clear();
            GameScreen card = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController, imageAnchor);
            card.controller.setCurrent(staffView);
            ((ResultsCardController) card.controller).setNameLabelVisibile(false);
        }

    }

}
