package wrestling.view;

import java.io.File;
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
import wrestling.model.Promotion;
import wrestling.model.Worker;
import wrestling.view.utility.ViewUtilityFunctions;

public class StartGameScreenController extends Controller implements Initializable {

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
        //now that we have the game controller we can set the promotions to the listview
        ObservableList<Promotion> promotionsObservableList = FXCollections.observableArrayList();

        for (Promotion current : gameController.getPromotions()) {
            //dont' want the player to pick the free agents. probably want a cleaner solution though.
            if (!current.getName().equals("All Workers")) {
                promotionsObservableList.add(current);
            }
        }

        promotionListView.setItems(promotionsObservableList);

        initializePromotionsListView();
        updateWorkersListView(
                (Promotion) promotionListView.getSelectionModel().getSelectedItem());
    }

    private void initializePromotionsListView() {
        promotionListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Promotion>() {

            @Override
            public void changed(ObservableValue<? extends Promotion> observable, Promotion oldValue, Promotion newValue) {

                updateWorkersListView(newValue);
                ViewUtilityFunctions.showImage(new File(mainApp.getLogosFolder().toString() + "\\" + newValue.getImagePath()),
                        promotionImageBorder,
                        imageView);

            }
        });

        promotionListView.getSelectionModel().selectFirst();
    }

    private void updateWorkersListView(Promotion newValue) {
        currentPromotionName.setText(newValue.toString().trim());
        currentPromotionText.setText("Level: " + newValue.getLevel() + "\n"
                + "Workers: " + gameController.getFullRoster(newValue).size() + "\n"
                + "Average Popularity: " + gameController.averageWorkerPopularity(newValue));

        ObservableList<Worker> rosterList = FXCollections.observableArrayList();
        for (Worker current : gameController.getFullRoster(newValue)) {
            rosterList.add(current);
        }
        workersListView.setItems(rosterList);

        selectedPromotion = newValue;

    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException, ClassNotFoundException {

        if (event.getSource().equals(startGameButton)) {
            gameController.setPlayerPromotion(selectedPromotion);
            mainApp.startGame();

        }

    }

}
