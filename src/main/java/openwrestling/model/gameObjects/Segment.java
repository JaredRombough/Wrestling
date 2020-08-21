package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.NewsItem;
import openwrestling.model.SegmentItem;
import openwrestling.model.segment.constants.AngleType;
import openwrestling.model.segment.constants.JoinTeamType;
import openwrestling.model.segment.constants.MatchFinish;
import openwrestling.model.segment.constants.PresenceType;
import openwrestling.model.segment.constants.PromoType;
import openwrestling.model.segment.constants.SegmentType;
import openwrestling.model.segment.constants.SegmentValidation;
import openwrestling.model.segment.constants.ShowType;
import openwrestling.model.segment.constants.TeamType;
import openwrestling.model.segment.opitons.MatchRules;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Segment extends GameObject implements Serializable {

    private long segmentID;

    @Builder.Default
    private List<SegmentTeam> segmentTeams = new ArrayList<>();

    @Builder.Default
    private List<Title> titles = new ArrayList<>();

    private SegmentType segmentType;
    private Event event;
    private LocalDate date;
    private StaffMember referee;
    @Builder.Default
    private List<BroadcastTeamMember> broadcastTeam = new ArrayList<>();
    private Map<Worker, MoraleRelationship> moraleRelationshipMap = new HashMap<>();
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
    private String challengeEventName;
    private SegmentTemplate challengeSegment;
    private List<Injury> injuries = new ArrayList<>();
    private List<NewsItem> segmentNewsItems = new ArrayList<>();

    private MatchFinish matchFinish = MatchFinish.CLEAN;
    private MatchRules matchRules;

    public Segment(SegmentType segmentType) {
        this.segmentType = segmentType;
        segmentTeams = new ArrayList<>();
        titles = new ArrayList<>();
    }

    public List<Worker> getWorkers() {
        List<Worker> workers = new ArrayList<>();
        for (SegmentTeam team : segmentTeams) {
            workers.addAll(team.getWorkers());
        }
        return workers;
    }

    public List<SegmentItem> getSegmentItems() {
        List<SegmentItem> segmentItems = new ArrayList<>();
        for (SegmentTeam team : segmentTeams) {
            segmentItems.addAll(team.getWorkers());
            segmentItems.addAll(team.getEntourage());
        }
        segmentItems.addAll(titles);
        if (CollectionUtils.isNotEmpty(broadcastTeam)) {
            broadcastTeam.forEach(broadcastTeamMember -> {
                if (broadcastTeamMember.getWorker() != null) {
                    segmentItems.add(broadcastTeamMember.getWorker());
                } else if (broadcastTeamMember.getStaffMember() != null) {
                    segmentItems.add(broadcastTeamMember.getStaffMember());
                }
            });
        }
        segmentItems.add(referee);
        return segmentItems;
    }

    /**
     * @return the teams
     */
    public List<SegmentTeam> getSegmentTeams() {
        return segmentTeams;
    }

    public List<SegmentTeam> getMatchParticipantTeams() {
        List<SegmentTeam> participants = new ArrayList<>();
        for (SegmentTeam team : segmentTeams) {
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
        for (SegmentTeam team : segmentTeams) {
            if (team.getType().equals(TeamType.WINNER)
                    || team.getType().equals(TeamType.LOSER)
                    || team.getType().equals(TeamType.DRAW)) {
                participants.addAll(team.getWorkers());
            }
        }
        return participants;
    }

    public List<SegmentTeam> getSegmentTeams(TeamType type) {
        List<SegmentTeam> teamTypeTeams = new ArrayList<>();
        for (SegmentTeam team : segmentTeams) {
            if (team.getType().equals(type)) {
                teamTypeTeams.add(team);
            }
        }
        return teamTypeTeams;
    }

    public SegmentTeam getWinner() {
        List<SegmentTeam> defaultTeams = getSegmentTeams(TeamType.WINNER);
        if (!defaultTeams.isEmpty()) {
            return defaultTeams.get(0);
        }
        return null;
    }

    public List<Worker> getWinners() {
        List<SegmentTeam> defaultTeams = getSegmentTeams(TeamType.WINNER);
        if (!defaultTeams.isEmpty()) {
            return defaultTeams.get(0).getWorkers();
        }
        return Collections.emptyList();
    }

    public SegmentTeam getTeam(Worker worker) {
        for (SegmentTeam team : getSegmentTeams()) {
            if (team.getWorkers().contains(worker)) {
                return team;
            }
        }
        return null;
    }

    public TeamType getTeamType(Worker worker) {
        for (SegmentTeam team : getSegmentTeams()) {
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
            for (SegmentTeam team : getSegmentTeams()) {
                if (CollectionUtils.isEmpty(team.getWorkers())) {
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

    @Override
    public boolean equals(Object object) {
        return object instanceof Segment &&
                Objects.equals(((Segment) object).getSegmentID(), segmentID);
    }

}
