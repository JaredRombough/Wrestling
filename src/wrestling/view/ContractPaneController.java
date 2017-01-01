package wrestling.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import wrestling.MainApp;
import wrestling.model.GameController;
import wrestling.model.Worker;

public class ContractPaneController implements Initializable {

    private MainApp mainApp;
    private GameController gameController;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;

        initializeMore();
    }

    @FXML
    private ComboBox typeComboBox;

    @FXML
    private ComboBox lengthComboBox;

    @FXML
    private ComboBox termsComboBox;

    @FXML
    private Label costLabel;

    @FXML
    private Button signButton;

    private boolean exclusive;

    private boolean monthly;

    private int cost;

    private Worker worker;

    public void setWorker(Worker worker) {
        this.worker = worker;

        if (this.worker.canNegotiate(gameController.playerPromotion())) {
            setDisable(false);
        } else {
            setDisable(true);
        }
    }

    //disables the pane when negotiation is impossible
    private void setDisable(boolean disable) {

        signButton.setDisable(disable);
        lengthComboBox.setDisable(disable);
        termsComboBox.setDisable(disable);
        typeComboBox.setDisable(disable);

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    private void initializeMore() {

        List<String> exclusiveOpen = new ArrayList<>(Arrays.asList("Exclusive", "Open"));
        List<String> terms = new ArrayList<>(Arrays.asList("Monthly", "Appearance"));

        typeComboBox.getItems().addAll(exclusiveOpen);
        typeComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals("Exclusive")) {
                    exclusive = true;
                } else {
                    exclusive = false;
                }

                updateLabels();

            }
        });
        typeComboBox.getSelectionModel().selectFirst();

        termsComboBox.getItems().addAll(terms);
        termsComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals("Monthly")) {
                    monthly = true;

                } else {
                    monthly = false;

                }

                updateLabels();

            }
        });
        termsComboBox.getSelectionModel().selectFirst();

        lengthComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                calculateCost();
            }
        });

        updateLengthComboBox();

    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {

        if (event.getSource().equals(signButton)) {

            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Sign Contract");
            alert.setHeaderText("Terms go here");
            alert.setContentText("Sign this contract?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                //sign the contract and disable the pane so they can't sign another
                signContract();
                setDisable(true);
            } else {
                //do nothing
            }

            updateLabels();
        }
    }

    private void signContract() {

        gameController.contractFactory.createContract(worker, gameController.playerPromotion(), monthly, exclusive, cost, cost);

    }

    public void updateLabels() {

        updateLengthComboBox();
        calculateCost();

    }

    private void updateCostLabel() {
        if (worker != null && worker.canNegotiate(gameController.playerPromotion())) {
            costLabel.setText("" + cost);
        } else {
            costLabel.setText("Under Contract");
        }

    }

    private void calculateCost() {

        cost = 0;

        if (worker != null) {

            cost = worker.getPopularity() * 10;

            if (exclusive) {
                cost *= 1.5;
            }

            cost *= (lengthComboBox.getSelectionModel().getSelectedIndex() + 1);
        }

        updateCostLabel();
    }

    private int duration() {
        return lengthComboBox.getSelectionModel().getSelectedIndex() + 1;
    }

    private void updateLengthComboBox() {

        int appearanceMax = 10;
        int monthlyMax = 36;
        lengthComboBox.getItems().clear();

        List<String> appearanceList = new ArrayList<>();

        for (Integer i = 1; i <= appearanceMax; i++) {
            appearanceList.add(i.toString());
        }

        lengthComboBox.getItems().addAll(appearanceList);

        lengthComboBox.getSelectionModel().selectFirst();
    }
}
