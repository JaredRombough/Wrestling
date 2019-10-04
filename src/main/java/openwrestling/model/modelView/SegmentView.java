package openwrestling.model.modelView;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import openwrestling.model.Angle;
import openwrestling.model.AngleParams;
import openwrestling.model.Match;
import openwrestling.model.SegmentItem;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.interfaces.Segment;
import openwrestling.model.segmentEnum.MatchRule;
import openwrestling.model.segmentEnum.SegmentType;
import openwrestling.model.segmentEnum.SegmentValidation;
import openwrestling.model.segmentEnum.TeamType;

public class SegmentView implements Serializable {

    private List<SegmentTeam> teams;
    private LocalDate date;
    private Segment segment;
    private final List<Title> titles;
    private final SegmentType segmentType;
    private EventView eventView;
    private StaffMember referee;
    private List<? extends SegmentItem> broadcastTeam;
    private Stable newStable;

    public SegmentView(SegmentType segmentType) {
        this.segmentType = segmentType;
        if (segmentType.equals(SegmentType.MATCH)) {
            segment = new Match();
        } else {
            segment = new Angle();
        }
        teams = new ArrayList<>();
        broadcastTeam = new ArrayList<>();
        titles = new ArrayList<>();
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
            segmentItems.addAll(team.getEntourage());
        }
        segmentItems.addAll(titles);
        segmentItems.addAll(broadcastTeam);
        segmentItems.add(referee);
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
        for (SegmentTeam team : teams) {
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

    public List<Worker> getWinners() {
        List<SegmentTeam> defaultTeams = getTeams(TeamType.WINNER);
        if (!defaultTeams.isEmpty()) {
            return defaultTeams.get(0).getWorkers();
        }
        return Collections.emptyList();
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
    public List<Title> getTitles() {
        return titles;
    }

    /**
     * @param title the title to set
     */
    public void addTitle(Title title) {
        this.titles.add(title);
    }

    public void addTitles(List<Title> titles) {
        this.titles.addAll(titles);
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

    public Promotion getPromotion() {
        return eventView.getEvent().getPromotion();
    }

    /**
     * @param eventView the eventView to set
     */
    public void setEventView(EventView eventView) {
        this.eventView = eventView;
    }

    /**
     * @return the referee
     */
    public StaffMember getReferee() {
        return referee;
    }

    /**
     * @param referee the referee to set
     */
    public void setReferee(StaffMember referee) {
        this.referee = referee;
    }

    /**
     * @return the broadcastTeam
     */
    public List<? extends SegmentItem> getBroadcastTeam() {
        return broadcastTeam;
    }

    /**
     * @param broadcastTeam the broadcastTeam to set
     */
    public void setBroadcastTeam(List<? extends SegmentItem> broadcastTeam) {
        this.broadcastTeam = broadcastTeam;
    }

    public MatchRule getMatchRule() {
        return segment.getSegmentParams().getMatchRule();
    }

    /**
     * @return the newStable
     */
    public Stable getNewStable() {
        return newStable;
    }

    /**
     * @param newStable the newStable to set
     */
    public void setNewStable(Stable newStable) {
        this.newStable = newStable;
    }

    /**
     * @return the angleParams
     */
    public AngleParams getAngleParams() {
        return (AngleParams) segment.getSegmentParams();
    }

}
