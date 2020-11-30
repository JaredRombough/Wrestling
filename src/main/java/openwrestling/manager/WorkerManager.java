package openwrestling.manager;

import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.model.gameObjects.Contract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.utility.ModelUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class WorkerManager extends GameObjectManager implements Serializable {

    private final ContractManager contractManager;
    @Getter
    private List<Worker> workers;

    public WorkerManager(Database database, ContractManager contractManager) {
        super(database);
        this.contractManager = contractManager;
        this.workers = new ArrayList<>();
    }

    @Override
    public void selectData() {
        workers = getDatabase().selectAll(Worker.class);
    }

    public Worker getWorker(long id) {
        return workers.stream().filter(worker -> worker.getWorkerID() == id).findFirst().orElse(null);
    }

    public List<Worker> getWorkers(List<Long> ids) {
        return workers.stream().filter(worker -> ids.contains(worker.getWorkerID())).collect(Collectors.toList());
    }

    List<Worker> refreshWorkers(List<Worker> workers) {
        return this.workers.stream().filter(workers::contains).collect(Collectors.toList());
    }

    public List<Worker> getRoster(Promotion promotion) {
        List<Worker> roster = new ArrayList<>();
        if (promotion == null) {
            return roster;
        }
        contractManager.getContracts().forEach(contract -> {
            if (contract.isActive() && contract.getPromotion().getPromotionID() == promotion.getPromotionID()) {
                roster.add(contract.getWorker());
            }
        });
        return roster;
    }

    public int averageWorkerPopularity(Promotion promotion) {
        int totalPop = 0;
        int averagePop = 0;
        List<Worker> roster = getRoster(promotion);
        if (!roster.isEmpty()) {
            for (Worker worker : roster) {
                totalPop += worker.getPopularity();
            }
            averagePop = totalPop / roster.size();
        }

        return averagePop;
    }

    public Worker createWorker(Worker worker) {
        return createWorkers(List.of(worker)).get(0);
    }

    public List<Worker> createWorkers(List<Worker> workers) {
        List savedWorkers = getDatabase().insertList(workers);
        this.workers = getDatabase().selectAll(Worker.class);
        return savedWorkers;
    }

    public List<Worker> updateWorkers(List<Worker> workers) {
        List<Worker> savedWorkers = getDatabase().insertList(workers);
        savedWorkers.addAll(
                this.workers.stream()
                        .filter(worker -> savedWorkers.stream().noneMatch(savedWorker -> savedWorker.getWorkerID() == worker.getWorkerID()))
                        .collect(Collectors.toList())
        );
        this.workers = savedWorkers;
        return savedWorkers;
    }

    public List<Worker> freeAgents(Promotion promotion) {
        List<Worker> freeAgents = new ArrayList<>();
        for (Worker worker : workers) {
            if (contractManager.canNegotiate(worker, promotion)) {
                freeAgents.add(worker);
            }
        }
        return freeAgents;
    }

    public void gainPopularity(Worker worker) {

        int maxPopularity = 0;

        for (Contract contract : contractManager.getContracts(worker)) {
            if (ModelUtils.maxPopularity(contract.getPromotion()) > maxPopularity) {
                maxPopularity = ModelUtils.maxPopularity(contract.getPromotion());
            }
        }

        if (worker.getPopularity() < maxPopularity
                || RandomUtils.nextInt(1, 10) == 1
                && worker.getPopularity() > 100) {

            int range = 0;

            if (worker.getPopularity() >= 90) {
                range = 20;
            } else if (worker.getPopularity() < 90 && worker.getPopularity() >= 80) {
                range = 10;
            } else if (worker.getPopularity() < 80 && worker.getPopularity() >= 70) {
                range = 7;
            } else if (worker.getPopularity() < 70) {
                range = 5;
            }

            if (RandomUtils.nextInt(1, range) == 1) {

                addPopularity(worker, 1);
            }
        }
    }

    private void addPopularity(Worker worker, int pop) {
        worker.setPopularity(worker.getPopularity() + pop);
    }

    public void losePopularity(Worker worker) {

        if (RandomUtils.nextInt(1, 10) == 10
                && worker.getPopularity() > 0
                && worker.getPopularity() > worker.getMinimumPopularity()) {
            addPopularity(worker, -1);
        }
    }

}
