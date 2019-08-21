package wrestling.model.modelView;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.SegmentItem;
import wrestling.model.Title;
import wrestling.model.interfaces.iRosterSplit;
import wrestling.model.segmentEnum.ActiveType;

public class TitleView implements SegmentItem, Serializable, iRosterSplit {

    private final Title title;
    private final List<TitleReign> titleReigns;
    private TitleReign champions;
    private WorkerGroup rosterSplit;

    public TitleView(Title title) {
        this.title = title;
        titleReigns = new ArrayList<>();
    }

    /**
     * @return the title
     */
    public Title getTitle() {
        return title;
    }

    public void addReign(List<WorkerView> workers, LocalDate dayWon) {
        if (champions != null) {
            champions.setDayLost(dayWon);
        }
        int sequenceNumber = titleReigns.size() + 1;
        TitleReign newChamps = new TitleReign(workers, dayWon, sequenceNumber);
        titleReigns.add(newChamps);
        champions = newChamps;
    }

    /**
     * @return the titleWorkers
     */
    public List<TitleReign> getTitleReigns() {
        return titleReigns;
    }

    @Override
    public String toString() {
        return title.toString();
    }

    @Override
    public String getLongName() {
        return title.toString() + " Title";
    }

    @Override
    public ActiveType getActiveType() {
        return title.getActiveType();
    }

    /**
     * @return the champions
     */
    public List<WorkerView> getChampions() {
        return champions.getWorkers();
    }

    public int getPrestige() {
        return title.getPrestige();
    }

    /**
     * @return the rosterSplit
     */
    @Override
    public WorkerGroup getRosterSplit() {
        return rosterSplit;
    }

    /**
     * @param rosterSplit the rosterSplit to set
     */
    @Override
    public void setRosterSplit(WorkerGroup rosterSplit) {
        this.rosterSplit = rosterSplit;
    }

}
