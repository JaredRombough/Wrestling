package wrestling.model.modelView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.segmentEnum.MatchFinish;
import wrestling.model.segmentEnum.MatchRule;
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.model.interfaces.Segment;
import wrestling.model.segmentEnum.AngleType;
import wrestling.model.segmentEnum.SegmentType;
import wrestling.model.segmentEnum.TeamType;

public class SegmentView {

    private List<SegmentTeam> teams;
    private MatchRule rules;
    private MatchFinish finish;
    private AngleType angleType;
    private LocalDate date;
    private Segment segment;
    private Title title;
    private final SegmentType segmentType;

    public SegmentView(SegmentType segmentType) {
        this.segmentType = segmentType;
        rules = MatchRule.DEFAULT;
        finish = MatchFinish.CLEAN;
        teams = new ArrayList<>();
    }

    public List<Worker> getWorkers() {
        List<Worker> workers = new ArrayList<>();
        for (SegmentTeam team : teams) {
            workers.addAll(team.getWorkers());
        }
        return workers;
    }

    /**
     * @return the teams
     */
    public List<SegmentTeam> getTeams() {
        return teams;
    }

    public List<SegmentTeam> getMatchParticipants() {
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

    public List<SegmentTeam> getTeams(TeamType type) {
        List<SegmentTeam> defaultTeams = new ArrayList<>();
        for (SegmentTeam team : teams) {
            if (team.getType().equals(type)) {
                defaultTeams.add(team);
            }
        }
        return defaultTeams;
    }

    /**
     * @param teams the teams to set
     */
    public void setTeams(List<SegmentTeam> teams) {
        this.teams = teams;
    }

    /**
     * @return the rules
     */
    public MatchRule getRules() {
        return rules;
    }

    /**
     * @param rules the rules to set
     */
    public void setRules(MatchRule rules) {
        this.rules = rules;
    }

    /**
     * @return the finish
     */
    public MatchFinish getFinish() {
        return finish;
    }

    /**
     * @param finish the finish to set
     */
    public void setFinish(MatchFinish finish) {
        this.finish = finish;
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
    public Title getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(Title title) {
        this.title = title;
    }

    /**
     * @return the segmentType
     */
    public SegmentType getSegmentType() {
        return segmentType;
    }

    /**
     * @return the angleType
     */
    public AngleType getAngleType() {
        return angleType;
    }

    /**
     * @param angleType the angleType to set
     */
    public void setAngleType(AngleType angleType) {
        this.angleType = angleType;
    }

}
