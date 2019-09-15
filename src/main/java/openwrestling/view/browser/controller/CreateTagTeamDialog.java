package openwrestling.view.browser.controller;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import openwrestling.model.controller.GameController;
import openwrestling.model.gameObjects.TagTeam;
import openwrestling.model.gameObjects.Worker;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.comparators.NameComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateTagTeamDialog {

    private boolean createDialogUpdating = false;

    public Dialog<TagTeam> getDialog(GameController gameController) {
        Dialog<TagTeam> dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        TextField tagTeamName = new TextField();
        List<Worker> workers = gameController.getWorkerManager().selectRoster(gameController.getPromotionManager().getPlayerPromotion());
        Collections.sort(workers, new NameComparator());
        ComboBox<Worker> workerA = new ComboBox(FXCollections.observableArrayList(workers));
        ComboBox<Worker> workerB = new ComboBox(FXCollections.observableArrayList(workers));
        VBox vBox = new VBox(8);

        dialog.setTitle("Create Tag Team");
        dialog.setHeaderText("Team Details");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        workerA.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Worker> observable, Worker oldValue, Worker newValue) -> {
            if (newValue != null && newValue != oldValue && workerB.getItems().contains(newValue)) {
                updateCreateTeamComboBox(newValue, new ArrayList<>(workers), workerB);
            }
        });
        workerB.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Worker> observable, Worker oldValue, Worker newValue) -> {
            if (newValue != null && newValue != oldValue && workerA.getItems().contains(newValue)) {
                updateCreateTeamComboBox(newValue, new ArrayList<>(workers), workerA);
            }
        });
        workerA.getSelectionModel().selectFirst();
        workerB.getSelectionModel().selectFirst();
        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        tagTeamName.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        ViewUtils.addRegionWrapperToVBox(tagTeamName, "Team Name:", vBox);
        ViewUtils.addRegionWrapperToVBox(workerA, "Worker:", vBox);
        ViewUtils.addRegionWrapperToVBox(workerB, "Worker:", vBox);

        dialogPane.setContent(vBox);
        dialogPane.getStylesheets().add("style.css");

        Platform.runLater(tagTeamName::requestFocus);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                TagTeam newTagTeam = gameController.getTagTeamManager().createTagTeam(
                        tagTeamName.getText(),
                        workerA.getSelectionModel().getSelectedItem(),
                        workerB.getSelectionModel().getSelectedItem());
                return newTagTeam;
            }
            return null;
        });
        return dialog;
    }

    private void updateCreateTeamComboBox(Worker worker, List<Worker> workers, ComboBox<Worker> otherComboBox) {
        if (!createDialogUpdating) {
            createDialogUpdating = true;
            List<Worker> currentWorkers = workers;
            currentWorkers.remove(worker);
            Worker selected = otherComboBox.getSelectionModel().getSelectedItem();
            otherComboBox.setItems(FXCollections.observableArrayList(currentWorkers));
            if (otherComboBox.getItems().contains(selected)) {
                otherComboBox.getSelectionModel().select(selected);
            } else {
                otherComboBox.getSelectionModel().selectFirst();
            }
            createDialogUpdating = false;
        }
    }
}
