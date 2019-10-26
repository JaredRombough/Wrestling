package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.SegmentItem;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MoraleRelationship extends Relationship {

    private Worker worker;
    private Promotion promotion;
    private int level;
    private long relationshipID;

    @Override
    public SegmentItem getSegmentItem1() {
        return worker;
    }

    @Override
    public SegmentItem getSegmentItem2() {
        return promotion;
    }


    @Override
    public SegmentItem getOtherSegmentItem(SegmentItem segmentItem) {
        return Objects.equals(segmentItem, worker) ? promotion : worker;
    }
}
