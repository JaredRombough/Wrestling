package openwrestling.manager;

import openwrestling.database.Database;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.TitleReign;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.manager.DateManager;
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

    @Before
    public void setUp() {
        Database.createNewDatabase("testdb");
        DateManager mockDateManager = mock(DateManager.class);
        when(mockDateManager.today()).thenReturn(LocalDate.now());
        titleManager = new TitleManager(mockDateManager);
    }

    @Test
    public void title() {
        Worker worker = Database.insertGameObject(PersonFactory.randomWorker());
        Worker worker2 = Database.insertGameObject(PersonFactory.randomWorker());
        Worker worker3 = Database.insertGameObject(PersonFactory.randomWorker());

        Promotion promotion = Database.insertGameObject(Promotion.builder().name(RandomStringUtils.random(10)).build());
        Promotion promotion2 = Database.insertGameObject(Promotion.builder().name(RandomStringUtils.random(10)).build());

        Title title = Title.builder()
                .name(RandomStringUtils.random(10))
                .prestige(12)
                .promotion(promotion)
                .build();

        TitleReign titleReign = TitleReign.builder()
                .sequenceNumber(1)
                .dayWon(LocalDate.now())
                .workers(List.of(worker))
                .title(title)
                .build();

        title.setChampionTitleReign(titleReign);

        Title title2 = Title.builder()
                .name(RandomStringUtils.random(10))
                .prestige(12)
                .promotion(promotion2)
                .build();

        TitleReign titleReign2 = TitleReign.builder()
                .sequenceNumber(1)
                .dayWon(LocalDate.now())
                .workers(List.of(worker2, worker3))
                .title(title2)
                .build();

        title2.setChampionTitleReign(titleReign2);


        Title title3 = Title.builder()
                .name(RandomStringUtils.random(10))
                .prestige(12)
                .promotion(promotion2)
                .build();

        List<Title> titles = titleManager.createTitles(List.of(title, title2, title3));

        assertThat(titles).hasSize(3);

        List<TitleReign> titleReigns = titleManager.getTitleReigns();

        TitleReign savedReign = titleReigns.stream().filter(reign -> reign.getTitle().getName().equals(title.getName())).findFirst().orElse(null);
        assertThat(savedReign).isNotNull();
        assertThat(savedReign.getSequenceNumber()).isEqualTo(1);
        assertThat(savedReign.getWorkers()).hasSize(1);
        assertThat(savedReign.getWorkers().get(0).getWorkerID()).isEqualTo(worker.getWorkerID());
        assertThat(savedReign.getDayWon()).isEqualTo(LocalDate.now());
        assertThat(savedReign.getDayLost()).isNull();
        assertThat(savedReign.getTitleReignID()).isNotNull().isPositive();

        TitleReign savedReign3 = titleReigns.stream().filter(reign -> reign.getTitle().getName().equals(title3.getName())).findFirst().orElse(null);
        assertThat(savedReign3).isNotNull();
        assertThat(savedReign3.getSequenceNumber()).isEqualTo(1);
        assertThat(savedReign3.getWorkers()).isEmpty();
        assertThat(savedReign3.getDayWon()).isEqualTo(LocalDate.now());
        assertThat(savedReign3.getDayLost()).isNull();
        assertThat(savedReign3.getTitleReignID()).isNotNull().isPositive();
    }

}