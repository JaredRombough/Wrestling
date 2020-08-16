package openwrestling.view.browser.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.interfaces.iContract;
import openwrestling.model.interfaces.iPerson;
import openwrestling.model.utility.ContractUtils;
import openwrestling.model.utility.ModelUtils;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;

import java.net.URL;
import java.util.ResourceBundle;

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
        if (object instanceof Worker || object instanceof StaffMember) {
            person = (iPerson) object;
            updateLabels();
        }
    }

    @Override
    public void updateLabels() {

        if (person != null) {

            iContract firstContract =
                    gameController.getContractManager().getContracts(person).stream()
                            .findFirst()
                            .orElse(null);

            contractText.setVisible(firstContract != null);
            contractLabel.setVisible(firstContract != null);

            if (firstContract != null) {
                contractLabel.setText(firstContract.isExclusive() ? "Exclusive Contract" : "Open Contract");
            }

            iContract playerPromotionContract = gameController.getContractManager().getContract(person, playerPromotion());

            if (playerPromotionContract != null) {
                contractTypeLabel.setVisible(true);
                contractTypeLabel.setText(playerPromotionContract.isExclusive() ? "Monthly" : "Appearance");
                contractTypeText.setVisible(true);
                contractTypeText.setText(ModelUtils.currencyString(playerPromotionContract.isExclusive() ? playerPromotionContract.getMonthlyCost() : playerPromotionContract.getAppearanceCost()));
                contractDurationText.setText(ContractUtils.contractDurationString(playerPromotionContract, gameController.getDateManager().today()));
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
            } else if (playerPromotionContract != null) {
                contractButton.setVisible(true);
                contractButton.setText("Release Worker");
                contractButton.setOnAction(e -> {
                    if (ViewUtils.releaseWorkerDialog(person,
                            playerPromotion(),
                            playerPromotionContract,
                            today())
                    ) {
                        gameController.getContractManager().terminateContract(playerPromotionContract);
                        mainApp.updateLabels(ScreenCode.BROWSER);
                    }
                });

            } else {
                contractButton.setVisible(false);
            }
        }

    }

}
