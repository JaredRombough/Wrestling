package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.RosterSplit;

import java.util.ArrayList;
import java.util.List;

public class RosterSplitManager extends GameObjectManager {

    @Getter
    private List<RosterSplit> rosterSplits = new ArrayList<>();

    @Override
    public void selectData() {
        rosterSplits = Database.selectAll(RosterSplit.class);
    }

    public List<RosterSplit> createRosterSplits(List<RosterSplit> rosterSplits) {
        List saved = Database.insertList(rosterSplits);
        this.rosterSplits.addAll(saved);
        return saved;
    }
}
