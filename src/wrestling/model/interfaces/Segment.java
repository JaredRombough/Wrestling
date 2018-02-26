package wrestling.model.interfaces;

import wrestling.model.MatchFinishes;
import wrestling.model.MatchRules;

/*
generic segment class to be extended by more specific segments like matches
 */
public interface Segment {

    public int getRating();

    public MatchRules getRules();

    public MatchFinishes getFinish();
}
