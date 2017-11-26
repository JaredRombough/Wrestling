package wrestling.model.temp;

import java.util.ArrayList;
import java.util.List;
import wrestling.model.MatchFinishes;
import wrestling.model.MatchRules;
import wrestling.model.interfaces.Segment;
import wrestling.model.Worker;

public class TempSegment implements Segment {

    private List<List<Worker>> teams;
    private MatchRules rules;
    private MatchFinishes finish;

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

}
