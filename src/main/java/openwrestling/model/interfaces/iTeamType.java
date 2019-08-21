package openwrestling.model.interfaces;

import java.util.List;
import openwrestling.model.SegmentItem;
import openwrestling.model.segmentEnum.TeamType;

public interface iTeamType {
    public boolean droppable(SegmentItem segmentItem);
    public List<TeamType> getShared();
}
