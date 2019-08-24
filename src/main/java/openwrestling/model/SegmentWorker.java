package openwrestling.model;

import openwrestling.model.interfaces.Segment;
import openwrestling.model.gameObjects.Worker;

public class SegmentWorker {

    private final Segment segment;
    private final Worker worker;

    private final int team;

    public SegmentWorker(Segment segment,
            Worker worker,
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
    public Worker getWorker() {
        return worker;
    }

    /**
     * @return the team
     */
    public int getTeam() {
        return team;
    }

}
