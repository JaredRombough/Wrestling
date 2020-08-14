package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.segmentEnum.OutcomeType;
import openwrestling.model.segmentEnum.PresenceType;
import openwrestling.model.segmentEnum.ResponseType;
import openwrestling.model.segmentEnum.SuccessType;
import openwrestling.model.segmentEnum.TeamType;
import openwrestling.model.segmentEnum.TimingType;
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
