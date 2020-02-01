package openwrestling.entities;

import java.util.List;

public abstract class Entity {
    public List<? extends Entity> childrenToInsert() {
        return List.of();
    }

    public List<? extends Entity> childrenToInsert2() {
        return List.of();
    }

    public void selectChildren() {
    }
}
