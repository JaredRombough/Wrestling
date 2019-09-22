package openwrestling.manager;

import openwrestling.database.Database;
import openwrestling.model.gameObjects.Stable;

import java.util.ArrayList;
import java.util.List;

public class StableManager {

    private final List<Stable> stables;

    public StableManager() {
        stables = new ArrayList<>();
    }

    /**
     * @return the stables
     */
    public List<Stable> getStables() {
        return stables;
    }

    public void addStable(Stable stable) {
        stables.add(stable);
    }

    public List<Stable> createStables(List<Stable> stables) {
        List saved = Database.insertList(stables);
        this.stables.addAll(saved);
        return this.stables;
    }

}
