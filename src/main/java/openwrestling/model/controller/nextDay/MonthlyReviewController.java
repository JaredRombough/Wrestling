package openwrestling.model.controller.nextDay;

import lombok.Builder;
import openwrestling.Logging;
import openwrestling.manager.BankAccountManager;
import openwrestling.manager.MonthlyReviewManager;
import openwrestling.model.gameObjects.MonthlyReview;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.financial.BankAccount;

import java.time.LocalDate;


@Builder
public class MonthlyReviewController extends Logging {

    private MonthlyReviewManager monthlyReviewManager;
    private BankAccountManager bankAccountManager;


    public void doStuff(Promotion promotion, LocalDate date) {

        BankAccount bankAccount = bankAccountManager.getBankAccount(promotion);

        MonthlyReview monthlyReview = MonthlyReview.builder()
                .funds(bankAccount.getFunds())
                .popularity(promotion.getPopularity())
                .level(promotion.getLevel())
                .date(date)
                .build();

        monthlyReviewManager.createMonthlyReview(monthlyReview);
    }


}
