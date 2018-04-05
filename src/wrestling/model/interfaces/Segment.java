package wrestling.model.interfaces;

import wrestling.model.segmentEnum.SegmentType;

public interface Segment {

    public int getWorkRating();

    public void setWorkRating(int workRating);

    public int getCrowdRating();

    public void setCrowdRating(int crowdRating);

    public SegmentParams getSegmentParams();

    public void setSegmentParams(SegmentParams segmentParams);

    public SegmentType getSegmentType();

}
