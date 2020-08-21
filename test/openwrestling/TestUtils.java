package openwrestling;

import openwrestling.model.factory.PersonFactory;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.EventTemplate;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Segment;
import openwrestling.model.gameObjects.SegmentTeam;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segment.constants.MatchFinish;
import openwrestling.model.segment.constants.SegmentType;
import openwrestling.model.segment.constants.TeamType;
import openwrestling.model.segment.opitons.MatchRules;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.util.List;

public class TestUtils {

    public static String TEST_DB_PATH = "C:/temp/testdb.db";

    public static Promotion randomPromotion() {
        return Promotion.builder()
                .name(RandomStringUtils.random(10))
                .level(2)
                .shortName(RandomStringUtils.random(3))
                .popularity(56)
                .build();
    }

    public static Event basicEvent() {
        Event event = new Event();
        event.setDate(LocalDate.now());
        EventTemplate eventTemplate = EventTemplate.builder().build();
        event.setEventTemplate(eventTemplate);

        Segment segment = Segment.builder()
                .segmentType(SegmentType.MATCH)
                .matchRules(new MatchRules())
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
        event.setPromotion(randomPromotion());
        return event;
    }
}
