package openwrestling.view.start.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import openwrestling.MainApp;
import openwrestling.model.controller.GameController;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.interfaces.iPerson;
import openwrestling.model.utility.ContractUtils;
import openwrestling.view.utility.ViewUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SelectPlayerPromotionDialog {
    public void createDialog(GameController gameController, MainApp mainApp) {
        Dialog<Promotion> dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        List<Promotion> promotions = gameController.getPromotionManager().getPromotions();

        Label level = new Label();
        Label rosterSize = new Label();
        Label averageWorkerPopularity = new Label();

        ComboBox<Promotion> promotionComboBox = new ComboBox();
        promotionComboBox.setOnMousePressed(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                promotionComboBox.requestFocus();
            }
        });


        promotionComboBox.setItems(FXCollections.observableArrayList(promotions));

        ListView<Worker> workerListView = new ListView<>();


        promotionComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Promotion>() {
            @Override
            public void changed(ObservableValue<? extends Promotion> observable, Promotion oldValue, Promotion newValue) {
                List<Worker> workers = gameController.getWorkerManager().selectRoster(newValue);
                workerListView.setItems(FXCollections.observableArrayList(workers));
            }
        });

     //   promotionComboBox.setOnMouseClicked(e -> System.out.println("click"));

        promotionComboBox.getSelectionModel().selectFirst();


        VBox vBox = new VBox(8);

        dialog.setTitle(String.format("Sign Contract"));
        dialog.setHeaderText("Terms");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        //   endDate.setText(ContractUtils.contractEndDate(gameController.getDateManager().today(), lengthComboBox.getSelectionModel().getSelectedIndex() + 1).toString());

        ViewUtils.addRegionWrapperToVBox(promotionComboBox, "Select promotion:", vBox);
        ViewUtils.addRegionWrapperToVBox(workerListView, "Roster:", vBox);

        dialogPane.setContent(vBox);

        dialogPane.getStylesheets().add("style.css");


        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                gameController.getPromotionManager().setPlayerPromotion(promotionComboBox.getValue());
            mainApp.startGame();
                return null;
            }
            return null;
        });
        //promotionComboBox.setDisable(false);
        dialog.show();
    }


}
