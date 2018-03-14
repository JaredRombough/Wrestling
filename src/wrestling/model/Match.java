package wrestling.model;

import wrestling.model.segmentEnum.MatchRule;
import wrestling.model.segmentEnum.MatchFinish;
import wrestling.model.interfaces.Segment;

public class Match implements Segment {

    private final MatchRule rules;
    private final MatchFinish finish;
    private final int rating;

    public Match(MatchRule rules, MatchFinish finish, int rating) {
        this.rules = rules;
        this.finish = finish;
        this.rating = rating;
    }

    /**
     * @return the finish
     */
    public MatchFinish getFinish() {
        return finish;
    }

    /**
     * @return the rules
     */
    public MatchRule getRules() {
        return rules;
    }

    /**
     * @return the rating
     */
    @Override
    public int getRating() {
        return rating;
    }


}
