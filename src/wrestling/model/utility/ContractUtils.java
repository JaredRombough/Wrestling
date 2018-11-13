package wrestling.model.utility;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import wrestling.model.SegmentItem;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.WorkerView;

public final class ContractUtils {

    public static int calculateWorkerContractCost(WorkerView worker, boolean exclusive) {
        int unitCost;

        List<Integer> pricePoints = new ArrayList<>();

        pricePoints.addAll(Arrays.asList(0, 10, 20, 50, 75, 100, 250, 500, 1000, 10000, 100000));

        double nearest10 = worker.getPopularity() / 10 * 10;
        double multiplier = (worker.getPopularity() - nearest10) / 10;

        int ppIndex = (int) nearest10 / 10;

        unitCost = pricePoints.get(ppIndex);

        if (nearest10 != 100) {
            double extra = (pricePoints.get(ppIndex + 1) - unitCost) * multiplier;
            unitCost += (int) extra;
        }

        if (exclusive) {
            unitCost *= 1.5;
        }

        return unitCost;
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

        return unitCost;
    }

    public static int calculateSigningFee(SegmentItem segmentItem, LocalDate startDate) {
        int monthlyCost = 0;
        if (segmentItem instanceof StaffView) {
            monthlyCost = calculateStaffContractCost((StaffView) segmentItem);
        } else if (segmentItem instanceof WorkerView) {
            monthlyCost = calculateWorkerContractCost((WorkerView) segmentItem, true);
        }

        return monthlyCost * (startDate.lengthOfMonth() - startDate.getDayOfMonth()) / startDate.lengthOfMonth();
    }

    public static LocalDate contractEndDate(LocalDate startDate, int months) {
        return startDate.plusMonths(months + 1).withDayOfMonth(1);
    }
}
