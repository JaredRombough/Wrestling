package openwrestling.model.modelView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import openwrestling.model.Event;
import openwrestling.model.gameObjects.Worker;

public class EventView implements Serializable {

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

    public List<Worker> allWorkers() {
        List<Worker> allWorkers = new ArrayList<>();
        for (SegmentView segment : segmentViews) {
            for (Worker worker : segment.getWorkers()) {
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
