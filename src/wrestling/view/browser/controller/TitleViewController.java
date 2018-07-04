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
import wrestling.model.modelView.TitleView;
import wrestling.model.segmentEnum.ActiveType;
import wrestling.view.utility.GameScreen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.comparators.NameComparator;
import wrestling.view.utility.interfaces.ControllerBase;

public class TitleViewController extends ControllerBase implements Initializable {

    private TitleView titleView;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private AnchorPane activeTypeAnchorPane;

    @FXML
    private AnchorPane nameAnchor;

    @FXML
    private Label prestigeLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void setCurrent(Object obj) {
        if (obj instanceof TitleView) {
            this.titleView = (TitleView) obj;
        } else {
            this.titleView = null;
        }
        anchorPane.setVisible(this.titleView != null);
        updateLabels();
    }

    @Override
    public void updateLabels() {
        if (titleView != null) {

            ComboBox comboBox = ViewUtils.updatePlayerComboBox(
                    activeTypeAnchorPane,
                    playerPromotion().equals(titleView.getTitle().getPromotion()),
                    Arrays.asList(ActiveType.ACTIVE, ActiveType.INACTIVE),
                    titleView.getActiveType());
            comboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ActiveType>() {
                @Override
                public void changed(ObservableValue<? extends ActiveType> observable, ActiveType oldValue, ActiveType newValue) {
                    titleView.getTitle().setActiveType(newValue);
                }
            });
            nameAnchor.getChildren().clear();
            GameScreen screen = ViewUtils.loadScreenFromResource(ScreenCode.EDIT_LABEL, mainApp, gameController, nameAnchor);

            EditLabel editLabel = (EditLabel) screen.controller;
            editLabel.setCurrent(titleView.getTitle().getName());
            editLabel.getEditButton().setOnAction(e -> {
                titleView.getTitle().setName(ViewUtils.editTextDialog(titleView.getTitle().getName()));
                updateLabels();
                mainApp.updateLabels(ScreenCode.BROWSER);
            });
            editLabel.getCreateButton().setOnAction(e -> {
                Optional<TitleView> optionalResult = createTagTeamDialog().showAndWait();
                optionalResult.ifPresent((TitleView newTitleView) -> {
                    mainApp.show(ScreenCode.BROWSER, newTitleView);
                });
            });

            prestigeLabel.setText("prestige here");

        }

    }

    private Dialog<TitleView> createTagTeamDialog() {
        Dialog<TitleView> dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        TextField titleName = new TextField();
        VBox vBox = new VBox(8);

        dialog.setTitle("Create Title");
        dialog.setHeaderText("Title Details");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        titleName.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        ViewUtils.addRegionWrapperToVBox(titleName, "Title Name:", vBox);

        dialogPane.setContent(vBox);
        dialogPane.getStylesheets().add("style.css");

        Platform.runLater(titleName::requestFocus);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                TitleView newTitleView = gameController.getTitleFactory().createTitle(
                        playerPromotion(), titleName.getText());
                return newTitleView;
            }
            return null;
        });
        return dialog;
    }

}
