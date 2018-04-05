package wrestling.model;

import wrestling.model.interfaces.Segment;
import wrestling.model.interfaces.SegmentParams;
import wrestling.model.segmentEnum.SegmentType;

public class Angle implements Segment {

    private int workRating;
    private int crowdRating;
    private AngleParams angleParams;

    public Angle() {
        angleParams = new AngleParams();
    }

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
    public SegmentParams getSegmentParams() {
        return angleParams;
    }

    @Override
    public void setSegmentParams(SegmentParams segmentParams) {
        this.angleParams = (AngleParams) segmentParams;
    }

    @Override
    public SegmentType getSegmentType() {
        return SegmentType.ANGLE;
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
