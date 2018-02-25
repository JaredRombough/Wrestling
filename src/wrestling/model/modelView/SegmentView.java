package wrestling.model.modelView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.MatchFinishes;
import wrestling.model.MatchRules;
import wrestling.model.Worker;
import wrestling.model.interfaces.Segment;

public class SegmentView {

    private List<List<Worker>> teams;
    private MatchRules rules;
    private MatchFinishes finish;
    private LocalDate date;
    private Segment segment;

    public List<Worker> getWorkers() {
        List<Worker> workers = new ArrayList<>();
        for (List<Worker> team : teams) {
            workers.addAll(team);
        }
        return workers;
    }

    /**
     * @return the teams
     */
    public List<List<Worker>> getTeams() {
        return teams;
    }

    /**
     * @param teams the teams to set
     */
    public void setTeams(List<List<Worker>> teams) {
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

}
