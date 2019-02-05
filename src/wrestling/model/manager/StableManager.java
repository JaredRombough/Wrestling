package wrestling.model.manager;

import java.util.ArrayList;
import java.util.List;
import wrestling.model.modelView.StableView;

public class StableManager {

    private final List<StableView> stables;

    public StableManager() {
        stables = new ArrayList<>();
    }

    /**
     * @return the stables
     */
    public List<StableView> getStables() {
        return stables;
    }

    public void addStable(StableView stable) {
        stables.add(stable);
    }

}
