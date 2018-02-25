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
import wrestling.model.manager.DateManager;
import wrestling.model.manager.MatchManager;
import wrestling.model.modelView.SegmentView;

public class MatchFactory implements Serializable {

    private final MatchManager matchManager;
    private final DateManager dateManager;

    public MatchFactory(MatchManager matchManager, DateManager dateManager) {
        this.matchManager = matchManager;
        this.dateManager = dateManager;
    }

    /*
    this constructor takes an arbitrary number of teams
     */
    public Match processMatch(final List<List<Worker>> teams) {
        Match match = new Match(MatchRules.DEFAULT, MatchFinishes.CLEAN, calculateMatchRating(teams));
        saveMatch(match, teams);
        return match;
    }

    public Match processMatch(final List<List<Worker>> teams, final MatchRules rules, final MatchFinishes finish) {
        Match match = new Match(rules, finish, calculateMatchRating(teams));
        saveMatch(match, teams);
        return match;
    }

    public Match processMatch(final List<List<Worker>> teams, Title title) {
        Match match = new Match(MatchRules.DEFAULT, MatchFinishes.CLEAN, calculateMatchRating(teams));
        saveMatch(match, teams, title);
        return match;

    }

    private void saveMatch(Match match, List<List<Worker>> teams, Title title) {
        matchManager.addMatchTitle(new MatchTitle(match, title));
        saveMatch(match, teams);
    }

    private void saveMatch(Match match, List<List<Worker>> teams) {
        SegmentView segmentView = new SegmentView();
        segmentView.setFinish(match.getFinish());
        segmentView.setRules(match.getRules());
        segmentView.setTeams(teams);
        segmentView.setRating(match.getRating());
        segmentView.setDate(dateManager.today());
        matchManager.addSegmentView(segmentView);

        for (List<Worker> team : teams) {
            for (Worker worker : team) {
                matchManager.addMatchWorker(new MatchWorker(match, worker, teams.indexOf(team)));
            }
        }

        matchManager.addMatch(match);
    }

    private int calculateMatchRating(List<List<Worker>> teams) {

        if (teams.size() < 1) {
            return 0;
        }

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
