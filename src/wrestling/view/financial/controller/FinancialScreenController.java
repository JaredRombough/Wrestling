package wrestling.view.financial.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import wrestling.model.segmentEnum.StaffType;
import wrestling.model.segmentEnum.TransactionType;
import wrestling.view.utility.GameScreen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;
import wrestling.view.utility.interfaces.ControllerBase;

public class FinancialScreenController extends ControllerBase implements Initializable {

    private List<Label> sheetLabels;

    @FXML
    private AnchorPane medicalBase;

    @FXML
    private AnchorPane creativeBase;

    @FXML
    private AnchorPane roadAgentBase;

    @FXML
    private AnchorPane trainerBase;

    @FXML
    private AnchorPane productionBase;

    @FXML
    private GridPane balanceSheetGrid;

    private List<GameScreen> departmentScreens;

    private String sheetCell(TransactionType type, int monthsAgo) {

        LocalDate startDate = gameController.getDateManager().today().minusMonths(monthsAgo).withDayOfMonth(1);

        int amount = gameController.getPromotionManager().getBankAccount(playerPromotion())
                .getTransactionTotal(
                        type,
                        startDate);

        return "$" + amount;
    }

    @Override
    public void updateLabels() {
        balanceSheetGrid.getChildren().removeAll(sheetLabels);
        for (int i = 0; i < 3; i++) {
            addSheetLabel(i, TransactionType.GATE, 2);
            addSheetLabel(i, TransactionType.WORKER, 4);
            addSheetLabel(i, TransactionType.STAFF, 5);
        }

        for (GameScreen screen : departmentScreens) {
            screen.controller.updateLabels();
        }
    }

    private void addSheetLabel(int monthsAgo, TransactionType type, int row) {
        Label label = new Label();
        label.setText(sheetCell(type, monthsAgo));
        sheetLabels.add(label);
        balanceSheetGrid.add(label, 3 - monthsAgo, row);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sheetLabels = new ArrayList<>();
        departmentScreens = new ArrayList<>();
    }

    @Override
    public void initializeMore() {
        GameScreen medical = ViewUtils.loadScreenFromResource(ScreenCode.DEPARTMENT, mainApp, gameController, medicalBase);
        medical.controller.setCurrent(StaffType.MEDICAL);
        departmentScreens.add(medical);

        GameScreen roadAgents = ViewUtils.loadScreenFromResource(ScreenCode.DEPARTMENT, mainApp, gameController, roadAgentBase);
        roadAgents.controller.setCurrent(StaffType.ROAD_AGENT);
        departmentScreens.add(roadAgents);

        GameScreen creative = ViewUtils.loadScreenFromResource(ScreenCode.DEPARTMENT, mainApp, gameController, creativeBase);
        creative.controller.setCurrent(StaffType.CREATIVE);
        departmentScreens.add(creative);

        GameScreen trainers = ViewUtils.loadScreenFromResource(ScreenCode.DEPARTMENT, mainApp, gameController, trainerBase);
        trainers.controller.setCurrent(StaffType.TRAINER);
        departmentScreens.add(trainers);

        GameScreen production = ViewUtils.loadScreenFromResource(ScreenCode.DEPARTMENT, mainApp, gameController, productionBase);
        production.controller.setCurrent(StaffType.PRODUCTION);
        departmentScreens.add(production);

    }

}
