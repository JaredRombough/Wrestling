package openwrestling.model;

import openwrestling.model.modelView.WorkerView;

public class TagTeamWorker {

    private final TagTeam tagTeam;
    private final WorkerView worker;

    public TagTeamWorker(TagTeam tagTeam, WorkerView worker) {
        this.tagTeam = tagTeam;
        this.worker = worker;
    }

    /**
     * @return the tagTeam
     */
    public TagTeam getTagTeam() {
        return tagTeam;
    }

    /**
     * @return the worker
     */
    public WorkerView getWorker() {
        return worker;
    }

}
