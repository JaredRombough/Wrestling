package wrestling.model.utility;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import wrestling.model.Contract;
import wrestling.model.SegmentItem;
import wrestling.model.StaffContract;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.WorkerView;

public final class ContractUtils {

    public static int calculateWorkerContractCost(WorkerView worker, boolean exclusive) {
        int unitCost;

        List<Integer> pricePoints = new ArrayList<>();

        pricePoints.addAll(Arrays.asList(0, 15, 30, 50, 75, 100, 250, 500, 1000, 10000, 100000));

        double nearest10 = worker.getPopularity() / 10 * 10;
        double multiplier = (worker.getPopularity() - nearest10) / 10;

        int ppIndex = (int) nearest10 / 10;

        unitCost = pricePoints.get(ppIndex);

        if (nearest10 != 100) {
            double extra = (pricePoints.get(ppIndex + 1) - unitCost) * multiplier;
            unitCost += (int) extra;
        }

        if (exclusive) {
            unitCost *= 2;
        }

        return roundMoney(unitCost);
    }

    public static int calculateStaffContractCost(StaffView staff) {

        int unitCost;

        List<Integer> pricePoints = new ArrayList<>();

        pricePoints.addAll(Arrays.asList(50, 50, 100, 150, 150, 300, 600, 1000, 2000, 10000, 20000));

        double nearest10 = staff.getSkill() / 10 * 10;
        double multiplier = (staff.getSkill() - nearest10) / 10;

        int ppIndex = (int) nearest10 / 10;

        unitCost = pricePoints.get(ppIndex);

        if (nearest10 != 100) {
            double extra = (pricePoints.get(ppIndex + 1) - unitCost) * multiplier;
            unitCost += (int) extra;
        }

        return roundMoney(unitCost);
    }

    public static int roundMoney(int unitCost) {
        if (unitCost > 100000) {
            unitCost = unitCost / 10000 * 10000;
        } else if (unitCost > 10000) {
            unitCost = unitCost / 1000 * 1000;
        } else if (unitCost > 1000) {
            unitCost = unitCost / 100 * 100;
        } else {
            unitCost = unitCost / 10 * 10;
        }
        return unitCost;
    }

    public static int calculateSigningFee(SegmentItem segmentItem, LocalDate startDate) {
        int monthlyCost = 0;
        if (segmentItem instanceof StaffView) {
            monthlyCost = calculateStaffContractCost((StaffView) segmentItem);
        } else if (segmentItem instanceof WorkerView) {
            monthlyCost = calculateWorkerContractCost((WorkerView) segmentItem, true);
        }

        int fee = monthlyCost * (startDate.lengthOfMonth() - startDate.getDayOfMonth()) / startDate.lengthOfMonth();

        return roundMoney(fee);
    }

    public static LocalDate contractEndDate(LocalDate startDate, int months) {
        return startDate.plusMonths(months + 1).withDayOfMonth(1);
    }

    public static int getWorkerPayrollForMonth(LocalDate date, PromotionView promotion) {
        int total = 0;

        for (WorkerView worker : promotion.getFullRoster()) {
            Contract contract = worker.getContract(promotion);
            if (contract != null && contract.getEndDate().isAfter(date.withDayOfMonth(1))) {
                total += contract.getMonthlyCost();
            }
        }
        return total;
    }

    public static int getStaffPayrollForMonth(LocalDate date, PromotionView promotion) {
        int total = 0;

        for (StaffView staff : promotion.getAllStaff()) {
            StaffContract contract = staff.getStaffContract();
            if (contract != null && contract.getEndDate().isAfter(date.withDayOfMonth(1))) {
                total += contract.getMonthlyCost();
            }
        }
        return total;
    }
}
