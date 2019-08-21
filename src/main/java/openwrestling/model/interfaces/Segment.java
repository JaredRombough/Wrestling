package openwrestling.model.interfaces;

import openwrestling.model.segmentEnum.SegmentType;

public interface Segment {

    public int getWorkRating();

    public void setWorkRating(int workRating);

    public int getCrowdRating();

    public void setCrowdRating(int crowdRating);

    public SegmentParams getSegmentParams();

    public void setSegmentParams(SegmentParams segmentParams);

    public SegmentType getSegmentType();

    public int getSegmentLength();

    public void setSegmentLength(int segmentLengths);

}
