package wrestling.model.factory;

import java.io.Serializable;
import java.util.List;
import wrestling.model.Match;
import wrestling.model.segmentEnum.MatchFinish;
import wrestling.model.segmentEnum.MatchRule;
import wrestling.model.MatchTitle;
import wrestling.model.MatchWorker;
import wrestling.model.Title;
import wrestling.model.Worker;
import wrestling.model.manager.DateManager;
import wrestling.model.manager.MatchManager;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.modelView.SegmentView;
import wrestling.model.segmentEnum.SegmentType;

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
    public Match processMatch(final List<SegmentTeam> teams) {
        Match match = new Match(MatchRule.DEFAULT, MatchFinish.CLEAN, calculateMatchRating(teams));
        saveMatch(match, teams);
        return match;
    }

    public Match processMatch(final List<SegmentTeam> teams, final MatchRule rules, final MatchFinish finish) {
        Match match = new Match(rules, finish, calculateMatchRating(teams));
        saveMatch(match, teams);
        return match;
    }

    public Match processMatch(final List<SegmentTeam> teams, Title title) {
        Match match = new Match(MatchRule.DEFAULT, MatchFinish.CLEAN, calculateMatchRating(teams));
        saveMatch(match, teams, title);
        return match;

    }

    private void saveMatch(Match match, List<SegmentTeam> teams, Title title) {
        matchManager.addMatchTitle(new MatchTitle(match, title));
        saveMatch(match, teams);
    }

    private void saveMatch(Match match, List<SegmentTeam> teams) {
        SegmentView segmentView = new SegmentView(SegmentType.MATCH);
        segmentView.setFinish(match.getFinish());
        segmentView.setRules(match.getRules());
        segmentView.setTeams(teams);
        segmentView.setDate(dateManager.today());
        matchManager.addSegmentView(segmentView);

        for (SegmentTeam team : teams) {
            for (Worker worker : team.getWorkers()) {
                matchManager.addMatchWorker(new MatchWorker(match, worker, teams.indexOf(team)));
            }
        }

        matchManager.addMatch(match);
    }

    private int calculateMatchRating(List<SegmentTeam> teams) {

        if (teams.size() < 1) {
            return 0;
        }

        float ratingsTotal = 0;

        for (SegmentTeam team : teams) {
            float rating = 0;
            for (Worker worker : team.getWorkers()) {

                rating += (worker.getFlying() + worker.getStriking() + worker.getFlying()) / 3;

            }
            ratingsTotal += rating;

        }

        return Math.round(ratingsTotal / teams.size());

    }

}
