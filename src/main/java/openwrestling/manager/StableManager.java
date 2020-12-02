package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.StableMember;
import openwrestling.model.gameObjects.Worker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StableManager extends GameObjectManager {

    private WorkerManager workerManager;
    @Getter
    private List<Stable> stables;
    private List<StableMember> stableMembers;

    public StableManager(Database database, WorkerManager workerManager) {
        super(database);
        stables = new ArrayList<>();
        stableMembers = new ArrayList<>();
        this.workerManager = workerManager;
    }

    @Override
    public void selectData() {
        stables = getDatabase().selectAll(Stable.class);
        stableMembers = getDatabase().selectAll(StableMember.class);

        stables.forEach(stable -> {
            List<Worker> activeMembers = stableMembers.stream()
                    .filter(stableMember -> stableMember.getStable().equals(stable))
                    .map(StableMember::getWorker)
                    .collect(Collectors.toList());
            stable.setWorkers(workerManager.refreshWorkers(activeMembers));
        });
    }

    public void removeStableMember(Worker worker, Stable stable) {

        StableMember stableMember = stableMembers.stream()
                .filter(member -> member.getWorker().equals(worker))
                .filter(member -> member.getStable().equals(stable))
                .findFirst()
                .orElseThrow();


        getDatabase().deleteByID(StableMember.class, stableMember.getStableMemberID());

        selectData();
    }

    public void addMemberToStable(Worker worker, Stable stable) {

        StableMember stableMember = StableMember.builder()
                .worker(worker)
                .stable(stable)
                .build();

        getDatabase().insertList(List.of(stableMember));

        selectData();
    }

    public Stable createStable(Stable stable) {
        return createStables(List.of(stable)).get(0);
    }


    public List<Stable> createStables(List<Stable> stables) {
        List saved = getDatabase().insertList(stables);
        selectData();
        return saved;
    }


}
