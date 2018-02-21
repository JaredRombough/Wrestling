package wrestling.model.modelView;

import java.util.ArrayList;
import java.util.List;
import wrestling.model.Event;

public class EventView {

    private final List<SegmentView> segments;
    private final Event event;

    public EventView(Event event, List<SegmentView> segments) {
        this.segments = new ArrayList<>(segments);
        this.event = event;
    }

    /**
     * @return the segments
     */
    public List<SegmentView> getSegments() {
        return segments;
    }
}
