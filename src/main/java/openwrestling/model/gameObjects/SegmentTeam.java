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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SegmentTeam extends GameObject implements Serializable {

    private long segmentTeamID;
    @Builder.Default
    private List<Worker> workers = new ArrayList<>();
    @Builder.Default
    private List<Worker> entourage = new ArrayList<>();
    @Builder.Default
    private TeamType type = TeamType.DEFAULT;
    private SegmentTeam target;
    private SuccessType success;
    private TimingType timing;
    //    @Builder.Default
//    private OutcomeType outcome = OutcomeType.WINNER;
    private OutcomeType outcome;
    private PresenceType presence;
    private ResponseType response;
    private Segment segment;

//    public SegmentTeam(Worker worker, TeamType type) {
//        this.workers = Arrays.asList(worker);
//        this.type = type;
//    }
//
//    public SegmentTeam(List<Worker> workers, TeamType type) {
//        this.workers = workers;
//        this.type = type;
//    }

    @Override
    public String toString() {
        String string;

        if (type.equals(TeamType.EVERYONE)) {
            string = "Everyone";
        } else if (!type.equals(TeamType.EVERYONE) && workers.isEmpty()) {
            string = "(Empty)";
        } else {
            string = ModelUtils.slashShortNames(workers);
        }

        return string;
    }

}
