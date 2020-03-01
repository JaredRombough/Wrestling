package openwrestling.manager;

import openwrestling.database.Database;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Worker;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


public class EntourageManagerTest {

    private EntourageManager entourageManager;
    private WorkerManager workerManager;

    @Before
    public void setUp() {
        workerManager = new WorkerManager(mock(ContractManager.class));
        entourageManager = new EntourageManager(workerManager);
        Database.createNewTempDatabase("testdb");
    }

    @Test
    public void getEntourage() {
        Worker leader = workerManager.createWorker(PersonFactory.randomWorker());
        Worker follower = workerManager.createWorker(PersonFactory.randomWorker());
        Worker manager = workerManager.createWorker(PersonFactory.randomWorker());

        leader.setManager(manager);
        workerManager.updateWorkers(List.of(leader));

        entourageManager.addWorkerToEntourage(leader, follower);

        List<Worker> entourage = entourageManager.getEntourage(leader);

        assertThat(entourage).hasSize(2);
        assertThat(entourage).extracting(Worker::getWorkerID).containsOnly(follower.getWorkerID(), manager.getWorkerID());
    }

    @Test
    public void removeWorkerFromEntourage() {
        Worker leader = workerManager.createWorker(PersonFactory.randomWorker());
        Worker follower = workerManager.createWorker(PersonFactory.randomWorker());


        entourageManager.addWorkerToEntourage(leader, follower);

        List<Worker> entourage = entourageManager.getEntourage(leader);

        assertThat(entourage).hasSize(1);
        assertThat(entourage).extracting(Worker::getWorkerID).containsOnly(follower.getWorkerID());

        entourageManager.removeWorkerFromEntourage(leader, follower);

        List<Worker> updatedEntourage = entourageManager.getEntourage(leader);
        assertThat(updatedEntourage).isEmpty();
    }

}