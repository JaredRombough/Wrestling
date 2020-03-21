package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.Stable;

import java.util.ArrayList;
import java.util.List;

public class StableManager extends GameObjectManager {

    private WorkerManager workerManager;
    @Getter
    private List<Stable> stables;

    public StableManager(Database database, WorkerManager workerManager) {
        super(database);
        stables = new ArrayList<>();
        this.workerManager = workerManager;
    }

    @Override
    public void selectData() {
        stables = getDatabase().selectAll(Stable.class);
        stables.forEach(stable -> stable.setWorkers(workerManager.refreshWorkers(stable.getWorkers())));
    }

    public void addStable(Stable stable) {
        stables.add(stable);
    }

    public List<Stable> createStables(List<Stable> stables) {
        List saved = getDatabase().insertList(stables);
        this.stables.addAll(saved);
        return this.stables;
    }

}
