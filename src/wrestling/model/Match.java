package wrestling.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Match extends Segment implements Serializable {

    private List<List<Worker>> teams = new ArrayList<List<Worker>>();

    private List<Worker> teamA = new ArrayList<>();

    private List<Worker> winner;

    public List<Worker> teamA() {
        return teamA;
    }
    private List<Worker> teamB = new ArrayList<>();

    public List<Worker> teamB() {
        return teamB;
    }

    @Override
    public List<Worker> allWorkers() {
        List<Worker> allWorkersList = new ArrayList<Worker>();

        for (List<Worker> team : teams) {
            allWorkersList.addAll(team);
        }

        return allWorkersList;
    }

    private boolean hasWinner;
    private boolean hasTeams;

    private int matchRating;

    public int segmentRating() {
        return matchRating;
    }

    public Match(final Worker workerA, final Worker workerB) {
        teamA.add(workerA);
        teamB.add(workerB);
        teams.add(teamA);
        teams.add(teamB);
        this.hasWinner = true;
        this.hasTeams = true;
        this.winner = teamA;
        calculateMatchRating();

    }

    /*
    this constructor takes an arbitrary number of teams
     */
    public Match(final List<List<Worker>> teams) {

        this.hasWinner = false;
        this.hasTeams = false;

        if (teams.size() == 1) {
            this.winner = teams.get(0);
            this.hasWinner = true;
            this.hasTeams = false;

        } else if (teams.size() > 1) {
            this.winner = teams.get(0);
            this.hasWinner = true;
            this.teams.addAll(teams);
            this.hasTeams = true;
            calculateMatchRating();
        }

    }

    @Override
    public boolean isComplete() {

        //consider a match completed if it has any workers (placeholder)
        return !allWorkers().isEmpty();
    }

    @Override
    public String toString() {

        String string = new String();

        if (this.hasTeams) {
            for (int t = 0; t < teams.size(); t++) {
                List<Worker> team = teams.get(t);

                for (int i = 0; i < team.size(); i++) {
                    string += team.get(i).getShortName();
                    if (team.size() > 1 && i < team.size() - 1) {
                        string += "/";
                    }

                }

                if (t == 0 && !string.isEmpty()) {
                    string += " def. ";

                } else if (t < teams.size() - 1 && !string.isEmpty()) {
                    string += ", ";
                }

            }
        } else {
            string += getWinner();
        }
        if (string.isEmpty()) {

            string += "Empty Segment";
        }

        return string;
    }

    public List<Worker> getWinner() {

        return this.winner;
    }

    private void calculateMatchRating() {

        float ratingsTotal = 0;

        for (List<Worker> team : teams) {
            float rating = 0;
            for (Worker worker : team) {

                rating += (worker.getFlying() + worker.getStriking() + worker.getFlying()) / 3;

            }
            ratingsTotal += rating;

        }

        matchRating = Math.round(ratingsTotal / teams.size());

    }

}
