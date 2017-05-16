package wrestling.view;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import wrestling.MainApp;
import wrestling.model.GameController;
import wrestling.model.Promotion;
import wrestling.model.Worker;

public class StartGameScreenController implements Initializable {

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
    private GridPane gridPane;

    @FXML
    private ImageView imageView;

    private GameController gameController;
    private MainApp mainApp;

    private Promotion selectedPromotion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;

        //now that we have the game controller we can set the promotions to the listview
        ObservableList<Promotion> promotionsObservableList = FXCollections.observableArrayList();

        for (Promotion current : gameController.getPromotions()) {
            //dont' want the player to pick the free agents. probably want a cleaner solution though.
            if (!current.getName().equals("All Workers")) {
                promotionsObservableList.add(current);
            }

        }

        promotionListView.setItems(promotionsObservableList);

        initializeMore();
    }

    public void initializeMore() {
        promotionListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Promotion>() {

            @Override
            public void changed(ObservableValue<? extends Promotion> observable, Promotion oldValue, Promotion newValue) {

                updateWorkersListView(newValue);
                //set the promotion image
                File f = new File(mainApp.getLogosFolder().toString() + "\\" + newValue.getImagePath());
                if (f.exists() && !f.isDirectory()) {
                    Image image = new Image("File:" + mainApp.getLogosFolder().toString() + "\\" + newValue.getImagePath());
                    imageView.setImage(image);
                }

            }
        });

        promotionListView.getSelectionModel().selectFirst();
        updateWorkersListView(
                (Promotion) promotionListView.getSelectionModel().getSelectedItem());
    }

    private void updateWorkersListView(Promotion newValue) {
        currentPromotionName.setText(newValue.toString().trim());
        currentPromotionText.setText("Level: " + newValue.getLevel() + "\n"
                + "Workers: " + newValue.getFullRoster().size() + "\n"
                + "Average Popularity: " + newValue.averageWorkerPopularity());

        ObservableList<Worker> rosterList = FXCollections.observableArrayList();
        for (Worker current : newValue.getFullRoster()) {
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
