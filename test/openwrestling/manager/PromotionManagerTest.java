package openwrestling.manager;

import openwrestling.database.Database;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.financial.BankAccount;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static openwrestling.TestUtils.TEST_DB_PATH;
import static openwrestling.TestUtils.randomPromotion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class PromotionManagerTest {
    private Database database;

    @Before
    public void setUp() {
        database = new Database(TEST_DB_PATH);
    }


    @Test
    public void createPromotions() {
        BankAccountManager bankAccountManager = new BankAccountManager(database);
        PromotionManager promotionManager = new PromotionManager(database, bankAccountManager, mock(GameSettingManager.class));
        Promotion promotion = randomPromotion();
        Promotion savedPromotion = promotionManager.createPromotions(List.of(promotion)).get(0);
        assertThat(savedPromotion).isNotNull();
        BankAccount bankAccount = bankAccountManager.getBankAccount(savedPromotion);
        assertThat(bankAccount).isNotNull();
    }
}