package wrestling.model.modelView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import wrestling.model.Angle;
import wrestling.model.Match;
import wrestling.model.SegmentItem;
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.model.interfaces.Segment;
import wrestling.model.segmentEnum.SegmentType;
import wrestling.model.segmentEnum.SegmentValidation;
import wrestling.model.segmentEnum.TeamType;

public class SegmentView {

    private List<SegmentTeam> teams;
    private LocalDate date;
    private Segment segment;
    private List<TitleView> titleViews;
    private final SegmentType segmentType;
    private EventView eventView;

    public SegmentView(SegmentType segmentType) {
        this.segmentType = segmentType;
        if (segmentType.equals(SegmentType.MATCH)) {
            segment = new Match();
        } else {
            segment = new Angle();
        }
        teams = new ArrayList<>();
        titleViews = new ArrayList<>();
    }

    public List<Worker> getWorkers() {
        List<Worker> workers = new ArrayList<>();
        for (SegmentTeam team : teams) {
            workers.addAll(team.getWorkers());
        }
        return workers;
    }

    public List<SegmentItem> getSegmentItems() {
        List<SegmentItem> segmentItems = new ArrayList<>();
        for (SegmentTeam team : teams) {
            segmentItems.addAll(team.getWorkers());
        }
        segmentItems.addAll(titleViews);
        return segmentItems;
    }

    /**
     * @return the teams
     */
    public List<SegmentTeam> getTeams() {
        return teams;
    }

    public List<SegmentTeam> getMatchParticipantTeams() {
        List<SegmentTeam> participants = new ArrayList<>();
        for (SegmentTeam team : teams) {
            if (team.getType().equals(TeamType.WINNER)
                    || team.getType().equals(TeamType.LOSER)
                    || team.getType().equals(TeamType.DRAW)) {
                participants.add(team);
            }
        }
        return participants;
    }

    public List<Worker> getMatchParticipants() {
        List<Worker> participants = new ArrayList<>();
        for (SegmentTeam team : teams) {
            if (team.getType().equals(TeamType.WINNER)
                    || team.getType().equals(TeamType.LOSER)
                    || team.getType().equals(TeamType.DRAW)) {
                participants.addAll(team.getWorkers());
            }
        }
        return participants;
    }

    public List<SegmentTeam> getTeams(TeamType type) {
        List<SegmentTeam> teamTypeTeams = new ArrayList<>();
        for (SegmentTeam team : teamTypeTeams) {
            if (team.getType().equals(type)) {
                teamTypeTeams.add(team);
            }
        }
        return teamTypeTeams;
    }

    public SegmentTeam getWinner() {
        List<SegmentTeam> defaultTeams = getTeams(TeamType.WINNER);
        if (!defaultTeams.isEmpty()) {
            return defaultTeams.get(0);
        }
        return null;
    }

    public SegmentTeam getTeam(Worker worker) {
        for (SegmentTeam team : getTeams()) {
            if (team.getWorkers().contains(worker)) {
                return team;
            }
        }
        return null;
    }

    public TeamType getTeamType(Worker worker) {
        for (SegmentTeam team : getTeams()) {
            if (team.getWorkers().contains(worker)) {
                return team.getType();
            }
        }
        return null;
    }

    public SegmentValidation getValidationStatus() {
        if (getWorkers().isEmpty()) {
            return SegmentValidation.EMPTY;
        } else {
            for (SegmentTeam team : getTeams()) {
                if (team.getWorkers().isEmpty()) {
                    return SegmentValidation.INCOMPLETE;
                }
            }
        }

        return SegmentValidation.COMPLETE;
    }

    /**
     * @param teams the teams to set
     */
    public void setTeams(List<SegmentTeam> teams) {
        this.teams = teams;
    }

    /**
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * @return the segment
     */
    public Segment getSegment() {
        return segment;
    }

    /**
     * @param segment the segment to set
     */
    public void setSegment(Segment segment) {
        this.segment = segment;
    }

    /**
     * @return the title
     */
    public List<TitleView> getTitleViews() {
        return titleViews;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(TitleView title) {
        this.titleViews = Arrays.asList(title);
    }

    public void setTitleViews(List<TitleView> titleViews) {
        this.titleViews = titleViews;
    }

    /**
     * @return the segmentType
     */
    public SegmentType getSegmentType() {
        return segmentType;
    }

    /**
     * @return the eventView
     */
    public EventView getEventView() {
        return eventView;
    }

    /**
     * @param eventView the eventView to set
     */
    public void setEventView(EventView eventView) {
        this.eventView = eventView;
    }

}
