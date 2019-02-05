package wrestling.model.modelView;

import java.util.ArrayList;
import java.util.List;

public class StableView {

    private String name;
    private List<WorkerView> workers;

    public StableView() {
        workers = new ArrayList<>();
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

    /**
     * @return the workers
     */
    public List<WorkerView> getWorkers() {
        return workers;
    }

    /**
     * @param workers the workers to set
     */
    public void setWorkers(List<WorkerView> workers) {
        this.workers = workers;
    }

}
