package openwrestling.manager;

import openwrestling.TestUtils;
import openwrestling.database.Database;
import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Segment;
import openwrestling.model.gameObjects.SegmentTeam;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segment.constants.EventBroadcast;
import openwrestling.model.segment.constants.EventFrequency;
import openwrestling.model.segment.constants.EventVenueSize;
import openwrestling.model.segment.constants.MatchFinish;
import openwrestling.model.segment.constants.SegmentType;
import openwrestling.model.segment.constants.TeamType;
import openwrestling.model.segment.opitons.MatchRules;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static openwrestling.TestUtils.TEST_DB_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventManagerTest {


    private EventManager eventManager;
    private PromotionManager promotionManager;
    private Promotion promotion;
    private Database database;

    @Before
    public void setUp() {
        database = new Database(TEST_DB_PATH);
        DateManager mockDateManager = mock(DateManager.class);
        when(mockDateManager.today()).thenReturn(LocalDate.now());
        promotionManager = new PromotionManager(database, new BankAccountManager(database), mock(GameSettingManager.class));
        eventManager = new EventManager(database, mock(ContractManager.class), mockDateManager, mock(SegmentManager.class), mock(SegmentStringService.class));
        promotion = promotionManager.createPromotions(List.of(TestUtils.randomPromotion())).get(0);
    }

    @Test
    public void createEventTemplates() {
        String name = RandomStringUtils.random(10);

        EventTemplate eventTemplate = EventTemplate.builder()
                .name(name)
                .eventBroadcast(EventBroadcast.TELEVISION)
                .eventFrequency(EventFrequency.WEEKLY)
                .eventVenueSize(EventVenueSize.LARGE)
                .build();

        eventManager.createEventTemplates(List.of(eventTemplate));

        List<EventTemplate> eventTemplates = database.selectAll(EventTemplate.class);

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
        eventTemplate = database.insertList(List.of(eventTemplate)).get(0);
        event.setEventTemplate(eventTemplate);

        Segment segment = Segment.builder()
                .segmentType(SegmentType.MATCH)
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
        segment.setSegmentTeams(List.of(winnerTeam, loserTeam));
        event.setSegments(List.of(segment));
        event.setPromotion(promotion);
        eventManager.createEvents(List.of(event));
        List<Event> events = eventManager.getEvents();
        assertThat(events).hasSize(1);
        Event savedEvent = events.get(0);
        assertThat(savedEvent.getDate()).isEqualTo(event.getDate());
        assertThat(savedEvent.getPromotion().getPromotionID()).isEqualTo(event.getPromotion().getPromotionID());
        assertThat(savedEvent.getEventTemplate()).isNotNull();
        assertThat(savedEvent.getEventTemplate().getEventTemplateID()).isEqualTo(eventTemplate.getEventTemplateID());
    }
}