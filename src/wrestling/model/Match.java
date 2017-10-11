package wrestling.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Match extends Segment implements Serializable {

    private List<List<Worker>> teams = new ArrayList<>();

    private List<Worker> teamA = new ArrayList<>();

    private List<Worker> winner;

    private Title title;

    private List<MatchRules> rules = new ArrayList<>();
    private List<MatchFinishes> finish = new ArrayList<>();

    private final List<Worker> teamB = new ArrayList<>();

    private boolean hasTeams;

    private int matchRating;

    /*
    this constructor takes an arbitrary number of teams
     */
    public Match(final List<List<Worker>> teams) {

        this.hasTeams = false;

        if (teams.size() == 1) {
            this.winner = teams.get(0);
            this.hasTeams = false;

        } else if (teams.size() > 1) {
            this.winner = teams.get(0);
            this.teams.addAll(teams);
            this.hasTeams = true;
            calculateMatchRating();
        }

        finish.add(MatchFinishes.CLEAN);
        rules.add(MatchRules.DEFAULT);

    }

    public Match(final List<List<Worker>> teams, final List<MatchRules> rules, final List<MatchFinishes> finish) {
        this(teams);
        this.rules = rules;
        this.finish = finish;
    }

    public Match(final List<List<Worker>> teams, final List<MatchRules> rules, final List<MatchFinishes> finish, Title title) {
        this(teams, title);
        this.rules = rules;
        this.finish = finish;
    }

    public Match(final List<List<Worker>> teams, Title title) {

        this.title = title;
        this.hasTeams = false;

        if (teams.size() == 1) {
            this.winner = teams.get(0);
            this.hasTeams = false;

        } else if (teams.size() > 1) {
            this.winner = teams.get(0);
            this.teams.addAll(teams);
            this.hasTeams = true;
            calculateMatchRating();
        }

        finish.add(MatchFinishes.CLEAN);
        rules.add(MatchRules.DEFAULT);

    }

    public List<Worker> teamA() {
        return teamA;
    }

    public List<Worker> teamB() {
        return teamB;
    }

    @Override
    public List<Worker> allWorkers() {
        List<Worker> allWorkersList = new ArrayList<>();

        for (List<Worker> team : getTeams()) {
            allWorkersList.addAll(team);
        }

        return allWorkersList;
    }

    @Override
    public int segmentRating() {
        return matchRating;
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

    private boolean isHandicapMatch() {
        boolean handicap = false;
        int size = getTeams().get(0).size();
        for (List<Worker> team : getTeams()) {
            if (team.size() != size && !team.isEmpty()) {
                handicap = true;
                break;

            }
        }
        return handicap;
    }

    @Override
    public String toString() {

        String string = new String();

        if (this.hasTeams) {

            if (isHandicapMatch()) {
                string += "Handicap Match\n";

            } else if (rules.get(0).equals(MatchRules.DEFAULT)) {

                if (getTeams().size() == 2) {

                    switch (getTeams().get(0).size()) {
                        case 1:
                            string += "Singles Match\n";
                            break;
                        case 2:
                            string += "Tag Team Match\n";
                            break;
                        case 3:
                            string += "Six Man Tag Team Match\n";
                            break;
                        case 4:
                            string += "Eight Man Tag Team Match\n";
                            break;
                        case 5:
                            string += "Ten Man Tag Team Match\n";
                            break;
                        default:
                            break;
                    }

                } else {
                    string += getTeams().size() + "-Way Match\n";
                }
            } else {
                string += rules.get(0).description() + " Match\n";
            }

            for (int t = 0; t < getTeams().size(); t++) {
                List<Worker> team = getTeams().get(t);

                for (int i = 0; i < team.size(); i++) {
                    string += team.get(i).getShortName();

                    if (team.size() > 1 && i < team.size() - 1) {
                        string += "/";
                    }

                }

                if (t == 0 && !string.isEmpty()) {
                    string += " def. ";

                } else if (t < getTeams().size() - 1 && !string.isEmpty()) {
                    string += ", ";
                }

            }

            switch (finish.get(0)) {
                case COUNTOUT:
                    string += " by Countout";
                    break;
                case DQINTERFERENCE:
                case DQ:
                    string += " by DQ";
                    break;
                default:
                    break;

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

        for (List<Worker> team : getTeams()) {
            float rating = 0;
            for (Worker worker : team) {

                rating += (worker.getFlying() + worker.getStriking() + worker.getFlying()) / 3;

            }
            ratingsTotal += rating;

        }

        matchRating = Math.round(ratingsTotal / getTeams().size());

    }

    /**
     * @return the title
     */
    public Title getTitle() {
        return title;
    }

    /**
     * @return the teams
     */
    public List<List<Worker>> getTeams() {
        return teams;
    }

}
