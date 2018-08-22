package wrestling.model.modelView;

import java.util.ArrayList;
import java.util.List;
import wrestling.model.segmentEnum.OutcomeType;
import wrestling.model.segmentEnum.PresenceType;
import wrestling.model.segmentEnum.SuccessType;
import wrestling.model.segmentEnum.TeamType;
import wrestling.model.segmentEnum.TimingType;
import wrestling.model.utility.ModelUtils;

public class SegmentTeam {

    private List<WorkerView> workers;
    private TeamType type;
    private SegmentTeam target;
    private SuccessType success;
    private TimingType timing;
    private OutcomeType outcome;
    private PresenceType presence;

    public SegmentTeam(List<WorkerView> workers, TeamType type) {
        this.workers = workers;
        this.type = type;
    }

    public SegmentTeam() {
        workers = new ArrayList();
        type = TeamType.DEFAULT;
        outcome = OutcomeType.WINNER;
    }

    /**
     * @return the workers
     */
    public List<WorkerView> getWorkers() {
        return workers;
    }

    /**
     * @param workers the workers to set
     */
    public void setWorkers(List<WorkerView> workers) {
        this.workers = workers;
    }

    /**
     * @return the type
     */
    public TeamType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(TeamType type) {
        this.type = type;
    }

    /**
     * @return the target
     */
    public SegmentTeam getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(SegmentTeam target) {
        this.target = target;
    }

    /**
     * @return the success
     */
    public SuccessType getSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(SuccessType success) {
        this.success = success;
    }

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

    /**
     * @return the timing
     */
    public TimingType getTiming() {
        return timing;
    }

    /**
     * @param timing the timing to set
     */
    public void setTiming(TimingType timing) {
        this.timing = timing;
    }

    /**
     * @return the outcome
     */
    public OutcomeType getOutcome() {
        return outcome;
    }

    /**
     * @param outcome the outcome to set
     */
    public void setOutcome(OutcomeType outcome) {
        this.outcome = outcome;
    }

    /**
     * @return the presence
     */
    public PresenceType getPresence() {
        return presence;
    }

    /**
     * @param presence the presence to set
     */
    public void setPresence(PresenceType presence) {
        this.presence = presence;
    }

}
