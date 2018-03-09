package wrestling.model.modelView;

import java.util.List;
import wrestling.model.Worker;
import wrestling.model.utility.ModelUtils;
import wrestling.model.segmentEnum.SuccessType;
import wrestling.model.segmentEnum.TeamType;
import wrestling.model.segmentEnum.TimingType;

public class SegmentTeam {

    private List<Worker> workers;
    private TeamType type;
    private SegmentTeam target;
    private SuccessType success;
    private TimingType timing;

    public SegmentTeam(List<Worker> workers, TeamType type) {
        this.workers = workers;
        this.type = type;
    }

    /**
     * @return the workers
     */
    public List<Worker> getWorkers() {
        return workers;
    }

    /**
     * @param workers the workers to set
     */
    public void setWorkers(List<Worker> workers) {
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

}
