package openwrestling.manager;

import openwrestling.TestUtils;
import openwrestling.database.Database;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Segment;
import openwrestling.model.gameObjects.SegmentTeam;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segment.constants.MatchFinish;
import openwrestling.model.segment.constants.SegmentType;
import openwrestling.model.segment.constants.TeamType;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static openwrestling.TestUtils.TEST_DB_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SegmentStringServiceTest {

    private EventManager eventManager;
    private SegmentManager segmentManager;
    private WorkerManager workerManager;
    private PromotionManager promotionManager;
    private Promotion promotion;
    private Worker worker;
    private Worker worker2;
    private Database database;
    private ContractManager contractManager;
    private SegmentStringService service;
    private LocalDate date;

    @Before
    public void setUp() {
        date = LocalDate.now();
        database = new Database(TEST_DB_PATH);
        DateManager mockDateManager = mock(DateManager.class);
        when(mockDateManager.today()).thenReturn(LocalDate.now());
        promotionManager = new PromotionManager(database, new BankAccountManager(database), mock(GameSettingManager.class));
        workerManager = new WorkerManager(database, mock(ContractManager.class));
        segmentManager = new SegmentManager(database, mock(DateManager.class));
        eventManager = new EventManager(database, mock(ContractManager.class), mockDateManager, segmentManager);
        contractManager = new ContractManager(database, mock(BankAccountManager.class));
        promotion = promotionManager.createPromotions(List.of(TestUtils.randomPromotion())).get(0);

        worker = workerManager.createWorker(PersonFactory.randomWorker());
        worker2 = workerManager.createWorker(PersonFactory.randomWorker());

        service = new SegmentStringService(
                segmentManager,
                mock(TagTeamManager.class),
                mock(StableManager.class),
                eventManager,
                contractManager
        );
    }

    @Test
    public void getPercentOfShowsString() {
        createEvent(worker2, date.minusDays(1));
        createEvent(worker2, date.minusDays(5));
        createEvent(worker, date.minusDays(6));

        Contract contract1 = Contract.builder().promotion(promotion).worker(worker).startDate(date.minusDays(6)).active(true).build();
        contractManager.createContracts(List.of(contract1));

        String noShows = service.getPercentOfShowsString(worker, promotion, date.minusDays(10));
        assertThat(noShows).isEqualTo("");

        String onlyShowIsToday = service.getPercentOfShowsString(worker, promotion, date.minusDays(6));
        assertThat(onlyShowIsToday).isEqualTo("");

        String oneOfOne = service.getPercentOfShowsString(worker, promotion, date.minusDays(5));
        assertThat(oneOfOne).isEqualTo("Appears on 100% of shows");

        String oneOfTwo = service.getPercentOfShowsString(worker, promotion, date.minusDays(4));
        assertThat(oneOfTwo).isEqualTo("Appears on 50% of shows");

        String oneOfThree = service.getPercentOfShowsString(worker, promotion, date);
        assertThat(oneOfThree).isEqualTo("Appears on 33% of shows");
    }

    private void createEvent(Worker worker, LocalDate date) {
        Event event = new Event();
        event.setDate(date);
        EventTemplate eventTemplate = EventTemplate.builder().build();
        eventTemplate = database.insertList(List.of(eventTemplate)).get(0);
        event.setEventTemplate(eventTemplate);

        Segment segment = Segment.builder()
                .segmentType(SegmentType.MATCH)
                .matchFinish(MatchFinish.CLEAN)
                .date(date)
                .build();
        Worker loserWorker = PersonFactory.randomWorker();
        SegmentTeam winnerTeam = SegmentTeam.builder()
                .workers(List.of(worker))
                .type(TeamType.WINNER)
                .build();
        SegmentTeam loserTeam = SegmentTeam.builder()
                .workers(List.of(loserWorker))
                .type(TeamType.LOSER)
                .build();
        segment.setSegmentTeams(List.of(winnerTeam, loserTeam));
        event.setSegments(List.of(segment));
        event.setPromotion(promotion);
        eventManager.createEvents(List.of(event));
    }

}