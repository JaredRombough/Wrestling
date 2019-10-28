package openwrestling.model.manager;

import openwrestling.TestUtils;
import openwrestling.database.Database;
import openwrestling.manager.BankAccountManager;
import openwrestling.manager.ContractManager;
import openwrestling.manager.EventManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.StableManager;
import openwrestling.manager.TagTeamManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.modelView.Segment;
import openwrestling.model.modelView.SegmentTeam;
import openwrestling.model.segmentEnum.MatchFinish;
import openwrestling.model.segmentEnum.MatchRule;
import openwrestling.model.segmentEnum.SegmentType;
import openwrestling.model.segmentEnum.TeamType;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SegmentManagerTest {

    private EventManager eventManager;
    private SegmentManager segmentManager;
    private WorkerManager workerManager;
    private PromotionManager promotionManager = new PromotionManager(new BankAccountManager());
    private Promotion promotion;

    @Before
    public void setUp() {
        Database.createNewDatabase("testdb");
        DateManager mockDateManager = mock(DateManager.class);
        when(mockDateManager.today()).thenReturn(LocalDate.now());
        workerManager = new WorkerManager(mock(ContractManager.class));
        segmentManager = new SegmentManager(mock(DateManager.class), mock(TagTeamManager.class), mock(StableManager.class));
        eventManager = new EventManager(mock(ContractManager.class), mockDateManager, segmentManager);
        promotion = promotionManager.createPromotions(List.of(TestUtils.randomPromotion())).get(0);
    }


    @Test
    public void createSegments() {
        Event event = new Event();
        event.setDate(LocalDate.now());
        EventTemplate eventTemplate = EventTemplate.builder().build();
        event.setEventTemplate(eventTemplate);

        Segment segment = Segment.builder()
                .segmentType(SegmentType.MATCH)
                .matchRule(MatchRule.DEFAULT)
                .matchFinish(MatchFinish.CLEAN)
                .build();
        Worker winnerWorker = workerManager.createWorker(PersonFactory.randomWorker());
        Worker loserWorker = workerManager.createWorker(PersonFactory.randomWorker());
        SegmentTeam winnerTeam = SegmentTeam.builder()
                .workers(List.of(winnerWorker))
                .type(TeamType.WINNER)
                .build();
        SegmentTeam loserTeam = SegmentTeam.builder()
                .workers(List.of(loserWorker))
                .type(TeamType.LOSER)
                .build();
        segment.setTeams(List.of(winnerTeam, loserTeam));

        event.setPromotion(promotion);
        List<Event> events = eventManager.createEvents(List.of(event));

        segment.setEvent(events.get(0));


        segmentManager.createSegments(List.of(segment));

        List<Segment> segments = segmentManager.getSegments();

        assertThat(segments).hasSize(1);

        Segment savedSegment = segments.get(0);

        assertThat(savedSegment.getSegmentID()).isNotNull().isPositive();
        assertThat(savedSegment.getSegmentType()).isEqualTo(SegmentType.MATCH);
        assertThat(savedSegment.getMatchFinish()).isEqualTo(MatchFinish.CLEAN);
        assertThat(savedSegment.getMatchRule()).isEqualTo(MatchRule.DEFAULT);
        assertThat(savedSegment.getEvent().getEventID()).isEqualTo(events.get(0).getEventID());
        assertThat(savedSegment.getTeams()).hasSize(2);
        SegmentTeam winner = savedSegment.getTeams().stream()
                .filter(segmentTeam -> segmentTeam.getType().equals(TeamType.WINNER))
                .findFirst()
                .orElse(null);
        assertThat(winner).isNotNull();
        assertThat(winner.getSegmentTeamID()).isNotNull().isPositive();
        assertThat(winner.getWorkers()).hasSize(1);
        assertThat(winner.getWorkers().get(0).getWorkerID()).isEqualTo(winnerWorker.getWorkerID());
    }

}