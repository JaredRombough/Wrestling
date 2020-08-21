package openwrestling.model.interfaces;

import openwrestling.model.gameObjects.Stable;
import openwrestling.model.segment.constants.JoinTeamType;
import openwrestling.model.segment.opitons.MatchRules;

public interface SegmentParams {

    default MatchRules getMatchRule() {
        return new MatchRules();
    }

    default Stable getJoinStable() {
        return null;
    }

    default JoinTeamType getJoinTeamType() {
        return null;
    }

}
