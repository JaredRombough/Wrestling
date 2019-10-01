package openwrestling.model.factory;

import openwrestling.manager.ContractManager;
import openwrestling.manager.PromotionManager;
import openwrestling.manager.StableManager;
import openwrestling.manager.TagTeamManager;
import openwrestling.manager.TitleManager;
import openwrestling.manager.WorkerManager;
import openwrestling.model.Event;
import openwrestling.model.Match;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.manager.BankAccountManager;
import openwrestling.manager.EventManager;
import openwrestling.model.manager.NewsManager;
import openwrestling.model.manager.RelationshipManager;
import openwrestling.model.modelView.EventView;
import openwrestling.model.modelView.SegmentTeam;
import openwrestling.model.modelView.SegmentView;
import openwrestling.model.segmentEnum.SegmentType;
import openwrestling.model.segmentEnum.TeamType;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static openwrestling.model.constants.GameConstants.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventFactoryTest {

    private final ContractManager contractManager = mock(ContractManager.class);
    private final EventManager eventManager = mock(EventManager.class);
    private final TitleManager titleManager = mock(TitleManager.class);
    private final WorkerManager workerManager = mock(WorkerManager.class);
    private final MatchFactory matchFactory = mock(MatchFactory.class);
    private final PromotionManager promotionManager = mock(PromotionManager.class);
    private final TagTeamManager tagTeamManager = mock(TagTeamManager.class);
    private final StableManager stableManager = mock(StableManager.class);
    private final NewsManager newsManager = mock(NewsManager.class);
    private final BankAccountManager bankAccountManager = mock(BankAccountManager.class);

    private RelationshipManager relationshipManager;
    private Promotion promotion;
    private Event event;
    private EventView eventView;
    private SegmentView segmentView;
    private Worker winner;
    private Worker loser;

    private EventFactory eventFactory;

    public EventFactoryTest() {
    }

    @Before
    public void setUp() {
        relationshipManager = new RelationshipManager();
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
                bankAccountManager);

        when(matchFactory.saveSegment(any())).thenReturn(new Match());

        promotion = new Promotion();
        event = new Event(promotion, LocalDate.now());
        eventView = new EventView(event, Collections.emptyList());
        segmentView = new SegmentView(SegmentType.MATCH);
        winner = createWorker();
        loser = createWorker();
        segmentView.setTeams(Arrays.asList(new SegmentTeam(winner, TeamType.WINNER), new SegmentTeam(loser, TeamType.LOSER)));
    }

    @Test
    public void processEventView_increaseWinnerMorale() {
        eventFactory.processSegmentView(eventView, segmentView);

        assertEquals(relationshipManager.getRelationshipLevel(winner, promotion), DEFAULT_RELATIONSHIP_LEVEL + MORALE_BONUS_MATCH_WIN);
        assertEquals(relationshipManager.getRelationshipLevel(loser, promotion), DEFAULT_RELATIONSHIP_LEVEL);
    }

    @Test
    public void processEventView_increaseTitleWinnerMorale() {
        Title title = Title.builder()
                .promotion(promotion)
                .sequenceNumber(1)
                .name("title")
                .build();
        title.addReign(Collections.emptyList(), LocalDate.now());
        segmentView.addTitle(title);

        eventFactory.processSegmentView(eventView, segmentView);

        assertEquals(relationshipManager.getRelationshipLevel(winner, promotion), DEFAULT_RELATIONSHIP_LEVEL + MORALE_BONUS_MATCH_WIN + MORALE_BONUS_TITLE_MATCH_WIN);
        assertEquals(relationshipManager.getRelationshipLevel(loser, promotion), DEFAULT_RELATIONSHIP_LEVEL);
    }

    private Worker createWorker() {
        Worker worker = PersonFactory.randomWorker();
        worker.setPopularity(80);
        return worker;
    }

}
