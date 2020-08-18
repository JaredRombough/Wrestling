package openwrestling.view.browser.controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.VBox;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.comparators.NameComparator;

import java.util.ArrayList;
import java.util.List;

public class EditBroadcastTeamDialog {

    private final String EMPTY = "Empty";
    private boolean updating = false;
    private StaffMember empty;

    public Dialog<List<StaffMember>> getDialog(List<StaffMember> broadcastStaff,
                                               List<StaffMember> defaultTeam,
                                               String targetName) {
        Dialog<List<StaffMember>> dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        dialog.setTitle("Edit Broadcast Team");
        dialog.setHeaderText(String.format("Select broadcast team for %s", targetName));
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        VBox vBox = new VBox(8);

        empty = new StaffMember();
        empty.setName(EMPTY);

        broadcastStaff.sort(new NameComparator());
        broadcastStaff.add(0, empty);

        List<ComboBox<StaffMember>> comboBoxes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            comboBoxes.add(new ComboBox<>(FXCollections.observableArrayList(broadcastStaff)));
        }

        for (ComboBox<StaffMember> comboBox : comboBoxes) {
            List<ComboBox<StaffMember>> otherComboBoxes = new ArrayList<>(comboBoxes);
            otherComboBoxes.remove(comboBox);
            initializeComboBox(comboBox, otherComboBoxes);
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
                List<StaffMember> newBroadcastTeam = new ArrayList<>();
                comboBoxes.forEach(cb -> {
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

    private void initializeComboBox(ComboBox<StaffMember> comboBox, List<ComboBox<StaffMember>> otherComboBoxes) {
        comboBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends StaffMember> observable, StaffMember oldValue, StaffMember newValue) -> {
            if (newValue != null && oldValue != null && newValue != oldValue) {
                updateCreateTeamComboBox(newValue, oldValue, otherComboBoxes);
            }
        });
    }

    private void updateCreateTeamComboBox(StaffMember newSelection, StaffMember oldSelection, List<ComboBox<StaffMember>> otherComboBoxes) {
        if (!updating) {
            updating = true;
            for (ComboBox<StaffMember> comboBox : otherComboBoxes) {
                if (!newSelection.getName().equals(EMPTY)) {
                    comboBox.getItems().remove(newSelection);
                }
                if (!oldSelection.getName().equals(EMPTY)) {
                    comboBox.getItems().add(oldSelection);
                }
                StaffMember selected = comboBox.getSelectionModel().getSelectedItem();
                List<StaffMember> items = new ArrayList<>(comboBox.getItems());
                items.remove(empty);
                items.sort(new NameComparator());
                items.add(0, empty);
                comboBox.setItems(FXCollections.observableArrayList(items));
                comboBox.getSelectionModel().select(selected);
            }
            updating = false;
        }
    }
}
