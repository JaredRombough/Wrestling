package openwrestling.model.utility;

import openwrestling.model.gameObjects.Segment;
import openwrestling.model.gameObjects.SegmentTeam;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segment.constants.AngleType;
import openwrestling.model.segment.constants.SegmentType;
import openwrestling.model.segment.constants.ShowType;
import openwrestling.model.segment.constants.TeamType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public final class SegmentUtils {

    public static boolean isChallengeForTonight(Segment segment) {
        return segment.getSegmentType().equals(SegmentType.ANGLE)
                && segment.getAngleType().equals(AngleType.CHALLENGE)
                && segment.getShowType().equals(ShowType.TONIGHT);
    }

    public static boolean isHandicapMatch(Segment segment) {
        boolean handicap = false;

        int size = segment.getMatchParticipantTeams().get(0).getWorkers().size();
        for (SegmentTeam team : segment.getMatchParticipantTeams()) {
            if (CollectionUtils.isNotEmpty(team.getWorkers()) && team.getWorkers().size() != size) {
                handicap = true;
                break;

            }
        }
        return handicap;
    }

    public static int getMatchLossMoralePenalty(int winnerPop, int loserPop) {
        int popularityDiff = (loserPop - winnerPop) / 10;
        if (popularityDiff < 0) {
            return 0;
        }

        return (popularityDiff * (popularityDiff + 1)) / 2;
    }

    public static HashMap<Worker, Integer> getMatchMoralePenalties(Segment segment) {
        HashMap<Worker, Integer> objections = new HashMap<>();
        int winnerPopularity = getWinnerMaxPopularity(segment);
        getMatchObjectors(segment).forEach(objector -> objections.put(objector, getMatchLossMoralePenalty(winnerPopularity, objector.getPopularity())));

        return objections;
    }

    public static List<Worker> getMatchObjectors(Segment segment) {
        if (CollectionUtils.isEmpty(segment.getWinners())) {
            return List.of();
        }
        int winnerPopularity = getWinnerMaxPopularity(segment);
        return segment.getSegmentTeams(TeamType.LOSER).stream()
                .flatMap(team -> team.getWorkers().stream())
                .filter(worker -> worker.getPopularity() > winnerPopularity + 10)
                .collect(Collectors.toList());
    }

    private static int getWinnerMaxPopularity(Segment segment) {
        return segment.getWinners().stream().max(Comparator.comparing(Worker::getPopularity)).get().getPopularity();
    }
}
