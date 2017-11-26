package wrestling.model;

public class MatchWorker {

    private final Match match;
    private final Worker worker;
    
    private final int team;
    private boolean winner;
    private boolean interfering;
    private boolean manager;
    private int target;
    private boolean entourage;

    public MatchWorker(Match match,
            Worker worker,
            int team) {
        this.match = match;
        this.worker = worker;
        this.team = team;
    }

    /**
     * @return the match
     */
    public Match getMatch() {
        return match;
    }

    /**
     * @return the worker
     */
    public Worker getWorker() {
        return worker;
    }

    /**
     * @return the team
     */
    public int getTeam() {
        return team;
    }

}
