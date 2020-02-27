package openwrestling.manager;

import openwrestling.database.Database;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.TitleReign;
import openwrestling.model.gameObjects.Worker;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TitleManagerTest {

    private TitleManager titleManager;
    private WorkerManager workerManager;
    private LocalDate today = LocalDate.now();

    @Before
    public void setUp() {
        Database.createNewTempDatabase("testdb");
        DateManager mockDateManager = mock(DateManager.class);
        when(mockDateManager.today()).thenReturn(today);
        workerManager = new WorkerManager(mock(ContractManager.class));
        titleManager = new TitleManager(mockDateManager, workerManager);
    }

    @Test
    public void title_create_vacant() {
        Promotion promotion = Database.insertGameObject(Promotion.builder().name(RandomStringUtils.random(10)).build());

        Title title = Title.builder()
                .promotion(promotion)
                .name(RandomStringUtils.random(10))
                .teamSize(2)
                .prestige(5)
                .build();

        titleManager.createTitle(title);

        List<Title> saved = titleManager.getTitles();

        assertThat(saved).hasSize(1);

        Title savedTitle = saved.get(0);

        assertThat(savedTitle.getPromotion()).isEqualTo(promotion);
        assertThat(savedTitle.getName()).isEqualTo(title.getName());
        assertThat(savedTitle.getTeamSize()).isEqualTo(title.getTeamSize());
        assertThat(savedTitle.getPrestige()).isEqualTo(title.getPrestige());
        assertThat(savedTitle.getTitleID()).isNotNull();

        assertThat(savedTitle.getTitleReigns()).hasSize(1);

        TitleReign vacant = savedTitle.getTitleReigns().get(0);

        assertThat(vacant.getTitleReignID()).isNotNull().isPositive();
        assertThat(vacant.getTitle()).isEqualTo(savedTitle);
        assertThat(vacant.getSequenceNumber()).isEqualTo(1);
        assertThat(vacant.getWorkers()).isEmpty();
        assertThat(vacant.getDayWon()).isEqualTo(today);
        assertThat(vacant.getDayLost()).isNull();
        assertThat(vacant.getTitleReignID()).isNotNull();
    }

    @Test
    public void title_create_champions() {
        Promotion promotion = Database.insertGameObject(Promotion.builder().name(RandomStringUtils.random(10)).build());
        Worker worker = workerManager.createWorker(PersonFactory.randomWorker());

        Title title = Title.builder()
                .promotion(promotion)
                .name(RandomStringUtils.random(10))
                .teamSize(2)
                .prestige(5)
                .build();

        TitleReign titleReign = TitleReign.builder()
                .workers(List.of(worker))
                .sequenceNumber(1)
                .dayWon(today)
                .build();

        title.setTitleReigns(List.of(titleReign));

        titleManager.createTitle(title);

        List<Title> saved = titleManager.getTitles();

        assertThat(saved).hasSize(1);

        Title savedTitle = saved.get(0);


        assertThat(savedTitle.getPromotion()).isEqualTo(promotion);
        assertThat(savedTitle.getName()).isEqualTo(title.getName());
        assertThat(savedTitle.getTeamSize()).isEqualTo(title.getTeamSize());
        assertThat(savedTitle.getPrestige()).isEqualTo(title.getPrestige());
        assertThat(savedTitle.getTitleID()).isNotNull();

        assertThat(savedTitle.getTitleReigns()).hasSize(1);

        TitleReign champs = savedTitle.getTitleReigns().get(0);

        assertThat(champs.getTitleReignID()).isNotNull().isPositive();
        assertThat(champs.getTitle()).isEqualTo(savedTitle);
        assertThat(champs.getSequenceNumber()).isEqualTo(1);
        assertThat(champs.getWorkers()).containsOnly(worker);
        assertThat(champs.getWorkers().get(0).getName()).isEqualTo(worker.getName());
        assertThat(champs.getDayWon()).isEqualTo(today);
        assertThat(champs.getDayLost()).isNull();
        assertThat(champs.getTitleReignID()).isNotNull();
    }

    @Test
    public void title_title_change() {
        Promotion promotion = Database.insertGameObject(Promotion.builder().name(RandomStringUtils.random(10)).build());
        Worker worker = workerManager.createWorker(PersonFactory.randomWorker());
        Worker worker2 = workerManager.createWorker(PersonFactory.randomWorker());

        Title title = Title.builder()
                .promotion(promotion)
                .name(RandomStringUtils.random(10))
                .teamSize(2)
                .prestige(5)
                .build();

        TitleReign titleReign = TitleReign.builder()
                .workers(List.of(worker))
                .sequenceNumber(1)
                //          .title(title)
                .dayWon(today)
                .build();

        title.setTitleReigns(List.of(titleReign));

        titleManager.createTitle(title);
        titleManager.titleChange(titleManager.getTitles().get(0), List.of(worker2));


        List<Title> saved = titleManager.getTitles();

        assertThat(saved).hasSize(1);

        Title savedTitle = saved.get(0);

        assertThat(savedTitle.getTitleReigns()).hasSize(2);

        TitleReign champs = savedTitle.getTitleReigns().stream()
                .filter(titleReign1 -> titleReign1.getSequenceNumber() == 2).findFirst().orElse(null);

        assertThat(champs).isNotNull();
        assertThat(champs.getTitleReignID()).isNotNull().isPositive();
        assertThat(champs.getTitle()).isEqualTo(savedTitle);
        assertThat(champs.getSequenceNumber()).isEqualTo(2);
        assertThat(champs.getWorkers()).containsOnly(worker2);
        assertThat(champs.getWorkers().get(0).getName()).isEqualTo(worker2.getName());
        assertThat(champs.getDayWon()).isEqualTo(today);
        assertThat(champs.getDayLost()).isNull();
        assertThat(champs.getTitleReignID()).isNotNull();

        TitleReign oldChamps = savedTitle.getTitleReigns().stream()
                .filter(titleReign1 -> titleReign1.getSequenceNumber() == 1).findFirst().orElse(null);

        assertThat(oldChamps).isNotNull();
        assertThat(oldChamps.getTitleReignID()).isNotNull().isPositive();
        assertThat(oldChamps.getTitle()).isEqualTo(savedTitle);
        assertThat(oldChamps.getSequenceNumber()).isEqualTo(1);
        assertThat(oldChamps.getWorkers()).containsOnly(worker);
        assertThat(oldChamps.getWorkers().get(0).getName()).isEqualTo(worker.getName());
        assertThat(oldChamps.getDayWon()).isEqualTo(today);
        assertThat(oldChamps.getDayLost()).isEqualTo(today);
        assertThat(oldChamps.getTitleReignID()).isNotNull();
    }

}