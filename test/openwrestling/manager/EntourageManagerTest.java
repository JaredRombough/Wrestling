package openwrestling.manager;

import openwrestling.TestUtils;
import openwrestling.database.Database;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static openwrestling.TestUtils.TEST_DB_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


public class EntourageManagerTest {

    private EntourageManager entourageManager;
    private ContractManager contractManager;
    private WorkerManager workerManager;
    private Promotion promotion;
    private Database database;

    @Before
    public void setUp() {
        database = new Database(TEST_DB_PATH);
        PromotionManager promotionManager = new PromotionManager(database, new BankAccountManager(database), mock(GameSettingManager.class));
        promotion = promotionManager.createPromotions(List.of(TestUtils.randomPromotion())).get(0);

        contractManager = new ContractManager(database, mock(BankAccountManager.class));
        workerManager = new WorkerManager(database, contractManager);
        entourageManager = new EntourageManager(database, workerManager, contractManager);
    }

    @Test
    public void getEntourage() {
        Worker leader = workerManager.createWorker(PersonFactory.randomWorker());
        Worker follower = workerManager.createWorker(PersonFactory.randomWorker());
        Worker manager = workerManager.createWorker(PersonFactory.randomWorker());

        Contract contract1 = Contract.builder().promotion(promotion).worker(leader).active(true).build();
        Contract contract2 = Contract.builder().promotion(promotion).worker(follower).active(true).build();
        Contract contract3 = Contract.builder().promotion(promotion).worker(manager).active(true).build();

        contractManager.createContracts(List.of(contract1, contract2, contract3));


        leader.setManager(manager);
        workerManager.updateWorkers(List.of(leader));

        entourageManager.addWorkerToEntourage(leader, follower);

        List<Worker> entourage = entourageManager.getEntourage(leader, promotion);

        assertThat(entourage).hasSize(2);
        assertThat(entourage).extracting(Worker::getWorkerID).containsOnly(follower.getWorkerID(), manager.getWorkerID());
    }

    @Test
    public void removeWorkerFromEntourage() {
        Worker leader = workerManager.createWorker(PersonFactory.randomWorker());
        Worker follower = workerManager.createWorker(PersonFactory.randomWorker());

        Contract contract1 = Contract.builder().promotion(promotion).worker(leader).active(true).build();
        Contract contract2 = Contract.builder().promotion(promotion).worker(follower).active(true).build();

        contractManager.createContracts(List.of(contract1, contract2));


        entourageManager.addWorkerToEntourage(leader, follower);

        List<Worker> entourage = entourageManager.getEntourage(leader, promotion);

        assertThat(entourage).hasSize(1);
        assertThat(entourage).extracting(Worker::getWorkerID).containsOnly(follower.getWorkerID());

        entourageManager.removeWorkerFromEntourage(leader, follower);

        List<Worker> updatedEntourage = entourageManager.getEntourage(leader, promotion);
        assertThat(updatedEntourage).isEmpty();
    }

    @Test
    public void workerNotUnderContract_notSelectedInEntourage() {
        Worker leader = workerManager.createWorker(PersonFactory.randomWorker());
        Worker follower = workerManager.createWorker(PersonFactory.randomWorker());

        Contract contract1 = Contract.builder().promotion(promotion).worker(leader).active(true).build();
        Contract contract2 = Contract.builder().promotion(promotion).worker(follower).active(false).build();

        contractManager.createContracts(List.of(contract1, contract2));

        entourageManager.addWorkerToEntourage(leader, follower);

        List<Worker> entourage = entourageManager.getEntourage(leader, promotion);

        assertThat(entourage).isEmpty();
    }


}