package wrestling.model.modelView;

import java.util.List;
import wrestling.model.Worker;
import wrestling.view.event.TeamType;

public class SegmentTeam {

    private List<Worker> workers;
    private TeamType type;

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

}
