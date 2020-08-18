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

import static openwrestling.TestUtils.TEST_DB_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class BankAccountManagerTest {

    private Database database;

    @Before
    public void setUp() {
        database = new Database(TEST_DB_PATH);
    }


    @Test
    public void createBankAccounts() {
        BankAccountManager bankAccountManager = new BankAccountManager(database);
        PromotionManager promotionManager = new PromotionManager(database, bankAccountManager, mock(GameSettingManager.class));
        Promotion promotion = promotionManager.createPromotions(List.of(TestUtils.randomPromotion())).get(0);
        BankAccount bankAccount = bankAccountManager.getBankAccount(promotion);
        assertThat(bankAccount).isNotNull();
        assertThat(bankAccountManager.getTransactions(promotion)).isEmpty();
        Transaction transaction = Transaction.builder()
                .date(LocalDate.now())
                .type(TransactionType.GATE)
                .amount(123)
                .promotion(promotion)
                .build();
        bankAccountManager.insertTransactions(List.of(transaction));

        List<Transaction> selectedTransactions = database.selectAll(Transaction.class);
        assertThat(selectedTransactions).hasSize(1);
    }

    @Test
    public void insertTransactions() {
        BankAccountManager bankAccountManager = new BankAccountManager(database);
        PromotionManager promotionManager = new PromotionManager(database, bankAccountManager, mock(GameSettingManager.class));
        Promotion promotion = promotionManager.createPromotions(List.of(TestUtils.randomPromotion())).get(0);
        BankAccount bankAccount = bankAccountManager.getBankAccount(promotion);
        assertThat(bankAccount).isNotNull();

        Transaction transaction = Transaction.builder()
                .promotion(promotion)
                .type(TransactionType.GATE)
                .amount(100)
                .build();

        bankAccountManager.insertTransactions(List.of(transaction));

        bankAccount = bankAccountManager.getBankAccount(promotion);
        assertThat(bankAccount.getFunds()).isEqualTo(1000100);

        Transaction transaction2 = Transaction.builder()
                .promotion(promotion)
                .type(TransactionType.GATE)
                .amount(100)
                .build();

        Transaction transaction3 = Transaction.builder()
                .promotion(promotion)
                .type(TransactionType.WORKER_APPEARANCE)
                .amount(100)
                .build();

        Transaction transaction4 = Transaction.builder()
                .promotion(promotion)
                .type(TransactionType.GATE)
                .amount(50)
                .build();

        bankAccountManager.insertTransactions(List.of(transaction2, transaction3, transaction4));

        bankAccount = bankAccountManager.getBankAccount(promotion);
        assertThat(bankAccount.getFunds()).isEqualTo(1000150);
    }
}