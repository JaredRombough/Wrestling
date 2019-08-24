package openwrestling.model.modelView;

import java.util.ArrayList;
import java.util.List;

import lombok.NoArgsConstructor;
import openwrestling.model.SegmentItem;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;

@NoArgsConstructor
public class WorkerGroup implements SegmentItem {

    private String name;
    private List<Worker> workers = new ArrayList<>();
    private Promotion owner;

    public WorkerGroup(String name, Promotion owner) {
        this.name = name;
        this.owner = owner;
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
    public List<Worker> getWorkers() {
        return workers;
    }

    @Override
    public List<? extends SegmentItem> getSegmentItems() {
        return workers;
    }

    /**
     * @param workers the workers to set
     */
    public void setWorkers(List<Worker> workers) {
        this.workers = workers;
    }

    /**
     * @return the owner
     */
    public Promotion getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(Promotion owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return name;
    }

}
