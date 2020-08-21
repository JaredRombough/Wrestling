package openwrestling.view.financial.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.segment.constants.StaffType;
import openwrestling.model.segment.constants.TransactionType;
import openwrestling.model.utility.ContractUtils;
import openwrestling.view.browser.controller.EditBroadcastTeamDialog;
import openwrestling.view.utility.interfaces.ControllerBase;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class FinancialScreenController extends ControllerBase implements Initializable {

    private final int GATE_ROW = 2;
    private final int WORKER_MONTHLY_EXPENSE_ROW = 4;
    private final int WORKER_APPEARANCE_EXPENSE_ROW = 5;
    private final int STAFF_EXPENSE_ROW = 6;
    private final int TOTAL_ROW = 7;
    private List<Label> sheetLabels;

    @FXML
    private Button editDefaultBroadcastTeamButton;
    @FXML
    private GridPane balanceSheetGrid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sheetLabels = new ArrayList<>();
    }

    @Override
    public void updateLabels() {
        balanceSheetGrid.getChildren().removeAll(sheetLabels);
        for (int i = 0; i < 3; i++) {
            addSheetLabel(i, sheetCell(TransactionType.GATE, i), GATE_ROW);
            addSheetLabel(i, sheetCell(TransactionType.WORKER_MONTHLY, i), WORKER_MONTHLY_EXPENSE_ROW);
            addSheetLabel(i, sheetCell(TransactionType.WORKER_APPEARANCE, i), WORKER_APPEARANCE_EXPENSE_ROW);
            addSheetLabel(i, sheetCell(TransactionType.STAFF, i), STAFF_EXPENSE_ROW);
            addSheetLabel(i, totalText(i), TOTAL_ROW);
        }

        addSheetLabel(-1,
                ContractUtils.getWorkerPayrollForMonth(gameController.getDateManager().today().plusMonths(1), playerPromotion(), gameController.getWorkerManager().getRoster(playerPromotion())),
                WORKER_MONTHLY_EXPENSE_ROW);

        addSheetLabel(-1,
                gameController.getStaffManager().getStaffPayrollForMonth(gameController.getDateManager().today().plusMonths(1), playerPromotion()),
                STAFF_EXPENSE_ROW);

        editDefaultBroadcastTeamButton.setOnAction(e -> {
            EditBroadcastTeamDialog dialog = new EditBroadcastTeamDialog();
            Optional<List<StaffMember>> optionalResult = dialog.getDialog(
                    gameController.getStaffManager().getStaff(StaffType.BROADCAST, playerPromotion()),
                    gameController.getBroadcastTeamManager().getDefaultBroadcastTeam(playerPromotion()),
                    playerPromotion().getLongName()
            ).showAndWait();
            optionalResult.ifPresent((List<StaffMember> broadcastTeam) -> {
                gameController.getBroadcastTeamManager().setDefaultBroadcastTeam(playerPromotion(), broadcastTeam);
                updateLabels();
            });
        });
    }

    private int sheetCell(TransactionType type, int monthsAgo) {
        LocalDate startDate = gameController.getDateManager().today().minusMonths(monthsAgo).withDayOfMonth(1);

        return gameController.getBankAccountManager().getTransactionTotal(playerPromotion(), type, startDate);
    }


    private void addSheetLabel(int monthsAgo, int amount, int row) {
        int columnOffset = 4;
        Label label = new Label();
        label.setText(String.format("$%,d", amount));
        if (monthsAgo != 0) {
            label.getStyleClass().add("grey-text");
        }
        sheetLabels.add(label);
        GridPane.setHalignment(label, HPos.RIGHT);
        balanceSheetGrid.add(label, columnOffset - monthsAgo, row);

    }

    private int totalText(int monthsAgo) {
        return gameController.getBankAccountManager().getMonthlyNet(playerPromotion(),
                gameController.getDateManager().today().minusMonths(monthsAgo));
    }

}
