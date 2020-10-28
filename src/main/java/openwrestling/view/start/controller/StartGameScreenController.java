package openwrestling.view.start.controller;

import javafx.beans.value.ChangeListener;
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
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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

    private Promotion selectedPromotion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @Override
    public void initializeMore() {
        ObservableList<Promotion> promotionsObservableList = FXCollections.observableArrayList();

        for (Promotion current : gameController.getPromotionManager().getPromotions()) {
            if (!current.getName().equals("All Workers")) {
                promotionsObservableList.add(current);
            }
        }

        promotionListView.setItems(promotionsObservableList);

        initializePromotionsListView();
        updateWorkersListView((Promotion) promotionListView.getSelectionModel().getSelectedItem());
    }

    private void initializePromotionsListView() {
        promotionListView.getSelectionModel().selectedItemProperty().addListener((ChangeListener<Promotion>) (observable, oldValue, newValue) -> {
            updateWorkersListView(newValue);
            ViewUtils.showImage(String.format(mainApp.getLogosFolder().toString() + "\\" + newValue.getImageFileName()),
                    promotionImageBorder,
                    imageView);

        });

        promotionListView.getSelectionModel().selectFirst();
    }

    private void updateWorkersListView(Promotion newValue) {
        List<Worker> roster = gameController.getWorkerManager().getRoster(newValue);
        currentPromotionName.setText(newValue.toString().trim());
        currentPromotionText.setText("Level: " + newValue.getLevel() + "\n"
                + "Workers: " + roster.size() + "\n"
                + "Average Popularity: " + gameController.getWorkerManager().averageWorkerPopularity(newValue));

        ObservableList<Worker> rosterList = FXCollections.observableArrayList();
        for (Worker current : roster) {
            rosterList.add(current);
        }
        workersListView.setItems(rosterList);

        selectedPromotion = newValue;

    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {

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
