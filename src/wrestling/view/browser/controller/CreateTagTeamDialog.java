package wrestling.view.browser.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import wrestling.model.modelView.WorkerView;
import wrestling.model.controller.GameController;
import wrestling.model.modelView.TagTeamView;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.comparators.NameComparator;

public class CreateTagTeamDialog {

    private boolean createDialogUpdating = false;

    public Dialog<TagTeamView> getDialog(GameController gameController) {
        Dialog<TagTeamView> dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        TextField tagTeamName = new TextField();
        List<WorkerView> workers = gameController.getContractManager().getFullRoster(
                gameController.getPromotionManager().playerPromotion());
        Collections.sort(workers, new NameComparator());
        ComboBox<WorkerView> workerA = new ComboBox(FXCollections.observableArrayList(workers));
        ComboBox<WorkerView> workerB = new ComboBox(FXCollections.observableArrayList(workers));
        VBox vBox = new VBox(8);

        dialog.setTitle("Create Tag Team");
        dialog.setHeaderText("Team Details");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        workerA.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends WorkerView> observable, WorkerView oldValue, WorkerView newValue) -> {
            if (newValue != null && newValue != oldValue && workerB.getItems().contains(newValue)) {
                updateCreateTeamComboBox(newValue, new ArrayList(workers), workerB);
            }
        });
        workerB.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends WorkerView> observable, WorkerView oldValue, WorkerView newValue) -> {
            if (newValue != null && newValue != oldValue && workerA.getItems().contains(newValue)) {
                updateCreateTeamComboBox(newValue, new ArrayList(workers), workerA);
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
                TagTeamView newTagTeamView = gameController.getTagTeamManager().createTagTeam(
                        tagTeamName.getText(),
                        workerA.getSelectionModel().getSelectedItem(),
                        workerB.getSelectionModel().getSelectedItem());
                return newTagTeamView;
            }
            return null;
        });
        return dialog;
    }

    private void updateCreateTeamComboBox(WorkerView worker, List<WorkerView> workers, ComboBox<WorkerView> otherComboBox) {
        if (!createDialogUpdating) {
            createDialogUpdating = true;
            List<WorkerView> currentWorkers = workers;
            currentWorkers.remove(worker);
            WorkerView selected = otherComboBox.getSelectionModel().getSelectedItem();
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
