package openwrestling.model;

import java.util.Objects;
import static openwrestling.model.constants.GameConstants.MAX_RELATIONSHIP_LEVEL;
import static openwrestling.model.constants.GameConstants.MIN_RELATIONSHIP_LEVEL;

public class Relationship {

    private final SegmentItem segmentItem1;
    private final SegmentItem segmentItem2;
    private int level;

    public Relationship(SegmentItem segmentItem, SegmentItem otherSegmentItem, int level) {
        this.segmentItem1 = segmentItem;
        this.segmentItem2 = otherSegmentItem;
        this.level = level;
    }

    public void modifyValue(int diff) {
        setLevel(level + diff);
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        if (level > MAX_RELATIONSHIP_LEVEL) {
            this.level = MAX_RELATIONSHIP_LEVEL;
        } else if (level < MIN_RELATIONSHIP_LEVEL) {
            this.level = MIN_RELATIONSHIP_LEVEL;
        } else {
            this.level = level;
        }
    }

    /**
     * @return the segmentItem
     */
    public SegmentItem getSegmentItem1() {
        return segmentItem1;
    }

    /**
     * @return the otherSegmentItem
     */
    public SegmentItem getSegmentItem2() {
        return segmentItem2;
    }

    public SegmentItem getOtherSegmentItem(SegmentItem segmentItem) {
        return Objects.equals(segmentItem, segmentItem1) ? segmentItem2 : segmentItem1;
    }
}
