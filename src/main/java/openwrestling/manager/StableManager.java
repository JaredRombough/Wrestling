package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.Stable;

import java.util.ArrayList;
import java.util.List;

public class StableManager extends GameObjectManager {

    @Getter
    private List<Stable> stables;

    public StableManager() {
        stables = new ArrayList<>();
    }

    @Override
    public void selectData() {
        stables = Database.selectAll(Stable.class);
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
