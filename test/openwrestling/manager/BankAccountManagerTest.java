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

        List<BankAccount> selected = Database.selectAll(BankAccount.class);
        assertThat(selected).hasSize(1);
        assertThat(selected.get(0).getTransactions()).hasSize(1);

        bankAccount = bankAccountManager.getBankAccount(promotion);
        assertThat(bankAccount).isNotNull();
        assertThat(bankAccount.getTransactions()).hasSize(1);


    }
}