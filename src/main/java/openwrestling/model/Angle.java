package openwrestling.model;

import java.io.Serializable;
import openwrestling.model.interfaces.Segment;
import openwrestling.model.interfaces.SegmentParams;
import openwrestling.model.segmentEnum.SegmentType;

public class Angle implements Segment, Serializable {

    private int workRating;
    private int crowdRating;
    private AngleParams angleParams;
    private int segmentLength;

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

    /**
     * @return the segmentLength
     */
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
