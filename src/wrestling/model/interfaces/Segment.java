package wrestling.model.interfaces;

import wrestling.model.segmentEnum.SegmentType;

public interface Segment {

    public int getRating();

    public void setRating(int rating);

    public SegmentParams getSegmentParams();

    public void setSegmentParams(SegmentParams segmentParams);

    public SegmentType getSegmentType();

}
