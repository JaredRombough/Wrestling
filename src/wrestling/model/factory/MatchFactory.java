package wrestling.model.factory;

import java.io.Serializable;
import java.util.List;
import wrestling.model.Match;
import wrestling.model.MatchFinishes;
import wrestling.model.MatchRules;
import wrestling.model.MatchTitle;
import wrestling.model.MatchWorker;
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.model.manager.MatchManager;

public class MatchFactory implements Serializable {

    private final MatchManager matchManager;

    public MatchFactory(MatchManager matchManager) {
        this.matchManager = matchManager;
    }

    /*
    this constructor takes an arbitrary number of teams
     */
    public Match CreateMatch(final List<List<Worker>> teams) {

        int rating = 0;
        if (teams.size() > 1) {
            rating = calculateMatchRating(teams);
        }

        Match match = new Match(MatchRules.DEFAULT, MatchFinishes.CLEAN, rating);

        for (List<Worker> team : teams) {
            for (Worker worker : team) {
                matchManager.addMatchWorker(new MatchWorker(match, worker, teams.indexOf(team)));
            }
        }

        return match;

    }

    public Match CreateMatch(final List<List<Worker>> teams, final MatchRules rules, final MatchFinishes finish) {
        int rating = 0;
        if (teams.size() > 1) {
            rating = calculateMatchRating(teams);
        }

        Match match = new Match(rules, finish, rating);

        for (List<Worker> team : teams) {
            for (Worker worker : team) {
                matchManager.addMatchWorker(new MatchWorker(match, worker, teams.indexOf(team)));
            }
        }

        return match;
    }

    public Match CreateMatch(final List<List<Worker>> teams, Title title) {

        int rating = 0;
        if (teams.size() > 1) {
            rating = calculateMatchRating(teams);
        }

        Match match = new Match(MatchRules.DEFAULT, MatchFinishes.CLEAN, rating);

        for (List<Worker> team : teams) {
            for (Worker worker : team) {
                matchManager.addMatchWorker(new MatchWorker(match, worker, teams.indexOf(team)));
            }
        }

        matchManager.addMatchTitle(new MatchTitle(match, title));

        return match;

    }

    /*
    @Override
    public List<Worker> allWorkers() {
        List<Worker> allWorkersList = new ArrayList<>();

        for (List<Worker> team : getTeams()) {
            allWorkersList.addAll(team);
        }

        return allWorkersList;
    }

    public void setWinner(int winnerIndex) {
        if (winnerIndex < getTeams().size()) {
            setWinner(getTeams().get(winnerIndex));
        }

    }

    public void setWinner(List<Worker> winningTeam) {
        if (getTeams().contains(winningTeam)) {
            this.winner = winningTeam;
            Collections.swap(getTeams(), getTeams().indexOf(winningTeam), 0);
        }
    }

    @Override
    public boolean isComplete() {
        //consider a match completed if it has any workers (placeholder)
        return !allWorkers().isEmpty() && !getWinner().isEmpty();
    }

    

    public List<Worker> getWinner() {

        return this.winner;
    }
     */
    private int calculateMatchRating(List<List<Worker>> teams) {

        float ratingsTotal = 0;

        for (List<Worker> team : teams) {
            float rating = 0;
            for (Worker worker : team) {

                rating += (worker.getFlying() + worker.getStriking() + worker.getFlying()) / 3;

            }
            ratingsTotal += rating;

        }

        return Math.round(ratingsTotal / teams.size());

    }

}
