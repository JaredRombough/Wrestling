package openwrestling.view.browser.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import openwrestling.model.controller.GameController;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.StaffContract;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.interfaces.iPerson;
import openwrestling.model.utility.ContractUtils;
import openwrestling.model.utility.ModelUtils;
import openwrestling.view.utility.ViewUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContractDialog {

    private iPerson person;
    private boolean exclusive;

    public void createDialog(iPerson person, GameController gameController) {
        this.person = person;
        Dialog dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        Promotion playerPromotion = gameController.getPromotionManager().getPlayerPromotion();

        Label costLabel = new Label();
        Label endDate = new Label();
        Label signingFee = new Label();

        ComboBox lengthComboBox = new ComboBox();
        List<String> lengthList = new ArrayList<>();
        for (Integer i = 1; i <= 12; i++) {
            lengthList.add(i.toString());
        }
        lengthComboBox.getItems().addAll(lengthList);
        lengthComboBox.getSelectionModel().selectFirst();

        ComboBox typeComboBox = new ComboBox();
        List<String> exclusiveOpen = new ArrayList<>();
        if (person instanceof Worker) {
            exclusiveOpen.add("Open");
        }
        if (playerPromotion.getLevel() == 5 || person instanceof StaffMember) {
            exclusiveOpen.add("Exclusive");
        }
        typeComboBox.getItems().addAll(exclusiveOpen);
        typeComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                exclusive = newValue.equals("Exclusive");
                updateCostLabel(costLabel);
                updateSigningFeeLabel(signingFee, gameController.getDateManager().today());
            }
        });
        typeComboBox.getSelectionModel().selectFirst();
        lengthComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateCostLabel(costLabel);
                endDate.setText(ContractUtils.contractEndDate(gameController.getDateManager().today(), lengthComboBox.getSelectionModel().getSelectedIndex() + 1).toString());
            }
        });
        lengthComboBox.getSelectionModel().selectFirst();
        VBox vBox = new VBox(8);

        dialog.setTitle(String.format("Sign Contract"));
        dialog.setHeaderText("Terms");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        endDate.setText(ContractUtils.contractEndDate(gameController.getDateManager().today(), lengthComboBox.getSelectionModel().getSelectedIndex() + 1).toString());

        ViewUtils.addRegionWrapperToVBox(typeComboBox, "Type:", vBox);
        ViewUtils.addRegionWrapperToVBox(lengthComboBox, "Months:", vBox);
        ViewUtils.addRegionWrapperToVBox(endDate, "Ends:", vBox);
        ViewUtils.addRegionWrapperToVBox(costLabel, "Cost:", vBox);
        ViewUtils.addRegionWrapperToVBox(signingFee, "Signing Fee:", vBox);

        dialogPane.setContent(vBox);
        updateCostLabel(costLabel);
        updateSigningFeeLabel(signingFee, gameController.getDateManager().today());
        dialogPane.getStylesheets().add("style.css");

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (person instanceof Worker) {
                Contract contract = gameController.getContractFactory().createContract(
                        (Worker) person,
                        playerPromotion,
                        typeComboBox.getSelectionModel().selectedItemProperty().getValue().equals("Exclusive"),
                        lengthComboBox.getSelectionModel().getSelectedIndex() + 1,
                        gameController.getDateManager().today());
                gameController.getContractManager().createContracts(List.of(contract));
            } else if (person instanceof StaffMember) {
                StaffContract staffContract = gameController.getContractFactory().createContract((StaffMember) person,
                        playerPromotion,
                        gameController.getDateManager().today(),
                        lengthComboBox.getSelectionModel().getSelectedIndex() + 1);
                gameController.getContractManager().createStaffContracts(List.of(staffContract));
            }
        }
    }

    private void updateCostLabel(Label label) {
        label.setText(String.format("%s %s",
                ModelUtils.currencyString(person instanceof Worker ? ContractUtils.calculateWorkerContractCost((Worker) person, exclusive)
                        : ContractUtils.calculateStaffContractCost((StaffMember) person)),
                exclusive ? "Monthly" : "per Apperance"));
    }

    private void updateSigningFeeLabel(Label label, LocalDate startDate) {
        label.setText(ModelUtils.currencyString(
                exclusive ? ContractUtils.calculateSigningFee(person, startDate) : 0));
    }

}
