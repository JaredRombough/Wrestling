package openwrestling.file;

import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        Database.insertGameObject(worker);
        List<Worker> workers = Database.selectAll(Worker.class);
        assertThat(workers).hasSize(1);
        Worker worker1 = workers.get(0);

        assertThat(worker1.getPopularity()).isEqualTo(worker.getPopularity());
        assertThat(worker1.getCharisma()).isEqualTo(worker.getCharisma());
        assertThat(worker1.getName()).isEqualTo(worker.getName());
        assertThat(worker1.getWorkerID()).isNotEqualTo(worker.getWorkerID());
    }


    @Test
    public void contract() {
        Worker worker = Database.insertGameObject(PersonFactory.randomWorker());
        Promotion promotion = Database.insertGameObject(Promotion.builder().name(RandomStringUtils.random(10)).build());

        Contract contract =  Database.insertGameObject(Contract.builder().promotion(promotion).worker(worker).build());

        List<Contract> contracts = Database.selectAll(Contract.class);


        assertThat(contracts).hasSize(1);
        Contract savedContract = contracts.get(0);
        assertThat(savedContract.getContractID()).isEqualTo(contract.getContractID());
        assertThat(savedContract.getWorker()).isNotNull();
        assertThat(savedContract.getWorker().getWorkerID()).isEqualTo(worker.getWorkerID());
        assertThat(savedContract.getWorker().getName()).isEqualTo(worker.getName());
        assertThat(savedContract.getPromotion()).isNotNull();
        assertThat(savedContract.getPromotion().getName()).isEqualTo(promotion.getName());
        assertThat(savedContract.getPromotion().getPromotionID()).isEqualTo(promotion.getPromotionID());
    }

}