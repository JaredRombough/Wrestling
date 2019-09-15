package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.SegmentItem;
import openwrestling.model.segmentEnum.ActiveType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagTeam extends GameObject implements Serializable, SegmentItem {

    private long tagTeamID;
    private List<Worker> workers = new ArrayList<>();
    private String name;
    private ActiveType activeType;
    private int experience;

    @Override
    public List<Worker> getSegmentItems() {
        return getWorkers();
    }


    public void addWorker(Worker worker) {
        getWorkers().add(worker);
    }

    @Override
    public String toString() {
        return name;
    }

}
