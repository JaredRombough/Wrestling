package openwrestling.manager;

import openwrestling.TestUtils;
import openwrestling.database.Database;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.manager.DateManager;
import openwrestling.model.manager.SegmentManager;
import openwrestling.model.modelView.Segment;
import openwrestling.model.modelView.SegmentTeam;
import openwrestling.model.segmentEnum.EventBroadcast;
import openwrestling.model.segmentEnum.EventFrequency;
import openwrestling.model.segmentEnum.EventRecurrence;
import openwrestling.model.segmentEnum.EventVenueSize;
import openwrestling.model.segmentEnum.MatchFinish;
import openwrestling.model.segmentEnum.MatchRule;
import openwrestling.model.segmentEnum.SegmentType;
import openwrestling.model.segmentEnum.TeamType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventManagerTest {


    private EventManager eventManager;
    private PromotionManager promotionManager = new PromotionManager(new BankAccountManager());
    private Promotion promotion;

    @Before
    public void setUp() {
        Database.createNewDatabase("testdb");
        DateManager mockDateManager = mock(DateManager.class);
        when(mockDateManager.today()).thenReturn(LocalDate.now());
        eventManager = new EventManager(mock(ContractManager.class), mockDateManager, mock(SegmentManager.class));
        promotion = promotionManager.createPromotions(List.of(TestUtils.randomPromotion())).get(0);
    }

    @Test
    public void createEventTemplates() {
        String name = RandomStringUtils.random(10);

        EventTemplate eventTemplate = EventTemplate.builder()
                .name(name)
                .eventBroadcast(EventBroadcast.TELEVISION)
                .eventFrequency(EventFrequency.WEEKLY)
                .eventRecurrence(EventRecurrence.LIMITED)
                .eventVenueSize(EventVenueSize.LARGE)
                .bookedUntil(LocalDate.now().plusDays(100))
                .build();

        eventManager.createEventTemplates(List.of(eventTemplate));

        List<EventTemplate> eventTemplates = Database.selectAll(EventTemplate.class);

        assertThat(eventTemplates).isNotNull().hasOnlyOneElementSatisfying(savedTemplate -> {
            assertThat(savedTemplate.getName()).isEqualTo(name);
            assertThat(savedTemplate.getEventBroadcast()).isEqualTo(EventBroadcast.TELEVISION);
            assertThat(savedTemplate.getEventFrequency()).isEqualTo(EventFrequency.WEEKLY);
        });
    }

    @Test
    public void createEvents() {
        Event event = new Event();
        event.setDate(LocalDate.now());
        EventTemplate eventTemplate = EventTemplate.builder().build();
        eventTemplate = Database.insertOrUpdateList(List.of(eventTemplate)).get(0);
        event.setEventTemplate(eventTemplate);

        Segment segment = Segment.builder()
                .segmentType(SegmentType.MATCH)
                .matchRule(MatchRule.DEFAULT)
                .matchFinish(MatchFinish.CLEAN)
                .build();
        Worker winnerWorker = PersonFactory.randomWorker();
        Worker loserWorker = PersonFactory.randomWorker();
        SegmentTeam winnerTeam = SegmentTeam.builder()
                .workers(List.of(winnerWorker))
                .type(TeamType.WINNER)
                .build();
        SegmentTeam loserTeam = SegmentTeam.builder()
                .workers(List.of(loserWorker))
                .type(TeamType.LOSER)
                .build();
        segment.setTeams(List.of(winnerTeam, loserTeam));
        event.setSegments(List.of(segment));
        event.setPromotion(promotion);
        List<Event> events = eventManager.createEvents(List.of(event));
        assertThat(events).hasSize(1);
        Event savedEvent = events.get(0);
        assertThat(savedEvent.getDate()).isEqualTo(event.getDate());
        assertThat(savedEvent.getPromotion().getPromotionID()).isEqualTo(event.getPromotion().getPromotionID());
        assertThat(savedEvent.getEventTemplate()).isNotNull();
        assertThat(savedEvent.getEventTemplate().getEventTemplateID()).isEqualTo(eventTemplate.getEventTemplateID());
    }
}