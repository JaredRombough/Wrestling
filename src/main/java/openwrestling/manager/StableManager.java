package openwrestling.manager;

import java.util.ArrayList;
import java.util.List;

import openwrestling.file.Database;
import openwrestling.model.modelView.WorkerGroup;

public class StableManager {

    private final List<WorkerGroup> stables;
    private final List<WorkerGroup> rosterSplits;

    public StableManager() {
        stables = new ArrayList<>();
        rosterSplits = new ArrayList<>();
    }

    /**
     * @return the stables
     */
    public List<WorkerGroup> getStables() {
        return stables;
    }

    public void addStable(WorkerGroup stable) {
        stables.add(stable);
    }

    public void createStables(List<WorkerGroup> stables) {
        this.stables.addAll(stables);
        Database.insertList(stables);
    }

    /**
     * @return the rosterGroups
     */
    public List<WorkerGroup> getRosterSplits() {
        return rosterSplits;
    }

    public void addRosterSplit(WorkerGroup rosterSplit) {
        rosterSplits.add(rosterSplit);
    }

}
