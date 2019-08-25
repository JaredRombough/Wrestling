package openwrestling.model.interfaces;

import openwrestling.model.gameObjects.Stable;
import openwrestling.model.segmentEnum.JoinTeamType;
import openwrestling.model.segmentEnum.MatchRule;

public interface SegmentParams {

    default MatchRule getMatchRule() {
        return MatchRule.DEFAULT;
    }

    default Stable getJoinStable() {
        return null;
    }

    default JoinTeamType getJoinTeamType() {
        return null;
    }

}
