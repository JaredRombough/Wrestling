package openwrestling.model.gameObjects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.SegmentItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class Stable extends WorkerGroup implements SegmentItem {

    private String name;
    private List<Worker> workers = new ArrayList<>();
    private Promotion owner;
    private long stableID;

    public Stable(String name, Promotion owner) {
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

    @Override
    public boolean equals(Object object) {
        return object instanceof Stable &&
                Objects.equals(((Stable) object).getStableID(), stableID);
    }

}
