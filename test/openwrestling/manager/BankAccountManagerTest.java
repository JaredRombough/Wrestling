package openwrestling.manager;

import openwrestling.TestUtils;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.financial.BankAccount;
import openwrestling.model.gameObjects.financial.Transaction;
import openwrestling.model.segmentEnum.TransactionType;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BankAccountManagerTest {
    @Before
    public void setUp() {
        Database.createNewDatabase("testdb");
    }


    @Test
    public void createBankAccounts() {
        BankAccountManager bankAccountManager = new BankAccountManager();
        PromotionManager promotionManager = new PromotionManager(bankAccountManager);
        Promotion promotion = promotionManager.createPromotions(List.of(TestUtils.randomPromotion())).get(0);
        BankAccount bankAccount = bankAccountManager.getBankAccount(promotion);
        assertThat(bankAccount).isNotNull();
        assertThat(bankAccount.getTransactions()).isEmpty();
        Transaction transaction = Transaction.builder()
                .date(LocalDate.now())
                .type(TransactionType.GATE)
                .amount(123)
                .bankAccount(bankAccount)
                .build();
        bankAccount.setTransactions(List.of(transaction));
        bankAccountManager.updateBankAccounts(List.of(bankAccount));

        List<Transaction> selectedTransactions = Database.selectAll(Transaction.class);
        assertThat(selectedTransactions).hasSize(1);
    }

    @Test
    public void insertTransactions() {
        BankAccountManager bankAccountManager = new BankAccountManager();
        PromotionManager promotionManager = new PromotionManager(bankAccountManager);
        Promotion promotion = promotionManager.createPromotions(List.of(TestUtils.randomPromotion())).get(0);
        BankAccount bankAccount = bankAccountManager.getBankAccount(promotion);
        assertThat(bankAccount).isNotNull();
        bankAccount.setFunds(100);
        bankAccountManager.updateBankAccounts(List.of(bankAccount));

        bankAccount = bankAccountManager.getBankAccount(promotion);
        assertThat(bankAccount.getFunds()).isEqualTo(100);

        Transaction transaction = Transaction.builder()
                .promotion(promotion)
                .amount(100)
                .build();

        bankAccountManager.insertTransactions(List.of(transaction));

        bankAccount = bankAccountManager.getBankAccount(promotion);
        assertThat(bankAccount.getFunds()).isEqualTo(200);

        Transaction transaction2 = Transaction.builder()
                .promotion(promotion)
                .amount(100)
                .build();

        Transaction transaction3 = Transaction.builder()
                .promotion(promotion)
                .amount(-100)
                .build();

        Transaction transaction4 = Transaction.builder()
                .promotion(promotion)
                .amount(50)
                .build();

        bankAccountManager.insertTransactions(List.of(transaction2, transaction3, transaction4));

        bankAccount = bankAccountManager.getBankAccount(promotion);
        assertThat(bankAccount.getFunds()).isEqualTo(250);

    }
}