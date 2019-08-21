package openwrestling.model.utility;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import openwrestling.model.modelView.SegmentTeam;
import openwrestling.model.modelView.SegmentView;
import openwrestling.model.modelView.WorkerView;
import openwrestling.model.segmentEnum.AngleType;
import openwrestling.model.segmentEnum.SegmentType;
import openwrestling.model.segmentEnum.ShowType;
import openwrestling.model.segmentEnum.TeamType;

public final class SegmentUtils {

    public static boolean isChallengeForTonight(SegmentView segmentView) {
        return segmentView.getSegmentType().equals(SegmentType.ANGLE)
                && segmentView.getAngleParams().getAngleType().equals(AngleType.CHALLENGE)
                && segmentView.getAngleParams().getShowType().equals(ShowType.TONIGHT);
    }

    public static boolean isHandicapMatch(SegmentView segmentView) {
        boolean handicap = false;

        int size = segmentView.getMatchParticipantTeams().get(0).getWorkers().size();
        for (SegmentTeam team : segmentView.getMatchParticipantTeams()) {
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

    public static HashMap<WorkerView, Integer> getMatchMoralePenalties(SegmentView segment) {
        HashMap<WorkerView, Integer> objections = new HashMap<>();
        int winnerPopularity = getWinnerMaxPopularity(segment);
        getMatchObjectors(segment).forEach(objector -> objections.put(objector, getMatchLossMoralePenalty(winnerPopularity, objector.getPopularity())));

        return objections;
    }

    public static List<WorkerView> getMatchObjectors(SegmentView segment) {
        int winnerPopularity = getWinnerMaxPopularity(segment);
        return segment.getTeams(TeamType.LOSER).stream()
                .flatMap(team -> team.getWorkers().stream())
                .filter(worker -> worker.getPopularity() > winnerPopularity + 10)
                .collect(Collectors.toList());
    }

    private static int getWinnerMaxPopularity(SegmentView segment) {
        return segment.getWinners().stream().max(Comparator.comparing(WorkerView::getPopularity)).get().getPopularity();
    }
}
