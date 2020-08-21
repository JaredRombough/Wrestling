package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.segment.constants.OutcomeType;
import openwrestling.model.segment.constants.PresenceType;
import openwrestling.model.segment.constants.ResponseType;
import openwrestling.model.segment.constants.SuccessType;
import openwrestling.model.segment.constants.TeamType;
import openwrestling.model.segment.constants.TimingType;
import openwrestling.model.utility.ModelUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SegmentTeam extends GameObject implements Serializable {

    private long segmentTeamID;
    private List<Worker> workers;
    private List<Worker> entourage;
    private TeamType type = TeamType.DEFAULT;
    private SegmentTeam target;
    private SuccessType success;
    private TimingType timing;
    private OutcomeType outcome;
    private PresenceType presence;
    private ResponseType response;
    private Segment segment;

    @Override
    public String toString() {
        String string;

        if (type.equals(TeamType.EVERYONE)) {
            string = "Everyone";
        } else if (!type.equals(TeamType.EVERYONE) && CollectionUtils.isEmpty(workers)) {
            string = "(Empty)";
        } else {
            string = ModelUtils.slashShortNames(workers);
        }

        return string;
    }

}
