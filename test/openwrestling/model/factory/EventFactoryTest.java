package openwrestling.model.factory;

import openwrestling.database.Database;
import openwrestling.manager.BankAccountManager;
import openwrestling.manager.ContractManager;
import openwrestling.manager.DateManager;
import openwrestling.manager.EventManager;
import openwrestling.manager.InjuryManager;
import openwrestling.manager.NewsManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.RelationshipManager;
import openwrestling.manager.SegmentManager;
import openwrestling.manager.StableManager;
import openwrestling.manager.StaffManager;
import openwrestling.manager.TagTeamManager;
import openwrestling.manager.TitleManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.financial.BankAccount;
import openwrestling.model.segmentEnum.EventBroadcast;
import openwrestling.model.segmentEnum.EventFrequency;
import openwrestling.model.segmentEnum.EventVenueSize;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventFactoryTest {

    private final ContractManager contractManager = mock(ContractManager.class);
    private final EventManager eventManager = mock(EventManager.class);
    private final TitleManager titleManager = mock(TitleManager.class);
    private final WorkerManager workerManager = mock(WorkerManager.class);
    private MatchFactory matchFactory;
    private final PromotionManager promotionManager = mock(PromotionManager.class);
    private final TagTeamManager tagTeamManager = mock(TagTeamManager.class);
    private final StableManager stableManager = mock(StableManager.class);
    private final NewsManager newsManager = mock(NewsManager.class);
    private final BankAccountManager bankAccountManager = mock(BankAccountManager.class);
    private final SegmentManager segmentManager = mock(SegmentManager.class);

    private RelationshipManager relationshipManager;

    private LocalDate today;

    private EventFactory eventFactory;

    @Before
    public void setUp() {
        today = LocalDate.now();
        matchFactory = new MatchFactory(segmentManager, mock(DateManager.class), mock(InjuryManager.class), workerManager, mock(StaffManager.class));
        Database.createNewTempDatabase("testdb");
        when(bankAccountManager.getBankAccount(any(Promotion.class))).thenReturn(new BankAccount());
        relationshipManager = mock(RelationshipManager.class);
        eventFactory = new EventFactory(
                contractManager,
                eventManager,
                matchFactory,
                promotionManager,
                titleManager,
                workerManager,
                tagTeamManager,
                stableManager,
                relationshipManager,
                newsManager,
                bankAccountManager,
                segmentManager);

    }


    @Test
    public void bookEventForTemplateOnDate() {
        String name = RandomStringUtils.random(10);
        EventTemplate eventTemplate = EventTemplate.builder()
                .name(name)
                .eventBroadcast(EventBroadcast.TELEVISION)
                .eventFrequency(EventFrequency.WEEKLY)
                .eventVenueSize(EventVenueSize.LARGE)
                .build();

        Event event = EventFactory.bookEventForTemplateOnDate(eventTemplate, today);

        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDate()).isEqualTo(today);
        assertThat(event.getEventTemplate()).isEqualTo(eventTemplate);
    }

    @Test
    public void bookEventForNewAnnualEventTemplateAfterDate() {
        String name = RandomStringUtils.random(10);
        EventTemplate eventTemplate = EventTemplate.builder()
                .name(name)
                .eventBroadcast(EventBroadcast.PPV)
                .dayOfWeek(DayOfWeek.SUNDAY)
                .month(5)
                .eventFrequency(EventFrequency.ANNUAL)
                .eventVenueSize(EventVenueSize.LARGE)
                .build();

        Event event = EventFactory.bookEventForNewAnnualEventTemplateAfterDate(eventTemplate, today);

        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDate().getMonth().getValue()).isEqualTo(eventTemplate.getMonth());
        assertThat(event.getDate()).isAfter(today);
        assertThat(event.getDate().getDayOfWeek()).isEqualTo(eventTemplate.getDayOfWeek());
        assertThat(event.getEventTemplate()).isEqualTo(eventTemplate);
    }

    @Test
    public void bookEventForCompletedAnnualEventTemplateAfterDate() {

        String name = RandomStringUtils.random(10);
        EventTemplate eventTemplate = EventTemplate.builder()
                .name(name)
                .eventBroadcast(EventBroadcast.PPV)
                .dayOfWeek(DayOfWeek.SUNDAY)
                .month(5)
                .eventFrequency(EventFrequency.ANNUAL)
                .eventVenueSize(EventVenueSize.LARGE)
                .build();
        LocalDate date = today.withMonth(eventTemplate.getMonth())
                .withDayOfMonth(1)
                .with(TemporalAdjusters.next(eventTemplate.getDayOfWeek()));
        Event event = EventFactory.bookEventForCompletedAnnualEventTemplateAfterDate(eventTemplate, date);

        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDate().getMonth().getValue()).isEqualTo(eventTemplate.getMonth());
        assertThat(event.getDate().getYear()).isEqualTo(date.plusYears(1).getYear());
        assertThat(event.getDate()).isAfter(date);
        assertThat(event.getDate().getDayOfWeek()).isEqualTo(eventTemplate.getDayOfWeek());
        assertThat(event.getEventTemplate()).isEqualTo(eventTemplate);
    }
}
