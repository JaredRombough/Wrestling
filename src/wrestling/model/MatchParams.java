package wrestling.model;

import wrestling.model.interfaces.SegmentParams;
import wrestling.model.segmentEnum.MatchFinish;
import wrestling.model.segmentEnum.MatchRule;

public class MatchParams implements SegmentParams {

    private MatchFinish matchFinish;
    private MatchRule matchRule;

    public MatchParams() {
        matchFinish = MatchFinish.CLEAN;
        matchRule = MatchRule.DEFAULT;
    }

    /**
     * @return the matchFinish
     */
    public MatchFinish getMatchFinish() {
        return matchFinish;
    }

    /**
     * @param matchFinish the matchFinish to set
     */
    public void setMatchFinish(MatchFinish matchFinish) {
        this.matchFinish = matchFinish;
    }

    /**
     * @return the matchRule
     */
    @Override
    public MatchRule getMatchRule() {
        return matchRule;
    }

    /**
     * @param matchRule the matchRule to set
     */
    public void setMatchRule(MatchRule matchRule) {
        this.matchRule = matchRule;
    }

}
