package wrestling.model.modelView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.MatchFinishes;
import wrestling.model.MatchRules;
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.model.interfaces.Segment;
import wrestling.view.event.TeamType;

public class SegmentView {

    private List<SegmentTeam> teams;
    private MatchRules rules;
    private MatchFinishes finish;
    private LocalDate date;
    private Segment segment;
    private Title title;

    public SegmentView() {
        rules = MatchRules.DEFAULT;
        finish = MatchFinishes.CLEAN;
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
    public MatchRules getRules() {
        return rules;
    }

    /**
     * @param rules the rules to set
     */
    public void setRules(MatchRules rules) {
        this.rules = rules;
    }

    /**
     * @return the finish
     */
    public MatchFinishes getFinish() {
        return finish;
    }

    /**
     * @param finish the finish to set
     */
    public void setFinish(MatchFinishes finish) {
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

}
