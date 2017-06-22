package wrestling.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import wrestling.model.factory.TitleFactory;
import wrestling.model.utility.UtilityFunctions;

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
        
        for (List<Worker> team : teams) {
            allWorkersList.addAll(team);
        }
        
        return allWorkersList;
    }
    @Override
    public int segmentRating() {
        return matchRating;
    }

    public void setWinner(int winnerIndex) {
        if (winnerIndex < teams.size()) {
            setWinner(teams.get(winnerIndex));
        }

    }

    public void setWinner(List<Worker> winningTeam) {
        if (teams.contains(winningTeam)) {
            this.winner = winningTeam;
            Collections.swap(teams, teams.indexOf(winningTeam), 0);
        }
    }

    @Override
    public boolean isComplete() {

        //consider a match completed if it has any workers (placeholder)
        return !allWorkers().isEmpty() && !getWinner().isEmpty();
    }

    private boolean isHandicapMatch() {
        boolean handicap = false;
        int size = teams.get(0).size();
        for (List<Worker> team : teams) {
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

                if (teams.size() == 2) {

                    switch (teams.get(0).size()) {
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
                    string += teams.size() + "-Way Match\n";
                }
            } else {
                string += rules.get(0).description() + " Match\n";
            }

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

        for (List<Worker> team : teams) {
            float rating = 0;
            for (Worker worker : team) {

                rating += (worker.getFlying() + worker.getStriking() + worker.getFlying()) / 3;

            }
            ratingsTotal += rating;

        }

        matchRating = Math.round(ratingsTotal / teams.size());

    }

    @Override
    public String processSegment(LocalDate date, TitleFactory titleFactory) {
        
        StringBuilder sb = new StringBuilder();
        
        if (title != null) {

            if (title.isVacant()) {

                titleFactory.awardTitle(title, winner, date);
                sb.append(winner.toString()).append(" wins the vacant  ").append(title.getName()).append(" title");
            } else {
                for (Worker worker : title.getWorkers()) {
                    if (!winner.contains(worker)) {
                        sb.append(winner.toString()).append(" defeats ").append(title.getWorkers().toString()).append(" for the ").append(title.getName()).append(" title");
                        titleFactory.titleChange(title, winner, date);

                        break;
                    }

                    sb.append(winner.toString()).append(" defends the  ").append(title.getName()).append(" title");
                }
            }
        }
        int winnerPop = 0;

        //calculate the average popularity of the winning team
        //but should it be max popularity?
        for (Worker worker : getWinner()) {
            winnerPop += worker.getPopularity();
        }

        winnerPop = winnerPop / getWinner().size();

        for (List<Worker> team : teams) {

            if (!team.equals(getWinner())) {
                int teamPop = 0;

                for (Worker worker : team) {
                    teamPop += worker.getPopularity();
                }

                teamPop = teamPop / getWinner().size();

                if (teamPop > winnerPop) {
                    for (Worker worker : getWinner()) {
                        worker.gainPopularity();
                    }

                    for (Worker worker : team) {
                        if (UtilityFunctions.randRange(1, 3) == 1) {
                            worker.losePopularity();
                        }

                    }
                } else {
                    for (Worker worker : getWinner()) {
                        if (UtilityFunctions.randRange(1, 3) == 1) {
                            worker.gainPopularity();
                        }
                    }
                }

            }
        }

        //process injuries
        for (Worker w : allWorkers()) {
            //check worker against injury rule for the match type
            //would need some exceptions if valets or guest refs or whatever are involved
            //for a match we can filter nonparticipants here
            //pass participants
            //and they come back with specific injuries? eventually
            //for an angle we can pass different workers to different functions based on role?
            //have a 'bump' function
            //or rather than have a million different injury methods, just have a
            //dangerousness rating and a list of injury types

        }
        
        return sb.toString().isEmpty() ? toString().replace("\n", " ") : sb.toString();
    }
}
