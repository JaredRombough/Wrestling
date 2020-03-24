package openwrestling.model.controller.nextDay;

import openwrestling.TestUtils;
import openwrestling.database.Database;
import openwrestling.manager.BankAccountManager;
import openwrestling.manager.ContractManager;
import openwrestling.manager.DateManager;
import openwrestling.manager.GameSettingManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.SegmentManager;
import openwrestling.manager.StableManager;
import openwrestling.manager.TagTeamManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.factory.ContractFactory;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static openwrestling.TestUtils.TEST_DB_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContractUpdateTest {
    private SegmentManager segmentManager;
    private WorkerManager workerManager;
    private PromotionManager promotionManager;
    private ContractManager contractManager;

    private ContractUpdate contractUpdate;

    private Database database;

    private Promotion promotion;
    private Promotion promotion2;
    private Promotion promotion3;
    private Promotion promotion4;
    private Promotion promotion5;
    private Promotion promotion6;

    private Worker worker;
    private Worker worker2;
    private Worker worker3;
    private Worker worker4;
    private Worker worker5;

    @Before
    public void setUp() {
        database = new Database(TEST_DB_PATH);
        DateManager mockDateManager = mock(DateManager.class);
        when(mockDateManager.today()).thenReturn(LocalDate.now());
        promotionManager = new PromotionManager(database, new BankAccountManager(database), mock(GameSettingManager.class));

        segmentManager = new SegmentManager(database, mock(DateManager.class), mock(TagTeamManager.class), mock(StableManager.class));
        contractManager = new ContractManager(database, mock(BankAccountManager.class));
        workerManager = new WorkerManager(database, contractManager);

        promotion = promotionManager.createPromotions(List.of(TestUtils.randomPromotion())).get(0);
        promotion2 = promotionManager.createPromotions(List.of(TestUtils.randomPromotion())).get(0);
        promotion3 = promotionManager.createPromotions(List.of(TestUtils.randomPromotion())).get(0);
        promotion4 = promotionManager.createPromotions(List.of(TestUtils.randomPromotion())).get(0);
        promotion5 = promotionManager.createPromotions(List.of(TestUtils.randomPromotion())).get(0);
        promotion6 = promotionManager.createPromotions(List.of(TestUtils.randomPromotion())).get(0);

        worker = workerManager.createWorker(PersonFactory.randomWorker());
        worker2 = workerManager.createWorker(PersonFactory.randomWorker());
        worker3 = workerManager.createWorker(PersonFactory.randomWorker());
        worker4 = workerManager.createWorker(PersonFactory.randomWorker());
        worker5 = workerManager.createWorker(PersonFactory.randomWorker());

        contractUpdate = ContractUpdate.builder()
                .promotionManager(promotionManager)
                .dateManager(mockDateManager)
                .workerManager(workerManager)
                .contractManager(contractManager)
                .contractFactory(new ContractFactory(contractManager))
                .build();
    }

    @Test
    public void getNewContracts() {
        List<Contract> newContracts = contractUpdate.getNewContracts();
        assertThat(newContracts).isNotEmpty();
    }
}