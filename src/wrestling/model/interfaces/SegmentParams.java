package wrestling.model.interfaces;

import wrestling.model.segmentEnum.MatchRule;

public interface SegmentParams {

    default MatchRule getMatchRule() {
        return MatchRule.DEFAULT;
    }

}
