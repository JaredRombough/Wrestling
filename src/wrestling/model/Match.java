package wrestling.model;

import wrestling.model.interfaces.Segment;
import wrestling.model.interfaces.SegmentParams;
import wrestling.model.segmentEnum.SegmentType;

public class Match implements Segment {

    private int workRating;
    private int crowdRating;
    private MatchParams matchParams;

    public Match() {
        matchParams = new MatchParams();
    }

    /**
     * @return the rating
     */
    @Override
    public int getWorkRating() {
        return workRating;
    }

    /**
     * @param rating the rating to set
     */
    @Override
    public void setWorkRating(int rating) {
        this.workRating = rating;
    }

    @Override
    public MatchParams getSegmentParams() {
        return matchParams;
    }

    @Override
    public void setSegmentParams(SegmentParams segmentParams) {
        this.matchParams = (MatchParams) segmentParams;
    }

    @Override
    public SegmentType getSegmentType() {
        return SegmentType.MATCH;
    }

    @Override
    public int getCrowdRating() {
        return crowdRating;
    }

    @Override
    public void setCrowdRating(int crowdRating) {
        this.crowdRating = crowdRating;
    }

}
