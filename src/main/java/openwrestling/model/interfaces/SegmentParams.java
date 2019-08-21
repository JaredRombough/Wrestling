package openwrestling.model.interfaces;

import openwrestling.model.modelView.WorkerGroup;
import openwrestling.model.segmentEnum.JoinTeamType;
import openwrestling.model.segmentEnum.MatchRule;

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
