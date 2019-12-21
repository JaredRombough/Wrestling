package openwrestling.model.utility;

import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.interfaces.iContract;
import openwrestling.model.interfaces.iPerson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;

public final class ContractUtils {

    public static boolean isMoraleCheckDay(iContract contract, LocalDate today) {
        return DAYS.between(contract.getStartDate(), today) % 30 == 0;
    }

    public static int calculateWorkerContractCost(Worker worker, boolean exclusive) {
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

    public static int calculateStaffContractCost(StaffMember staff) {

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

    public static int calculateSigningFee(iPerson person, LocalDate startDate) {
        int monthlyCost = 0;
        if (person instanceof StaffMember) {
            monthlyCost = calculateStaffContractCost((StaffMember) person);
        } else if (person instanceof Worker) {
            monthlyCost = calculateWorkerContractCost((Worker) person, true);
        }

        int fee = monthlyCost * (startDate.lengthOfMonth() - startDate.getDayOfMonth()) / startDate.lengthOfMonth();

        return roundMoney(fee);
    }

    public static int calculateTerminationFee(iContract contract, LocalDate terminationDate) {
        if (!contract.isExclusive()) {
            return 0;
        }
        return Math.round(MONTHS.between(terminationDate, contract.getEndDate()) * contract.getMonthlyCost() / 2);
    }

    public static LocalDate contractEndDate(LocalDate startDate, int months) {
        return startDate.plusMonths(months + 1).withDayOfMonth(1).minusDays(1);
    }

    public static int getWorkerPayrollForMonth(LocalDate date, Promotion promotion, List<Worker> roster) {
        int total = 0;
        for (Worker worker : roster) {
            iContract contract = worker.getContract(promotion);
            if (contract != null && contract.getEndDate().isAfter(date.withDayOfMonth(1))) {
                total += contract.getMonthlyCost();
            }
        }
        return total;
    }


    public static String getTermsString(Worker worker, Promotion promotion) {
        iContract contract = worker.getContract(promotion);
        if (contract.isExclusive()) {
            return String.format("$%d monthly", contract.getMonthlyCost());
        }
        return String.format("$%d / appearance", contract.getAppearanceCost());
    }

    public static String contractDurationString(iContract contract, LocalDate today) {
        return String.format("%d day%s",
                DAYS.between(today, contract.getEndDate()),
                DAYS.between(today, contract.getEndDate()) > 1 ? "s" : "");
    }
}
