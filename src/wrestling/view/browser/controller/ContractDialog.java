package wrestling.view.browser.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import wrestling.model.SegmentItem;
import wrestling.model.controller.GameController;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.utility.ContractUtils;
import wrestling.view.utility.ViewUtils;

public class ContractDialog {

    private SegmentItem segmentItem;
    private boolean exclusive;

    public void createDialog(SegmentItem item, GameController gameController) {
        this.segmentItem = item;
        Dialog dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        PromotionView playerPromotion = gameController.getPromotionManager().playerPromotion();

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
        if (segmentItem instanceof WorkerView) {
            exclusiveOpen.add("Open");
        }
        if (playerPromotion.getLevel() == 5 || segmentItem instanceof StaffView) {
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
            if (segmentItem instanceof WorkerView) {
                gameController.getContractFactory().createContract(
                        (WorkerView) segmentItem,
                        playerPromotion,
                        typeComboBox.getSelectionModel().selectedItemProperty().getValue().equals("Exclusive"),
                        lengthComboBox.getSelectionModel().getSelectedIndex() + 1,
                        gameController.getDateManager().today());
            } else if (segmentItem instanceof StaffView) {
                gameController.getContractFactory().createContract((StaffView) segmentItem,
                        playerPromotion,
                        gameController.getDateManager().today(),
                        lengthComboBox.getSelectionModel().getSelectedIndex() + 1);

            }
        }
    }

    private void updateCostLabel(Label label) {
        label.setText(String.format("$%d %s",
                segmentItem instanceof WorkerView ? ContractUtils.calculateWorkerContractCost((WorkerView) segmentItem, exclusive)
                        : ContractUtils.calculateStaffContractCost((StaffView) segmentItem),
                exclusive ? "Monthly" : "per Apperance"));
    }

    private void updateSigningFeeLabel(Label label, LocalDate startDate) {
        label.setText(String.format("$%d",
                exclusive ? ContractUtils.calculateSigningFee(segmentItem, startDate) : 0));
    }

}
