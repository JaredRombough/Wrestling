package openwrestling.model.utility;

import openwrestling.model.gameObjects.Worker;
import openwrestling.model.modelView.SegmentTeam;
import openwrestling.model.modelView.Segment;
import openwrestling.model.segmentEnum.AngleType;
import openwrestling.model.segmentEnum.SegmentType;
import openwrestling.model.segmentEnum.ShowType;
import openwrestling.model.segmentEnum.TeamType;

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
            if (team.getWorkers().size() != size && !team.getWorkers().isEmpty()) {
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
        int winnerPopularity = getWinnerMaxPopularity(segment);
        return segment.getTeams(TeamType.LOSER).stream()
                .flatMap(team -> team.getWorkers().stream())
                .filter(worker -> worker.getPopularity() > winnerPopularity + 10)
                .collect(Collectors.toList());
    }

    private static int getWinnerMaxPopularity(Segment segment) {
        return segment.getWinners().stream().max(Comparator.comparing(Worker::getPopularity)).get().getPopularity();
    }
}
