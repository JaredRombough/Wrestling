package wrestling.model.interfaces;

import wrestling.model.modelView.StableView;
import wrestling.model.segmentEnum.JoinTeamType;
import wrestling.model.segmentEnum.MatchRule;

public interface SegmentParams {

    default MatchRule getMatchRule() {
        return MatchRule.DEFAULT;
    }

    default StableView getJoinStable() {
        return null;
    }

    default JoinTeamType getJoinTeamType() {
        return null;
    }

}
