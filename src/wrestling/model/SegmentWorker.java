package wrestling.model;

import wrestling.model.interfaces.Segment;
import wrestling.model.modelView.WorkerView;

public class SegmentWorker {

    private final Segment segment;
    private final WorkerView worker;

    private final int team;

    public SegmentWorker(Segment segment,
            WorkerView worker,
            int team) {
        this.segment = segment;
        this.worker = worker;
        this.team = team;
    }

    /**
     * @return the match
     */
    public Segment getSegment() {
        return segment;
    }

    /**
     * @return the worker
     */
    public WorkerView getWorker() {
        return worker;
    }

    /**
     * @return the team
     */
    public int getTeam() {
        return team;
    }

}
