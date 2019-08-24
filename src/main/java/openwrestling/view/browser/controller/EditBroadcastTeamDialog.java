package openwrestling.view.browser.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.VBox;
import openwrestling.model.controller.GameController;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.modelView.StaffView;
import openwrestling.model.segmentEnum.StaffType;
import openwrestling.model.utility.StaffUtils;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.comparators.NameComparator;

public class EditBroadcastTeamDialog {

    private boolean updating = false;
    private final String EMPTY = "Empty";
    private List<StaffView> broadcastStaff;
    private StaffView empty;

    public Dialog<List<StaffView>> getDialog(GameController gameController, Promotion promotion, List<StaffView> defaultTeam) {
        Dialog<List<StaffView>> dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        dialog.setTitle("Edit Broadcst Team");
        dialog.setHeaderText("Select members");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        VBox vBox = new VBox(8);

        empty = new StaffView();
        empty.setName(EMPTY);

        broadcastStaff = StaffUtils.getStaff(StaffType.BROADCAST, promotion);
        Collections.sort(broadcastStaff, new NameComparator());
        broadcastStaff.add(0, empty);

        List<ComboBox<StaffView>> comboBoxes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            comboBoxes.add(new ComboBox(FXCollections.observableArrayList(broadcastStaff)));
        }

        for (ComboBox<StaffView> comboBox : comboBoxes) {
            List<ComboBox<StaffView>> otherComboBoxes = new ArrayList<>(comboBoxes);
            otherComboBoxes.remove(comboBox);
            initailizeComboBox(comboBox, otherComboBoxes);
            comboBox.getSelectionModel().selectFirst();
            ViewUtils.addRegionWrapperToVBox(comboBox, "Commentary:", vBox);
        }

        for (int i = 0; i < defaultTeam.size(); i++) {
            if (broadcastStaff.contains(defaultTeam.get(i))) {
                comboBoxes.get(i).getSelectionModel().select(defaultTeam.get(i));
            }
        }

        dialogPane.setContent(vBox);
        dialogPane.getStylesheets().add("style.css");

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                List<StaffView> newBroadcastTeam = new ArrayList<>();
                comboBoxes.stream().forEach(cb -> {
                    if (!cb.getSelectionModel().getSelectedItem().equals(empty)) {
                        newBroadcastTeam.add(cb.getSelectionModel().getSelectedItem());
                    }
                });
                return newBroadcastTeam;
            }
            return null;
        });
        return dialog;
    }

    private void initailizeComboBox(ComboBox<StaffView> comboBox, List<ComboBox<StaffView>> otherComboBoxes) {
        comboBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends StaffView> observable, StaffView oldValue, StaffView newValue) -> {
            if (newValue != null && oldValue != null && newValue != oldValue) {
                updateCreateTeamComboBox(newValue, oldValue, otherComboBoxes);
            }
        });
    }

    private void updateCreateTeamComboBox(StaffView newSelection, StaffView oldSelection, List<ComboBox<StaffView>> otherComboBoxes) {
        if (!updating) {
            updating = true;
            for (ComboBox<StaffView> comboBox : otherComboBoxes) {
                if (!newSelection.getName().equals(EMPTY)) {
                    comboBox.getItems().remove(newSelection);
                }
                if (!oldSelection.getName().equals(EMPTY)) {
                    comboBox.getItems().add(oldSelection);
                }
                StaffView selected = comboBox.getSelectionModel().getSelectedItem();
                List<StaffView> items = new ArrayList<>(comboBox.getItems());
                items.remove(empty);
                Collections.sort(items, new NameComparator());
                items.add(0, empty);
                comboBox.setItems(FXCollections.observableArrayList(items));
                comboBox.getSelectionModel().select(selected);
            }
            updating = false;
        }
    }
}
