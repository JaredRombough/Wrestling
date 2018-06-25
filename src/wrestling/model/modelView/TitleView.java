package wrestling.model.modelView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.SegmentItem;
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.model.segmentEnum.ActiveType;

public class TitleView implements SegmentItem {

    private final Title title;
    private final List<TitleReign> titleReigns;

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

    public void addReign(List<Worker> workers, LocalDate dayWon) {
        titleReigns.add(new TitleReign(workers, dayWon));
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

}
