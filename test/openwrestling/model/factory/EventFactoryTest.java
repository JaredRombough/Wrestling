package openwrestling.model.factory;

import openwrestling.TestUtils;
import openwrestling.database.Database;
import openwrestling.manager.BankAccountManager;
import openwrestling.manager.ContractManager;
import openwrestling.manager.EventManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.StableManager;
import openwrestling.manager.StaffManager;
import openwrestling.manager.TagTeamManager;
import openwrestling.manager.TitleManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.financial.BankAccount;
import openwrestling.manager.DateManager;
import openwrestling.manager.InjuryManager;
import openwrestling.manager.NewsManager;
import openwrestling.manager.RelationshipManager;
import openwrestling.manager.SegmentManager;
import org.junit.Before;
import org.junit.Test;

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

    private EventFactory eventFactory;

    @Before
    public void setUp() {
        matchFactory = new MatchFactory(segmentManager, mock(DateManager.class), mock(InjuryManager.class), workerManager, mock(StaffManager.class));
        Database.createNewDatabase("testdb");
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
    public void processEventView() {
        Event event = TestUtils.basicEvent();

//        Event processed = eventFactory.processEventView(event, true);

//        assertThat(processed).isNotNull();
//        assertThat(processed.getEventTemplate().getNextDate()).isNotNull();
    }


}
