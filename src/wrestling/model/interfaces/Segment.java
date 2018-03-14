package wrestling.model.interfaces;

import wrestling.model.segmentEnum.MatchFinish;
import wrestling.model.segmentEnum.MatchRule;

/*
generic segment class to be extended by more specific segments like matches
 */
public interface Segment {

    public int getRating();

    public MatchRule getRules();

    public MatchFinish getFinish();
}
