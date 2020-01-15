package openwrestling.model.manager;

import openwrestling.database.Database;
import openwrestling.manager.BankAccountManager;
import openwrestling.manager.ContractManager;
import openwrestling.manager.InjuryManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Injury;
import openwrestling.model.gameObjects.Worker;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InjuryManagerTest {

    InjuryManager injuryManager;

    private WorkerManager workerManager = new WorkerManager(mock(ContractManager.class));
    private PromotionManager promotionManager = new PromotionManager(new BankAccountManager());

    @Before
    public void setUp() {
        Database.createNewDatabase("testdb");
        DateManager mockDateManager = mock(DateManager.class);
        when(mockDateManager.today()).thenReturn(LocalDate.now());
        injuryManager = new InjuryManager(mock(NewsManager.class), mock(WorkerManager.class), mockDateManager);
    }


    @Test
    public void createInjuries() {
        Worker worker = workerManager.createWorker(PersonFactory.randomWorker());

        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate expiryDate = LocalDate.now().plusDays(10);

        Injury injury = Injury.builder()
                .startDate(startDate)
                .expiryDate(expiryDate)
                .worker(worker)
                .build();

        injuryManager.createInjuries(List.of(injury));

        List<Injury> injuries = injuryManager.getInjuries();
        assertThat(injuries).hasSize(1);
        assertThat(injuries.get(0).getWorker()).isEqualTo(worker);
        assertThat(injuries.get(0).getStartDate()).isEqualTo(startDate);
        assertThat(injuries.get(0).getExpiryDate()).isEqualTo(expiryDate);
        assertThat(injuries.get(0).getInjuryID()).isNotNull().isPositive();
    }

    @Test
    public void createInjuries_expired() {
        Worker worker = workerManager.createWorker(PersonFactory.randomWorker());

        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate expiryDate = LocalDate.now().minusDays(1);

        Injury injury = Injury.builder()
                .startDate(startDate)
                .expiryDate(expiryDate)
                .worker(worker)
                .build();

        injuryManager.createInjuries(List.of(injury));

        List<Injury> injuries = injuryManager.getInjuries();
        assertThat(injuries).isEmpty();
    }

    @Test
    public void getInjuryByWorker() {
        Worker worker = workerManager.createWorker(PersonFactory.randomWorker());

        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate expiryDate = LocalDate.now().plusDays(10);

        Injury injury = Injury.builder()
                .startDate(startDate)
                .expiryDate(expiryDate)
                .worker(worker)
                .build();

        injuryManager.createInjuries(List.of(injury));

        Injury savedInjury = injuryManager.getInjury(worker);
        assertThat(savedInjury.getWorker()).isEqualTo(worker);
        assertThat(savedInjury.getStartDate()).isEqualTo(startDate);
        assertThat(savedInjury.getExpiryDate()).isEqualTo(expiryDate);
        assertThat(savedInjury.getInjuryID()).isNotNull().isPositive();
    }
}