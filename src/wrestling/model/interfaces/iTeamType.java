package wrestling.model.interfaces;

import java.util.List;
import wrestling.model.SegmentItem;
import wrestling.model.segmentEnum.TeamType;

public interface iTeamType {
    public boolean droppable(SegmentItem segmentItem);
    public List<TeamType> getShared();
}
