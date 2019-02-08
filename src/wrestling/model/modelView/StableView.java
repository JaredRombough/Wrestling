package wrestling.model.modelView;

import java.util.ArrayList;
import java.util.List;
import wrestling.model.SegmentItem;

public class StableView implements SegmentItem {

    private String name;
    private List<WorkerView> workers = new ArrayList<>();
    private PromotionView owner;

    public StableView() {
    }

    public StableView(String name, PromotionView owner) {
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
    public List<WorkerView> getWorkers() {
        return workers;
    }

    @Override
    public List<? extends SegmentItem> getSegmentItems() {
        return workers;
    }

    /**
     * @param workers the workers to set
     */
    public void setWorkers(List<WorkerView> workers) {
        this.workers = workers;
    }

    /**
     * @return the owner
     */
    public PromotionView getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(PromotionView owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return name;
    }

}
