package openwrestling.view.financial.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import openwrestling.model.segmentEnum.StaffType;
import openwrestling.model.segmentEnum.TransactionType;
import openwrestling.model.utility.ContractUtils;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import openwrestling.view.utility.interfaces.ControllerBase;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
    private AnchorPane refereeBase;

    @FXML
    private AnchorPane broadcastBase;

    @FXML
    private AnchorPane ownerBase;

    @FXML
    private GridPane balanceSheetGrid;

    private List<GameScreen> departmentScreens;

    private final int GATE_ROW = 2;
    private final int WORKER_EXPENSE_ROW = 4;
    private final int STAFF_EXPENSE_ROW = 5;
    private final int TOTAL_ROW = 6;

    private int sheetCell(TransactionType type, int monthsAgo) {

        LocalDate startDate = gameController.getDateManager().today().minusMonths(monthsAgo).withDayOfMonth(1);

        return gameController.getBankAccountManager().getTransactionTotal(playerPromotion(), type, startDate);
    }

    @Override
    public void updateLabels() {
        balanceSheetGrid.getChildren().removeAll(sheetLabels);
        for (int i = 0; i < 3; i++) {
            addSheetLabel(i, sheetCell(TransactionType.GATE, i), GATE_ROW);
            addSheetLabel(i, sheetCell(TransactionType.WORKER, i), WORKER_EXPENSE_ROW);
            addSheetLabel(i, sheetCell(TransactionType.STAFF, i), STAFF_EXPENSE_ROW);
            addSheetLabel(i, totalText(i), TOTAL_ROW);
        }

        addSheetLabel(-1,
                ContractUtils.getWorkerPayrollForMonth(gameController.getDateManager().today().plusMonths(1), playerPromotion(), gameController.getWorkerManager().selectRoster(playerPromotion())),
                WORKER_EXPENSE_ROW);

        addSheetLabel(-1,
                gameController.getStaffManager().getStaffPayrollForMonth(gameController.getDateManager().today().plusMonths(1), playerPromotion()),
                STAFF_EXPENSE_ROW);

        for (GameScreen screen : departmentScreens) {
            screen.controller.updateLabels();
        }
    }

    private void addSheetLabel(int monthsAgo, int amount, int row) {
        Label label = new Label();
        label.setText(String.format("$%,d", amount));
        if (monthsAgo != 0) {
            label.getStyleClass().add("grey-text");
        }
        sheetLabels.add(label);
        GridPane.setHalignment(label, HPos.RIGHT);
        balanceSheetGrid.add(label, 3 - monthsAgo, row);

    }

    private int totalText(int monthsAgo) {
        return gameController.getBankAccountManager().getMonthlyNet(playerPromotion(),
                gameController.getDateManager().today().minusMonths(monthsAgo));
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

        GameScreen trainers = ViewUtils.loadScreenFromResource(ScreenCode.RINGSIDE, mainApp, gameController, trainerBase);
        trainers.controller.setCurrent(StaffType.TRAINER);
        departmentScreens.add(trainers);

        GameScreen production = ViewUtils.loadScreenFromResource(ScreenCode.DEPARTMENT, mainApp, gameController, productionBase);
        production.controller.setCurrent(StaffType.PRODUCTION);
        departmentScreens.add(production);

        GameScreen referee = ViewUtils.loadScreenFromResource(ScreenCode.RINGSIDE, mainApp, gameController, refereeBase);
        referee.controller.setCurrent(StaffType.REFEREE);
        departmentScreens.add(referee);

        GameScreen broadcast = ViewUtils.loadScreenFromResource(ScreenCode.RINGSIDE, mainApp, gameController, broadcastBase);
        broadcast.controller.setCurrent(StaffType.BROADCAST);
        departmentScreens.add(broadcast);

        GameScreen owner = ViewUtils.loadScreenFromResource(ScreenCode.RINGSIDE, mainApp, gameController, ownerBase);
        owner.controller.setCurrent(StaffType.OWNER);
        departmentScreens.add(owner);

    }

}
