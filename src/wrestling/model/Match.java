package wrestling.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 
 */
public class Match extends Segment implements Serializable {

    private List<List<Worker>> teams = new ArrayList<List<Worker>>();

    private List<Worker> teamA;
    
    private List<Worker> winner;

    public List<Worker> teamA() {
        return teamA;
    }
    private List<Worker> teamB;

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
    
    private boolean isDraw;
    private int matchRating;

    public int segmentRating() {
        return matchRating;
    }

    /*
    get rid of this constructor eventually? it only handles two teams
    */
    public Match(final List<Worker> teamA, final List<Worker> teamB, boolean isDraw) {
        this.teamA = teamA;
        this.teamB = teamB;
        this.isDraw = isDraw;
        if (teamA.size() != 0 && teamB.size() != 0) {
            calculateMatchRating();
        }
        this.teams = new ArrayList<List<Worker>>();
        this.teams.add(teamA);
        this.teams.add(teamB);

    }

    /*
    this is the more flexible constructor, takes an arbitrary number of teams
    */
    public Match(final List<List<Worker>> teams) {
        
        
        if (teams.size() <= 1) {
            System.out.println("tried to make a match with not enough teams");

        } else if (teams.size() > 1) {
            this.winner = teams.get(0);
            this.teams.addAll(teams);
            calculateMatchRating();
        }
        
    }

    @Override
    public boolean isComplete() {
        //make sure we have two teams in order to have a match
        //this may become more complex
        return (teams.size() > 1);
    }

    @Override
    public String toString() {

        String string = new String();

        for (int t = 0; t < teams.size(); t++) {
            List<Worker> team = teams.get(t);

            for (int i = 0; i < team.size(); i++) {
                string += team.get(i).getShortName();
                if (team.size() > 1 && i < team.size() - 1) {
                    string += "/";
                }

            }

            if (t == 0) {
                string += " def. ";

            }

        }
        
        return string;
    }

  
    public List<Worker> getWinner() {

        return this.teamA;
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
