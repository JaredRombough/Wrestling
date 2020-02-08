package openwrestling.file;

import openwrestling.database.Database;
import openwrestling.manager.BankAccountManager;
import openwrestling.manager.ContractManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.manager.NewsManager;
import openwrestling.manager.RelationshipManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static openwrestling.TestUtils.randomPromotion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class DatabaseTest {

    @Before
    public void setUp() {
        Database.createNewDatabase("testdb");
    }

    @Test
    public void worker_list() {
        Worker worker = PersonFactory.randomWorker();
        Promotion promotion = new Promotion();
        Contract contract = Contract.builder().promotion(promotion).worker(worker).build();
        worker.addContract(contract);
        Database.insertList(List.of(worker));
        List<Worker> workers = Database.selectAll(Worker.class);
        assertThat(workers).hasSize(1);
        Worker worker1 = workers.get(0);

        assertThat(worker1.getPopularity()).isEqualTo(worker.getPopularity());
        assertThat(worker1.getCharisma()).isEqualTo(worker.getCharisma());
        assertThat(worker1.getName()).isEqualTo(worker.getName());
        assertThat(worker1.getWorkerID()).isNotEqualTo(worker.getWorkerID());
    }

    @Test
    public void worker() {
        Worker worker = PersonFactory.randomWorker();
        Promotion promotion = new Promotion();
        Contract contract = Contract.builder().promotion(promotion).worker(worker).build();
        worker.addContract(contract);
        Worker returnedWorker = Database.insertGameObject(worker);
        assertThat(returnedWorker).isNotNull();
        assertThat(returnedWorker.getWorkerID()).isNotEqualTo(0).isPositive();
        List<Worker> selectedWorkers = Database.selectAll(Worker.class);
        assertThat(selectedWorkers).hasSize(1);
        Worker worker1 = selectedWorkers.get(0);

        assertThat(worker1.getPopularity()).isEqualTo(worker.getPopularity());
        assertThat(worker1.getCharisma()).isEqualTo(worker.getCharisma());
        assertThat(worker1.getName()).isEqualTo(worker.getName());
        assertThat(worker1.getWorkerID()).isNotEqualTo(worker.getWorkerID());
    }

    @Test
    public void roster() {
        Worker worker = Database.insertGameObject(PersonFactory.randomWorker());
        Worker worker2 = Database.insertGameObject(PersonFactory.randomWorker());
        Worker worker3 = Database.insertGameObject(PersonFactory.randomWorker());
        Promotion promotion = Database.insertGameObject(randomPromotion());
        Promotion promotion2 = Database.insertGameObject(randomPromotion());

        ContractManager contractManager = new ContractManager(
                mock(PromotionManager.class),
                mock(NewsManager.class),
                mock(RelationshipManager.class),
                mock(BankAccountManager.class));
        WorkerManager workerManager = new WorkerManager(contractManager);

        Contract contract1 = Contract.builder().promotion(promotion).worker(worker).active(true).build();
        Contract contract2 = Contract.builder().promotion(promotion).worker(worker2).active(true).build();
        Contract contract3 = Contract.builder().promotion(promotion2).worker(worker3).active(true).build();

        contractManager.createContracts(List.of(contract1, contract2, contract3));

        List<Worker> promotionRoster = workerManager.selectRoster(promotion);
        List<Worker> promotion2Roster = workerManager.selectRoster(promotion2);

        assertThat(promotionRoster).hasSize(2);
        Worker rosterWorker = promotionRoster.stream()
                .filter(w -> w.getWorkerID() == worker.getWorkerID()).findFirst().orElse(null);
        Worker rosterWorker2 = promotionRoster.stream()
                .filter(w -> w.getWorkerID() == worker2.getWorkerID()).findFirst().orElse(null);

        assertThat(rosterWorker).isNotNull();
        assertThat(rosterWorker.getName()).isEqualTo(worker.getName());
        assertThat(rosterWorker2).isNotNull();
        assertThat(rosterWorker2.getName()).isEqualTo(worker2.getName());

        assertThat(promotion2Roster).hasSize(1);
        Worker rosterWorker3 = promotion2Roster.stream()
                .filter(w -> w.getWorkerID() == worker3.getWorkerID()).findFirst().orElse(null);

        assertThat(rosterWorker3).isNotNull();
        assertThat(rosterWorker3.getName()).isEqualTo(worker3.getName());
    }

    @Test
    public void contract() {
        Worker worker = Database.insertGameObject(PersonFactory.randomWorker());
        Promotion promotion = Database.insertGameObject(Promotion.builder().name(RandomStringUtils.random(10)).build());

        LocalDate startDate = LocalDate.now();
        Contract contract = Database.insertGameObject(
                Contract.builder()
                        .promotion(promotion)
                        .worker(worker)
                        .startDate(startDate)
                        .build()
        );

        assertThat(contract.getStartDate()).isEqualTo(startDate);

        List<Contract> contracts = Database.selectAll(Contract.class);

        assertThat(contracts).hasSize(1);
        Contract savedContract = contracts.get(0);
        assertThat(savedContract.getContractID()).isEqualTo(contract.getContractID());
        assertThat(savedContract.getStartDate()).isEqualTo(startDate);
        assertThat(savedContract.getWorker()).isNotNull();
        assertThat(savedContract.getWorker().getWorkerID()).isEqualTo(worker.getWorkerID());
        assertThat(savedContract.getWorker().getName()).isEqualTo(worker.getName());
        assertThat(savedContract.getPromotion()).isNotNull();
        assertThat(savedContract.getPromotion().getName()).isEqualTo(promotion.getName());
        assertThat(savedContract.getPromotion().getPromotionID()).isEqualTo(promotion.getPromotionID());
    }


}