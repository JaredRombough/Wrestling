package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.SegmentItem;

import java.util.Objects;

import static openwrestling.model.constants.GameConstants.MAX_RELATIONSHIP_LEVEL;
import static openwrestling.model.constants.GameConstants.MIN_RELATIONSHIP_LEVEL;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Relationship extends GameObject {

    private SegmentItem segmentItem1;
    private SegmentItem segmentItem2;
    private int level;

    public void modifyValue(int diff) {
        setLevel(level + diff);
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

    public SegmentItem getOtherSegmentItem(SegmentItem segmentItem) {
        return Objects.equals(segmentItem, segmentItem1) ? segmentItem2 : segmentItem1;
    }
}
