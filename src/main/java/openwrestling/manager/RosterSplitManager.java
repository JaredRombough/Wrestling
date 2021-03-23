package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.RosterSplit;
import openwrestling.model.gameObjects.RosterSplitWorker;
import openwrestling.model.gameObjects.Worker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RosterSplitManager extends GameObjectManager {

    private final WorkerManager workerManager;

    @Getter
    private List<RosterSplit> rosterSplits;
    private List<RosterSplitWorker> rosterSplitWorkers;

    public RosterSplitManager(Database database, WorkerManager workerManager) {
        super(database);
        rosterSplits = new ArrayList<>();
        rosterSplitWorkers = new ArrayList<>();
        this.workerManager = workerManager;
    }

    @Override
    public void selectData() {
        rosterSplits = getDatabase().selectAll(RosterSplit.class);
        rosterSplitWorkers = getDatabase().selectAll(RosterSplitWorker.class);

        rosterSplits.forEach(rosterSplit -> {
            List<Worker> workers = rosterSplitWorkers.stream()
                    .filter(rosterSplitWorker -> rosterSplitWorker.getRosterSplit().equals(rosterSplit))
                    .map(RosterSplitWorker::getWorker)
                    .collect(Collectors.toList());
            rosterSplit.setWorkers(workerManager.refreshWorkers(workers));
        });
    }


    public void removeFromRosterSplit(Worker worker, RosterSplit rosterSplit) {
        RosterSplitWorker matchingRosterSplitWorker = rosterSplitWorkers.stream()
                .filter(rosterSplitWorker -> rosterSplitWorker.getWorker().equals(worker))
                .filter(rosterSplitWorker -> rosterSplitWorker.getRosterSplit().equals(rosterSplit))
                .findFirst()
                .orElseThrow();


        getDatabase().deleteByID(RosterSplitWorker.class, matchingRosterSplitWorker.getRosterSplitWorkerID());

        selectData();
    }

    public void addWorkerToRosterSplit(Worker worker, RosterSplit rosterSplit) {
        RosterSplitWorker rosterSplitWorker = RosterSplitWorker.builder()
                .worker(worker)
                .rosterSplit(rosterSplit)
                .build();

        getDatabase().insertList(List.of(rosterSplitWorker));

        selectData();
    }

    public List<RosterSplit> createRosterSplits(List<RosterSplit> rosterSplits) {
        List saved = getDatabase().insertList(rosterSplits);
        selectData();
        return saved;
    }

    public void updateRosterSplit(RosterSplit rosterSplit) {
        updateRosterSplits(List.of(rosterSplit));
    }

    public void updateRosterSplits(List<RosterSplit> rosterSplits) {
        getDatabase().updateList(rosterSplits);
        selectData();
    }

}
