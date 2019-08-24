package openwrestling.manager;

import openwrestling.file.Database;
import openwrestling.model.Contract;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.manager.ContractManager;
import openwrestling.model.utility.ModelUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkerManager implements Serializable {

    private final ContractManager contractManager;
    private final List<Worker> workers;

    public WorkerManager(ContractManager contractManager) {
        this.contractManager = contractManager;
        this.workers = new ArrayList<>();
    }

    public void createWorkers(List<Worker> workers) {
        this.workers.addAll(workers);
        Database.createEntityList(workers);
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

    public List<Worker> allWorkers() {
        return workers;
    }

}
