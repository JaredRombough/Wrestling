package openwrestling.manager;


import openwrestling.database.Database;
import openwrestling.model.gameObjects.EntourageMember;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class EntourageManager extends GameObjectManager implements Serializable {

    private final WorkerManager workerManager;
    private final ContractManager contractManager;
    private List<EntourageMember> entourageMembers = new ArrayList<>();

    public EntourageManager(Database database, WorkerManager workerManager, ContractManager contractManager) {
        super(database);
        this.workerManager = workerManager;
        this.contractManager = contractManager;
    }

    @Override
    public void selectData() {
        entourageMembers = getDatabase().selectAll(EntourageMember.class);
    }

    public List<Worker> getEntourage(Worker leader, Promotion promotion) {
        List<Worker> entourage = new ArrayList<>();
        if (leader.getManager() != null) {
            entourage.add(leader.getManager());
        }
        entourage.addAll(selectEntourage(leader, promotion));
        return entourage;
    }

    public void addWorkerToEntourage(Worker leader, Worker follower) {
        EntourageMember entourageMember = EntourageMember.builder()
                .leader(leader)
                .follower(follower)
                .active(true)
                .build();
        getDatabase().insertGameObject(entourageMember);
        entourageMembers = getDatabase().selectAll(EntourageMember.class);
    }

    public void removeWorkerFromEntourage(Worker leader, Worker follower) {
        EntourageMember entourageMember = entourageMembers.stream()
                .filter(entourageMember1 -> entourageMember1.isActive() &&
                        entourageMember1.getLeader().getWorkerID() == leader.getWorkerID() &&
                        entourageMember1.getFollower().getWorkerID() == follower.getWorkerID())
                .findFirst()
                .orElse(null);
        if (entourageMember != null) {
            entourageMember.setActive(false);
            getDatabase().insertGameObject(entourageMember);
            entourageMembers = getDatabase().selectAll(EntourageMember.class);
        }
    }

    private List<Worker> selectEntourage(Worker leader, Promotion promotion) {
        List<Worker> entourageWorkers = entourageMembers.stream()
                .filter(entourageMember -> entourageMember.isActive() &&
                        entourageMember.getLeader().getWorkerID() == leader.getWorkerID() &&
                        contractManager.getActiveContract(entourageMember.getFollower(), promotion) != null)
                .map(EntourageMember::getFollower)
                .collect(Collectors.toList());

        return workerManager.getWorkers().stream()
                .filter(entourageWorkers::contains)
                .collect(Collectors.toList());
    }
}
