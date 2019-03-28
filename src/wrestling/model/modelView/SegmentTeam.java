package wrestling.model.modelView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.segmentEnum.OutcomeType;
import wrestling.model.segmentEnum.PresenceType;
import wrestling.model.segmentEnum.ResponseType;
import wrestling.model.segmentEnum.SuccessType;
import wrestling.model.segmentEnum.TeamType;
import wrestling.model.segmentEnum.TimingType;
import wrestling.model.utility.ModelUtils;

public class SegmentTeam implements Serializable {

    private List<WorkerView> workers;
    private List<WorkerView> entourage;
    private TeamType type;
    private SegmentTeam target;
    private SuccessType success;
    private TimingType timing;
    private OutcomeType outcome;
    private PresenceType presence;
    private ResponseType response;

    public SegmentTeam(List<WorkerView> workers, TeamType type) {
        this.workers = workers;
        this.type = type;
        entourage = new ArrayList();
    }

    public SegmentTeam() {
        workers = new ArrayList();
        entourage = new ArrayList();
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

    /**
     * @return the entourage
     */
    public List<WorkerView> getEntourage() {
        return entourage;
    }

    /**
     * @param entourage the entourage to set
     */
    public void setEntourage(List<WorkerView> entourage) {
        this.entourage = entourage;
    }

    /**
     * @return the response
     */
    public ResponseType getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(ResponseType response) {
        this.response = response;
    }

}
