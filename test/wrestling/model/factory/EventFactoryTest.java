package wrestling.model.factory;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import wrestling.model.Event;
import wrestling.model.Match;
import wrestling.model.Title;
import static wrestling.model.constants.GameConstants.DEFAULT_RELATIONSHIP_LEVEL;
import static wrestling.model.constants.GameConstants.MORALE_BONUS_MATCH_WIN;
import static wrestling.model.constants.GameConstants.MORALE_BONUS_TITLE_MATCH_WIN;
import wrestling.model.manager.ContractManager;
import wrestling.model.manager.EventManager;
import wrestling.model.manager.NewsManager;
import wrestling.model.manager.PromotionManager;
import wrestling.model.manager.RelationshipManager;
import wrestling.model.manager.StableManager;
import wrestling.model.manager.TagTeamManager;
import wrestling.model.manager.TitleManager;
import wrestling.model.manager.WorkerManager;
import wrestling.model.modelView.EventView;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.modelView.SegmentView;
import wrestling.model.modelView.TitleView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.segmentEnum.SegmentType;
import wrestling.model.segmentEnum.TeamType;

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

    private RelationshipManager relationshipManager;
    private PromotionView promotion;
    private Event event;
    private EventView eventView;
    private SegmentView segmentView;
    private WorkerView winner;
    private WorkerView loser;

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
                newsManager);

        when(matchFactory.saveSegment(any())).thenReturn(new Match());

        promotion = new PromotionView();
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
        TitleView titleView = new TitleView(new Title(promotion, 1, "title"));
        titleView.addReign(Collections.emptyList(), LocalDate.now());
        segmentView.addTitle(titleView);

        eventFactory.processSegmentView(eventView, segmentView);

        assertEquals(relationshipManager.getRelationshipLevel(winner, promotion), DEFAULT_RELATIONSHIP_LEVEL + MORALE_BONUS_MATCH_WIN + MORALE_BONUS_TITLE_MATCH_WIN);
        assertEquals(relationshipManager.getRelationshipLevel(loser, promotion), DEFAULT_RELATIONSHIP_LEVEL);
    }

    private WorkerView createWorker() {
        WorkerView worker = PersonFactory.randomWorker();
        worker.setPopularity(80);
        return worker;
    }

}
