package openwrestling.model.modelView;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.SegmentItem;
import openwrestling.model.SegmentTemplate;
import openwrestling.model.gameObjects.Event;
import openwrestling.model.gameObjects.GameObject;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Stable;
import openwrestling.model.gameObjects.StaffMember;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segmentEnum.AngleType;
import openwrestling.model.segmentEnum.JoinTeamType;
import openwrestling.model.segmentEnum.MatchFinish;
import openwrestling.model.segmentEnum.MatchRule;
import openwrestling.model.segmentEnum.PresenceType;
import openwrestling.model.segmentEnum.PromoType;
import openwrestling.model.segmentEnum.SegmentType;
import openwrestling.model.segmentEnum.SegmentValidation;
import openwrestling.model.segmentEnum.ShowType;
import openwrestling.model.segmentEnum.TeamType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Segment extends GameObject implements Serializable {

    private long segmentID;

    @Builder.Default
    private List<SegmentTeam> teams = new ArrayList<>();

    @Builder.Default
    private List<Title> titles = new ArrayList<>();

    private SegmentType segmentType;
    private Event event;
    private StaffMember referee;
    //TODO
    // private List<? extends SegmentItem> broadcastTeam;
    private Stable newStable;
    private int workRating;
    private int crowdRating;
    private int segmentLength;
    private AngleType angleType;
    private JoinTeamType joinTeamType;
    private PresenceType presenceType;
    private PromoType promoType;
    private ShowType showType;
    private Stable joinStable;
    private SegmentTemplate challengeSegment;

    private MatchFinish matchFinish = MatchFinish.CLEAN;
    private MatchRule matchRule = MatchRule.DEFAULT;

    public LocalDate getDate() {
        return event.getDate();
    }

    public Segment(SegmentType segmentType) {
        this.segmentType = segmentType;
        teams = new ArrayList<>();
        //TODOCaught
        //broadcastTeam = new ArrayList<>();
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
        //TODO
        //segmentItems.addAll(broadcastTeam);
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
     * @param title the title to set
     */
    public void addTitle(Title title) {
        this.titles.add(title);
    }

    public void addTitles(List<Title> titles) {
        this.titles.addAll(titles);
    }

    public Promotion getPromotion() {
        return event.getPromotion();
    }

}
