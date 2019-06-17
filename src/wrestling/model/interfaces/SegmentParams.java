package wrestling.model.interfaces;

import wrestling.model.modelView.WorkerGroup;
import wrestling.model.segmentEnum.JoinTeamType;
import wrestling.model.segmentEnum.MatchRule;

public interface SegmentParams {

    default MatchRule getMatchRule() {
        return MatchRule.DEFAULT;
    }

    default WorkerGroup getJoinStable() {
        return null;
    }

    default JoinTeamType getJoinTeamType() {
        return null;
    }

}
