package openwrestling.model;

import openwrestling.model.interfaces.Segment;
import openwrestling.model.interfaces.SegmentParams;
import openwrestling.model.segmentEnum.SegmentType;

public class Match implements Segment {

    private int workRating;
    private int crowdRating;
    private MatchParams matchParams;
    private int segmentLength;

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

    @Override
    public int getSegmentLength() {
        return segmentLength;
    }

    /**
     * @param segmentLength the segmentLength to set
     */
    @Override
    public void setSegmentLength(int segmentLength) {
        this.segmentLength = segmentLength;
    }

}
