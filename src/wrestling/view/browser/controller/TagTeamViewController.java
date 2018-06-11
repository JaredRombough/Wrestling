package wrestling.view.browser.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import wrestling.model.Worker;
import wrestling.model.modelView.TagTeamView;
import wrestling.model.segmentEnum.ActiveType;
import wrestling.view.utility.GameScreen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.comparators.NameComparator;
import wrestling.view.utility.interfaces.ControllerBase;

public class TagTeamViewController extends ControllerBase implements Initializable {

    private TagTeamView tagTeamView;

    @FXML
    private AnchorPane imageAnchor1;

    @FXML
    private AnchorPane imageAnchor2;

    @FXML
    private AnchorPane activeTypeAnchorPane;

    @FXML
    private AnchorPane nameAnchor;

    @FXML
    private Label experienceLabel;
    private boolean createDialogUpdating = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof TagTeamView) {
            this.tagTeamView = (TagTeamView) obj;
            updateLabels();
        }
    }

    @Override
    public void updateLabels() {
        if (tagTeamView != null && tagTeamView.getWorkers().size() == 2) {

            ComboBox comboBox = ViewUtils.updatePlayerComboBox(
                    activeTypeAnchorPane,
                    gameController.getContractManager().getFullRoster(playerPromotion()).containsAll(tagTeamView.getWorkers()),
                    Arrays.asList(ActiveType.ACTIVE, ActiveType.INACTIVE),
                    tagTeamView.getTagTeam().getActiveType());
            comboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ActiveType>() {
                @Override
                public void changed(ObservableValue<? extends ActiveType> observable, ActiveType oldValue, ActiveType newValue) {
                    tagTeamView.getTagTeam().setActiveType(newValue);
                }
            });
            nameAnchor.getChildren().clear();
            GameScreen screen = ViewUtils.loadScreenFromResource(ScreenCode.EDIT_LABEL, mainApp, gameController, nameAnchor);

            EditLabel editLabel = (EditLabel) screen.controller;
            editLabel.setCurrent(tagTeamView.getTagTeam().getName());
            editLabel.getEditButton().setOnAction(e -> {
                tagTeamView.getTagTeam().setName(ViewUtils.editTextDialog(tagTeamView.getTagTeam().getName()));
                updateLabels();
                mainApp.updateLabels(ScreenCode.BROWSER);
            });
            editLabel.getCreateButton().setOnAction(e -> {
                Optional<TagTeamView> optionalResult = createTagTeamDialog().showAndWait();
                optionalResult.ifPresent((TagTeamView newTagTeamView) -> {
                    mainApp.show(ScreenCode.BROWSER, newTagTeamView);
                });
            });

            imageAnchor1.getChildren().clear();
            imageAnchor2.getChildren().clear();
            GameScreen card1 = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController, imageAnchor1);
            GameScreen card2 = ViewUtils.loadScreenFromResource(ScreenCode.RESULTS_CARD, mainApp, gameController, imageAnchor2);
            card1.controller.setCurrent(tagTeamView.getWorkers().get(0));
            card2.controller.setCurrent(tagTeamView.getWorkers().get(1));

            experienceLabel.setText(Integer.toString(tagTeamView.getTagTeam().getExperience()));

        }

    }

    private Dialog<TagTeamView> createTagTeamDialog() {
        Dialog<TagTeamView> dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        TextField tagTeamName = new TextField();
        List<Worker> workers = gameController.getContractManager().getFullRoster(playerPromotion());
        Collections.sort(workers, new NameComparator());
        ComboBox<Worker> workerA = new ComboBox(FXCollections.observableArrayList(workers));
        ComboBox<Worker> workerB = new ComboBox(FXCollections.observableArrayList(workers));
        VBox vBox = new VBox(8);

        dialog.setTitle("Create Tag Team");
        dialog.setHeaderText("Team Details");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        workerA.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Worker> observable, Worker oldValue, Worker newValue) -> {
            if (newValue != null && newValue != oldValue && workerB.getItems().contains(newValue)) {
                updateCreateTeamComboBox(newValue, new ArrayList(workers), workerB);
            }
        });
        workerB.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Worker> observable, Worker oldValue, Worker newValue) -> {
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
