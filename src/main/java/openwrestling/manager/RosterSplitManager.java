package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.RosterSplit;

import java.util.ArrayList;
import java.util.List;

public class RosterSplitManager extends GameObjectManager {

    private final WorkerManager workerManager;

    @Getter
    private List<RosterSplit> rosterSplits = new ArrayList<>();

    public RosterSplitManager(Database database, WorkerManager workerManager) {
        super(database);
        this.workerManager = workerManager;
    }

    @Override
    public void selectData() {
        rosterSplits = getDatabase().selectAll(RosterSplit.class);
        rosterSplits.forEach(rosterSplit -> rosterSplit.setWorkers(workerManager.refreshWorkers(rosterSplit.getWorkers())));
    }

    public List<RosterSplit> createRosterSplits(List<RosterSplit> rosterSplits) {
        List saved = getDatabase().insertList(rosterSplits);
        this.rosterSplits.addAll(saved);
        return saved;
    }
}
