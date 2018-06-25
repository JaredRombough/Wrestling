package wrestling.model.modelView;

import java.util.ArrayList;
import java.util.List;
import wrestling.model.SegmentItem;
import wrestling.model.TagTeam;
import wrestling.model.Worker;
import wrestling.model.segmentEnum.ActiveType;

public class TagTeamView implements SegmentItem {

    private TagTeam tagTeam;
    private List<Worker> workers = new ArrayList<>();

    @Override
    public List<Worker> getSegmentItems() {
        return getWorkers();
    }

    @Override
    public String toString() {
        return tagTeam.getName();
    }

    public void addWorker(Worker worker) {
        getWorkers().add(worker);
    }

    /**
     * @return the tagTeam
     */
    public TagTeam getTagTeam() {
        return tagTeam;
    }

    /**
     * @param tagTeam the tagTeam to set
     */
    public void setTagTeam(TagTeam tagTeam) {
        this.tagTeam = tagTeam;
    }

    /**
     * @return the workers
     */
    public List<Worker> getWorkers() {
        return workers;
    }

    @Override
    public ActiveType getActiveType() {
        return tagTeam.getActiveType();
    }

}
