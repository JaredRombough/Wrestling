package wrestling.view.start.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.WorkerView;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class StartGameScreenController extends ControllerBase implements Initializable {

    @FXML
    private ListView promotionListView;

    @FXML
    private ListView workersListView;

    @FXML
    private Text currentPromotionText;

    @FXML
    private Text currentPromotionName;

    @FXML
    private Button startGameButton;

    @FXML
    private StackPane promotionImageBorder;

    @FXML
    private ImageView imageView;

    private PromotionView selectedPromotion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @Override
    public void initializeMore() {
        //now that we have the game controller we can set the promotions to the listview
        ObservableList<PromotionView> promotionsObservableList = FXCollections.observableArrayList();

        for (PromotionView current : gameController.getPromotionManager().getPromotions()) {
            //dont' want the player to pick the free agents. probably want a cleaner solution though.
            if (!current.getName().equals("All Workers")) {
                promotionsObservableList.add(current);
            }
        }

        promotionListView.setItems(promotionsObservableList);

        initializePromotionsListView();
        updateWorkersListView((PromotionView) promotionListView.getSelectionModel().getSelectedItem());
    }

    private void initializePromotionsListView() {
        promotionListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PromotionView>() {

            @Override
            public void changed(ObservableValue<? extends PromotionView> observable, PromotionView oldValue, PromotionView newValue) {
                updateWorkersListView(newValue);
                ViewUtils.showImage(String.format(mainApp.getLogosFolder().toString() + "\\" + newValue.getImagePath()),
                        promotionImageBorder,
                        imageView);

            }
        });

        promotionListView.getSelectionModel().selectFirst();
    }

    private void updateWorkersListView(PromotionView newValue) {
        currentPromotionName.setText(newValue.toString().trim());
        currentPromotionText.setText("Level: " + newValue.getLevel() + "\n"
                + "Workers: " + newValue.getFullRoster().size() + "\n"
                + "Average Popularity: " + gameController.getContractManager().averageWorkerPopularity(newValue));

        ObservableList<WorkerView> rosterList = FXCollections.observableArrayList();
        for (WorkerView current : newValue.getFullRoster()) {
            rosterList.add(current);
        }
        workersListView.setItems(rosterList);

        selectedPromotion = newValue;

    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException, ClassNotFoundException {

        if (event.getSource().equals(startGameButton)) {
            setControlsDisable(true);
            gameController.getPromotionManager().setPlayerPromotion(selectedPromotion);
            mainApp.startGame();

        }

    }

    private void setControlsDisable(boolean disable) {
        startGameButton.setDisable(disable);
        promotionListView.setDisable(disable);
        workersListView.setDisable(disable);
    }

}
