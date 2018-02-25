package wrestling.model;

import wrestling.model.interfaces.Segment;

public class Match implements Segment {

    private final MatchRules rules;
    private final MatchFinishes finish;
    private final int rating;

    public Match(MatchRules rules, MatchFinishes finish, int rating) {
        this.rules = rules;
        this.finish = finish;
        this.rating = rating;
    }

    /**
     * @return the finish
     */
    public MatchFinishes getFinish() {
        return finish;
    }

    /**
     * @return the rules
     */
    public MatchRules getRules() {
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
