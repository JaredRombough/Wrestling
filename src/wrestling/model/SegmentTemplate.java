package wrestling.model;

import java.util.ArrayList;
import java.util.List;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.modelView.TitleView;

public class SegmentTemplate {

    private EventTemplate eventTemplate;
    private final List<TitleView> titleView = new ArrayList();
    private final List<SegmentTeam> segmentTeams = new ArrayList();

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
    public List<TitleView> getTitleViews() {
        return titleView;
    }

    /**
     * @return the segmentTeams
     */
    public List<SegmentTeam> getSegmentTeams() {
        return segmentTeams;
    }

}
