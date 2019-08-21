package openwrestling.model.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import openwrestling.model.Contract;
import openwrestling.model.modelView.PromotionView;
import openwrestling.model.modelView.WorkerView;
import openwrestling.model.utility.ModelUtils;

public class WorkerManager implements Serializable {

    private final ContractManager contractManager;
    private final List<WorkerView> workers;

    private transient Logger log = LogManager.getLogger(this.getClass());

    public WorkerManager(ContractManager contractManager) {
        this.contractManager = contractManager;
        this.workers = new ArrayList();
    }

    public void addWorkers(List<WorkerView> workers) {
        for (WorkerView worker : workers) {
            this.workers.add(worker);
        }
    }

    public void addWorker(WorkerView worker) {
        workers.add(worker);
    }

    public List<WorkerView> freeAgents(PromotionView promotion) {
        List<WorkerView> freeAgents = new ArrayList();
        for (WorkerView worker : workers) {
            if (contractManager.canNegotiate(worker, promotion)) {
                freeAgents.add(worker);
            }
        }
        return freeAgents;
    }

    public void gainPopularity(WorkerView worker) {

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

    private void addPopularity(WorkerView worker, int pop) {
        worker.setPopularity(worker.getPopularity() + pop);
    }

    public void losePopularity(WorkerView worker) {

        if (RandomUtils.nextInt(1, 10) == 10
                && worker.getPopularity() > 0
                && worker.getPopularity() > worker.getMinimumPopularity()) {
            addPopularity(worker, -1);
        }
    }

    public List<WorkerView> allWorkers() {
        return workers;
    }

}
