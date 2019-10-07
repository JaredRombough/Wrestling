package openwrestling.manager;

import openwrestling.database.Database;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.financial.BankAccount;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static openwrestling.TestUtils.randomPromotion;
import static org.assertj.core.api.Assertions.assertThat;

public class PromotionManagerTest {
    @Before
    public void setUp() {
        Database.createNewDatabase("testdb");
    }


    @Test
    public void createPromotions() {
        BankAccountManager bankAccountManager = new BankAccountManager();
        PromotionManager promotionManager = new PromotionManager(bankAccountManager);
        Promotion promotion = randomPromotion();
        Promotion savedPromotion = promotionManager.createPromotions(List.of(promotion)).get(0);
        assertThat(savedPromotion).isNotNull();
        BankAccount bankAccount = bankAccountManager.getBankAccount(savedPromotion);
        assertThat(bankAccount).isNotNull();
    }
}