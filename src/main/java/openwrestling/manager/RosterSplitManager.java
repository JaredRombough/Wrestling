package openwrestling.manager;

import openwrestling.database.Database;
import openwrestling.model.gameObjects.RosterSplit;

import java.util.ArrayList;
import java.util.List;

public class RosterSplitManager {

    private final List<RosterSplit> rosterSplits = new ArrayList<>();

    public List<RosterSplit> getRosterSplits() {
        return rosterSplits;
    }

    public List<RosterSplit> addList(List<RosterSplit> rosterSplits) {
        List saved = Database.insertList(rosterSplits);
        this.rosterSplits.addAll(saved);
        return saved;
    }
}
