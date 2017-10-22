package wrestling.model.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wrestling.model.Contract;
import wrestling.model.Promotion;
import wrestling.model.Worker;
import wrestling.model.utility.ModelUtilityFunctions;

public class WorkerManager implements Serializable {
    
    private final ContractManager contractManager;
    private final List<Worker> workers;
    
    
    private transient Logger log = LogManager.getLogger(this.getClass());
    public WorkerManager(ContractManager contractManager) {
        this.contractManager = contractManager;
        this.workers = new ArrayList();
    }
    
    public void addWorkers(List<Worker> workers) {
        for (Worker worker : workers) {
            this.workers.add(worker);
        }
    }
    
    public List<Worker> freeAgents(Promotion promotion) {
        List<Worker> freeAgents = new ArrayList();
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
            if (ModelUtilityFunctions.maxPopularity(contract.getPromotion()) > maxPopularity) {
                maxPopularity = ModelUtilityFunctions.maxPopularity(contract.getPromotion());
            }
        }
        
        if (worker.getPopularity() < maxPopularity
                || ModelUtilityFunctions.randRange(1, 10) == 1
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
            
            if (ModelUtilityFunctions.randRange(1, range) == 1) {
                
                addPopularity(worker, 1);
            }
        }
    }
    
    private void addPopularity(Worker worker, int pop) {
        worker.setPopularity(worker.getPopularity() + pop);
    }
    
    public void losePopularity(Worker worker) {
        
        if (ModelUtilityFunctions.randRange(1, 10) == 10
                && worker.getPopularity() > 0
                && worker.getPopularity() > worker.getMinimumPopularity()) {
            addPopularity(worker, -1);
        }
    }
    
    public List<Worker> allWorkers() {
        return workers;
    }
    
}
