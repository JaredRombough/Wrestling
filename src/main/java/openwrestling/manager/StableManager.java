package openwrestling.manager;

import java.util.ArrayList;
import java.util.List;

import openwrestling.database.Database;
import openwrestling.model.gameObjects.Stable;

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

    public void createStables(List<Stable> stables) {
        this.stables.addAll(stables);
        Database.insertList(stables);
    }

}
