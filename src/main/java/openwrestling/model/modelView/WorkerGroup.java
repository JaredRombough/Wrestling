package openwrestling.model.modelView;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.SegmentItem;
import openwrestling.model.gameObjects.GameObject;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Worker;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WorkerGroup extends GameObject implements SegmentItem {

    private String name;
    private List<Worker> workers = new ArrayList<>();
    private Promotion owner;

    public WorkerGroup(String name, Promotion owner) {
        this.name = name;
        this.owner = owner;
    }

    @Override
    public List<? extends SegmentItem> getSegmentItems() {
        return workers;
    }

    @Override
    public String toString() {
        return name;
    }

}
