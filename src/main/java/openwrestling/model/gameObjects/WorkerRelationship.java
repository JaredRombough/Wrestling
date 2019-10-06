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
public class WorkerRelationship extends Relationship {

    private Worker worker1;
    private Worker worker2;
    private int level;

    @Override
    public SegmentItem getSegmentItem1() {
        return worker1;
    }

    @Override
    public SegmentItem getSegmentItem2() {
        return worker2;
    }


    @Override
    public SegmentItem getOtherSegmentItem(SegmentItem segmentItem) {
        return Objects.equals(segmentItem, worker1) ? worker2 : worker1;
    }
}
