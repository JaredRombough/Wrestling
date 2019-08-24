package openwrestling.model;

import openwrestling.model.gameObjects.Worker;

public class TagTeamWorker {

    private final TagTeam tagTeam;
    private final Worker worker;

    public TagTeamWorker(TagTeam tagTeam, Worker worker) {
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
    public Worker getWorker() {
        return worker;
    }

}
