package openwrestling.model.interfaces;

import openwrestling.model.SegmentItem;
import openwrestling.model.segment.constants.TeamType;

import java.util.List;

public interface iTeamType {
    public boolean droppable(SegmentItem segmentItem);

    public List<TeamType> getShared();
}
