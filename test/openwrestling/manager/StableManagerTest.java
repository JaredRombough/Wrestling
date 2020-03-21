package openwrestling.manager;

import openwrestling.database.Database;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.Worker;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static openwrestling.TestUtils.TEST_DB_PATH;
import static openwrestling.TestUtils.randomPromotion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class StableManagerTest {

    private StableManager stableManager;
    private Database database;
    private WorkerManager workerManager;
    private PromotionManager promotionManager;

    @Before
    public void setUp() {
        database = new Database(TEST_DB_PATH);
        workerManager = new WorkerManager(database, mock(ContractManager.class));
        stableManager = new StableManager(database, workerManager);
        promotionManager = new PromotionManager(database, mock(BankAccountManager.class), mock(GameSettingManager.class));
    }

    @Test
    public void selectData() {
        Worker worker = workerManager.createWorker(PersonFactory.randomWorker());
        Worker worker2 = workerManager.createWorker(PersonFactory.randomWorker());
        Promotion promotion = promotionManager.createPromotions(List.of(randomPromotion())).get(0);

        Stable stable = new Stable();
        stable.setWorkers(List.of(worker, worker2));
        stable.setName(RandomStringUtils.random(10));
        stable.setOwner(promotion);

        Stable saved = stableManager.createStables(List.of(stable)).get(0);
        assertThat(saved.getStableID()).isPositive();
        assertThat(saved.getWorkers()).containsOnlyOnce(worker, worker2);
        assertThat(saved.getOwner()).isEqualTo(promotion);
        assertThat(saved.getName()).isEqualTo(stable.getName());

        stableManager.selectData();

        assertThat(stableManager.getStables()).hasOnlyOneElementSatisfying(selected -> {
            assertThat(selected.getStableID()).isPositive();
            assertThat(selected.getWorkers()).containsOnlyOnce(worker, worker2);
            assertThat(selected.getOwner()).isEqualTo(promotion);
            assertThat(selected.getName()).isEqualTo(stable.getName());
        });
    }

}