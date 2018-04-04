package wrestling.model;

import wrestling.model.interfaces.Segment;
import wrestling.model.interfaces.SegmentParams;
import wrestling.model.segmentEnum.SegmentType;

public class Angle implements Segment {

    private int rating;
    private AngleParams angleParams;

    public Angle() {
        angleParams = new AngleParams();
    }

    @Override
    public int getRating() {
        return rating;
    }

    /**
     * @param rating the rating to set
     */
    @Override
    public void setRating(int rating) {
        this.rating = rating;
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

}
