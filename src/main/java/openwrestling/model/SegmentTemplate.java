package openwrestling.model;

import java.util.ArrayList;
import java.util.List;
import openwrestling.model.modelView.SegmentTeam;
import openwrestling.model.gameObjects.Title;

public class SegmentTemplate {

    private EventTemplate eventTemplate;
    private final List<Title> title = new ArrayList();
    private final List<SegmentTeam> segmentTeams = new ArrayList();
    private Event sourceEvent;

    /**
     * @return the eventTemplate
     */
    public EventTemplate getEventTemplate() {
        return eventTemplate;
    }

    /**
     * @param eventTemplate the eventTemplate to set
     */
    public void setEventTemplate(EventTemplate eventTemplate) {
        this.eventTemplate = eventTemplate;
    }

    /**
     * @return the titleView
     */
    public List<Title> getTitleViews() {
        return title;
    }

    /**
     * @return the segmentTeams
     */
    public List<SegmentTeam> getSegmentTeams() {
        return segmentTeams;
    }

    /**
     * @return the sourceEvent
     */
    public Event getSourceEvent() {
        return sourceEvent;
    }

    /**
     * @param sourceEvent the sourceEvent to set
     */
    public void setSourceEvent(Event sourceEvent) {
        this.sourceEvent = sourceEvent;
    }

}
