package wrestling.model.utility;

import wrestling.model.modelView.SegmentTeam;
import wrestling.model.modelView.SegmentView;
import wrestling.model.segmentEnum.AngleType;
import wrestling.model.segmentEnum.SegmentType;
import wrestling.model.segmentEnum.ShowType;

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
}
