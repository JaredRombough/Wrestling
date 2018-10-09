package wrestling.view.browser.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import wrestling.model.modelView.PromotionView;
import wrestling.model.SegmentItem;
import wrestling.model.controller.GameController;
import wrestling.model.interfaces.iContract;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.TagTeamView;
import wrestling.model.modelView.WorkerView;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.comparators.NameComparator;

public class ContractDialog {

    private SegmentItem segmentItem;
    private boolean exclusive = true;

    public void createDialog(SegmentItem item, GameController gameController) {
        this.segmentItem = item;
        Dialog dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        PromotionView playerPromotion = gameController.getPromotionManager().playerPromotion();

        Label costLabel = new Label();

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
        typeComboBox.getSelectionModel().selectFirst();
        typeComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                exclusive = newValue.equals("Exclusive");
                updateCostLabel(costLabel, gameController);
            }
        });
        lengthComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateCostLabel(costLabel, gameController);
            }
        });

        VBox vBox = new VBox(8);

        dialog.setTitle(String.format("Sign Contract"));
        dialog.setHeaderText("Terms");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ViewUtils.addRegionWrapperToVBox(typeComboBox, "Type:", vBox);
        ViewUtils.addRegionWrapperToVBox(lengthComboBox, "Months:", vBox);
        ViewUtils.addRegionWrapperToVBox(costLabel, "Cost:", vBox);

        dialogPane.setContent(vBox);
        updateCostLabel(costLabel, gameController);
        dialogPane.getStylesheets().add("style.css");

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (segmentItem instanceof WorkerView) {
                gameController.getContractFactory().createContract(
                        (WorkerView) segmentItem,
                        playerPromotion,
                        typeComboBox.getSelectionModel().selectedItemProperty().getValue().equals("Exclusive"),
                        duration(lengthComboBox),
                        gameController.getDateManager().today());
            } else if (segmentItem instanceof StaffView) {
                gameController.getContractFactory().createContract((StaffView) segmentItem,
                        playerPromotion,
                        gameController.getDateManager().today(),
                        duration(lengthComboBox));

            }
        }
    }

    private void updateCostLabel(Label label, GameController gameController) {
        String terms = "$";

        if (segmentItem instanceof WorkerView) {
            terms += gameController.getContractFactory().calculateAppearanceCost((WorkerView) segmentItem, exclusive);
        } else {
            terms += gameController.getContractFactory().calculateBiWeeklyCost((StaffView) segmentItem);
        }

        if (exclusive) {
            terms += " Bi-Weekly";
        } else {
            terms += " per Apperance";
        }

        label.setText(terms);
    }

    private int duration(ComboBox lengthComboBox) {
        return (lengthComboBox.getSelectionModel().getSelectedIndex() + 1) * 30;
    }

}
