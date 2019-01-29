package wrestling.view.browser.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import wrestling.model.interfaces.iContract;
import wrestling.model.interfaces.iPerson;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.utility.ContractUtils;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class ContractController extends ControllerBase {

    @FXML
    private Label contractLabel;

    @FXML
    private Label contractText;

    @FXML
    private Label contractTypeLabel;

    @FXML
    private Label contractTypeText;

    @FXML
    private Label contractDurationLabel;

    @FXML
    private Label contractDurationText;

    @FXML
    private Button contractButton;

    private iPerson person;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void setCurrent(Object object) {
        if (object instanceof WorkerView || object instanceof StaffView) {
            person = (iPerson) object;
            updateLabels();
        }
    }

    @Override
    public void updateLabels() {

        if (person != null) {

            boolean hasContract = person.getContract() != null;

            contractText.setVisible(hasContract);
            contractLabel.setVisible(hasContract);

            if (hasContract) {
                contractLabel.setText(person.getContract().isExclusive() ? "Exclusive Contract" : "Open Contract");
            }

            iContract contract = person.getContract(playerPromotion());

            if (contract != null) {
                contractTypeLabel.setVisible(true);
                contractTypeLabel.setText(contract.isExclusive() ? "Monthly" : "Appearance");
                contractTypeText.setVisible(true);
                contractTypeText.setText(String.format("$%d", contract.isExclusive() ? contract.getMonthlyCost() : contract.getAppearanceCost()));
                contractDurationText.setText(ContractUtils.contractDurationString(contract, gameController.getDateManager().today()));
            } else {
                contractTypeText.setVisible(false);
                contractTypeLabel.setVisible(false);
                contractDurationText.setVisible(false);
                contractDurationLabel.setVisible(false);
            }

            contractText.setText(gameController.getContractManager().contractPromotionsString(person, gameController.getDateManager().today()));

            if (gameController.getContractManager().canNegotiate(person, playerPromotion())) {
                contractButton.setVisible(true);
                contractButton.setText("Sign Contract");
                contractButton.setOnAction(e -> {
                    ContractDialog contractDialog = new ContractDialog();
                    contractDialog.createDialog(person, gameController);
                    mainApp.updateLabels(ScreenCode.BROWSER);
                });
            } else if (person.getContract(playerPromotion()) != null) {
                contractButton.setVisible(true);
                contractButton.setText("Release Worker");
                contractButton.setOnAction(e -> {
                    if (ViewUtils.releaseWorkerDialog(person, playerPromotion(), today())) {
                        gameController.getContractManager().terminateContract(contract);
                        mainApp.updateLabels(ScreenCode.BROWSER);
                    }
                });

            } else {
                contractButton.setVisible(false);
            }
        }

    }

}
