package wrestling.view;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import wrestling.MainApp;
import wrestling.model.GameController;

public class FinancialScreenController extends Controller implements Initializable {

    private List<List<Label>> sheetLabels;

    @FXML
    private Label gate1;

    @FXML
    private Label gate2;

    @FXML
    private Label gate3;

    private List<Label> gateLabels;

    @FXML
    private Label workerExpense1;

    @FXML
    private Label workerExpense2;

    @FXML
    private Label workerExpense3;

    private List<Label> workerExpenseLabels;

    private MainApp mainApp;
    private GameController gameController;

    @Override
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
        initializeMore();
    }

    @Override
    void initializeMore() {

    }

    @Override
    void setCurrent(Object obj) {

    }

    private String sheetCell(char type, int monthsAgo) {

        LocalDate startDate = gameController.date().minusMonths(monthsAgo).withDayOfMonth(1);

        LocalDate endDate = gameController.date();

        if (monthsAgo == 0) {
            endDate = gameController.date();
        } else {
            endDate = gameController.date().withDayOfMonth(monthsAgo).minusDays(1);
        }

        int amount = gameController.playerPromotion().getTransactionTotal(
                type,
                startDate,
                endDate);

        return "$" + amount;
    }

    @Override
    public void updateLabels() {
        for (int i = 0; i < gateLabels.size(); i++) {
            gateLabels.get(i).setText(sheetCell('e', i));
            workerExpenseLabels.get(i).setText(sheetCell('w', i));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gateLabels = new ArrayList<>();
        gateLabels.addAll(Arrays.asList(gate1, gate2, gate3));

        workerExpenseLabels = new ArrayList<>();
        workerExpenseLabels.addAll(Arrays.asList(workerExpense1, workerExpense2, workerExpense3));

    }

}
