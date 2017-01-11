
package wrestling.model;

import java.io.Serializable;
import java.util.List;

/*
A title links a worker or workers with a promotion
*/
public class Title implements Serializable {

    private List<Worker> workers;
    private Worker worker;

    private int teamSize;
    private Promotion promotion;
    private String name;

    //vacant title
    public Title(Promotion promotion, int teamSize, String name) {
        this.promotion = promotion;
        this.teamSize = teamSize;
        this.name = name;
    }

    //singles title with title holder
    public Title(Promotion promotion, Worker worker, String name) {
        this.promotion = promotion;
        this.worker = worker;
        this.name = name;
        this.teamSize = 1;
    }

    public Title(Promotion promotion, List<Worker> workers, String name) {
        this.promotion = promotion;
        this.workers = workers;
        this.name = name;
        this.teamSize = workers.size();

        if (teamSize == 1) {
            this.worker = workers.get(0);
        }
    }
    
    @Override
    public String toString(){
        return this.getName();
    }

    /**
     * @return the workers
     */
    public List<Worker> getWorkers() {
        return workers;
    }

    /**
     * @param workers the workers to set
     */
    public void setWorkers(List<Worker> workers) {
        this.workers = workers;
    }

    /**
     * @return the worker
     */
    public Worker getWorker() {
        return worker;
    }

    /**
     * @param worker the worker to set
     */
    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    /**
     * @return the promotion
     */
    public Promotion getPromotion() {
        return promotion;
    }

    /**
     * @param promotion the promotion to set
     */
    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
