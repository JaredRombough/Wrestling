package openwrestling.manager;

import openwrestling.database.Database;
import openwrestling.model.gameObjects.EntourageMember;
import openwrestling.model.gameObjects.Worker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class EntourageManager extends GameObjectManager implements Serializable {

    private List<EntourageMember> entourageMembers = new ArrayList<>();
    private WorkerManager workerManager;

    public EntourageManager(WorkerManager workerManager) {
        this.workerManager = workerManager;
    }

    @Override
    public void selectData() {
        entourageMembers = Database.selectAll(EntourageMember.class);
    }

    public List<Worker> getEntourage(Worker leader) {
        List<Worker> entourage = new ArrayList<>();
        if (leader.getManager() != null) {
            entourage.add(leader.getManager());
        }
        entourage.addAll(selectEntourage(leader));
        return entourage;
    }

    public void addWorkerToEntourage(Worker leader, Worker follower) {
        EntourageMember entourageMember = EntourageMember.builder()
                .leader(leader)
                .follower(follower)
                .active(true)
                .build();
        Database.insertGameObject(entourageMember);
        entourageMembers = Database.selectAll(EntourageMember.class);
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
            Database.insertGameObject(entourageMember);
            entourageMembers = Database.selectAll(EntourageMember.class);
        }

    }

    private List<Worker> selectEntourage(Worker leader) {
        List<Worker> entourageWorkers = entourageMembers.stream()
                .filter(entourageMember -> entourageMember.isActive() &&
                        entourageMember.getLeader().getWorkerID() == leader.getWorkerID())
                .map(EntourageMember::getFollower)
                .collect(Collectors.toList());

        return workerManager.getWorkers().stream()
                .filter(entourageWorkers::contains)
                .collect(Collectors.toList());
    }
}
