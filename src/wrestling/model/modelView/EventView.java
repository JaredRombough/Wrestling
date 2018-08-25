package wrestling.model.modelView;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import wrestling.model.Event;

public class EventView {

    private List<SegmentView> segmentViews;
    private final Event event;

    public EventView(Event event, List<SegmentView> segments) {
        this.segmentViews = new ArrayList<>(segments);
        this.event = event;
    }

    /**
     * @return the segments
     */
    public List<SegmentView> getSegmentViews() {
        return segmentViews;
    }

    /**
     * @return the event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * @param segmentViews the segments to set
     */
    public void setSegmentViews(List<SegmentView> segmentViews) {
        this.segmentViews = segmentViews;
    }

    public List<WorkerView> allWorkers() {
        List<WorkerView> allWorkers = new ArrayList<>();
        for (SegmentView segment : segmentViews) {
            for (WorkerView worker : segment.getWorkers()) {
                if (!allWorkers.contains(worker)) {
                    allWorkers.add(worker);
                }
            }
        }

        return allWorkers;
    }

    public String getVerboseEventTitle() {

        if (event.toString().contains(event.getPromotion().getShortName())) {
            return String.format("%s (%s)",
                    event.toString(),
                    event.getDate());
        } else {
            return String.format("%s %s (%s)",
                    event.getPromotion().getShortName(),
                    event.toString(),
                    event.getDate());
        }

    }

    @Override
    public String toString() {
        return event.toString();
    }
}
