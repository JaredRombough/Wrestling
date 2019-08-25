package openwrestling.model.gameObjects;

import lombok.Getter;
import lombok.Setter;
import openwrestling.model.SegmentItem;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class WorkerGroup extends GameObject {
    private String name;
    private List<Worker> workers = new ArrayList<>();
    private Promotion owner;

    public List<? extends SegmentItem> getSegmentItems() {
        return workers;
    }

    public String toString() {
        return name;
    }
}
