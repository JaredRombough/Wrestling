package wrestling.model.factory;

import java.io.Serializable;
import wrestling.model.SegmentWorker;
import wrestling.model.interfaces.Segment;
import wrestling.model.manager.DateManager;
import wrestling.model.manager.SegmentManager;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.modelView.SegmentView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.segmentEnum.PresenceType;
import wrestling.model.segmentEnum.SegmentType;
import wrestling.model.segmentEnum.TeamType;
import wrestling.model.utility.ModelUtils;

public class MatchFactory implements Serializable {

    private final SegmentManager matchManager;
    private final DateManager dateManager;

    public MatchFactory(SegmentManager matchManager, DateManager dateManager) {
        this.matchManager = matchManager;
        this.dateManager = dateManager;
    }

    public Segment saveSegment(SegmentView segmentView) {
        setSegmentRatings(segmentView);
        segmentView.setDate(dateManager.today());
        matchManager.addSegmentView(segmentView);
        for (SegmentTeam team : segmentView.getTeams()) {
            for (WorkerView worker : team.getWorkers()) {
                matchManager.addSegmentWorker(new SegmentWorker(segmentView.getSegment(), worker, segmentView.getTeams().indexOf(team)));
            }
        }

        matchManager.addSegment(segmentView.getSegment());
        return segmentView.getSegment();
    }

    private void setSegmentRatings(SegmentView segmentView) {

        int workRatingTotal = 0;
        int crowdRatingTotal = 0;
        int interferenceTotal = 0;

        for (SegmentTeam team : segmentView.getTeams()) {

            if (segmentView.getSegmentType().equals(SegmentType.MATCH)
                    && team.getType().equals(TeamType.INTERFERENCE)) {
                interferenceTotal += getWorkRating(team);
            }

            workRatingTotal += getWorkRating(team);

            for (WorkerView worker : team.getWorkers()) {
                crowdRatingTotal += ModelUtils.getPrioritizedScore(new Integer[]{
                    worker.getPopularity(),
                    worker.getCharisma()
                });
            }

        }

        if (interferenceTotal > 0) {
            int intRating = interferenceTotal
                    / segmentView.getTeams(TeamType.INTERFERENCE).size();
            int workRating = workRatingTotal
                    / (segmentView.getTeams().size()
                    - segmentView.getTeams(TeamType.INTERFERENCE).size());

            segmentView.getSegment().setWorkRating(ModelUtils.getPrioritizedScore(new Integer[]{
                intRating,
                workRating
            }));
        } else {
            segmentView.getSegment().setWorkRating(Math.round(
                    workRatingTotal / segmentView.getTeams().size()));
        }

        segmentView.getSegment().setCrowdRating(Math.round(
                crowdRatingTotal / segmentView.getWorkers().size()));
    }

    private int getWorkRating(SegmentTeam team) {
        int score = 0;
        for (WorkerView worker : team.getWorkers()) {
            switch (team.getType()) {
                case OFFERER:
                case OFFEREE:
                case CHALLENGER:
                case CHALLENGED:
                case ANNOUNCER:
                case AUDIENCE:
                case PROMO: {
                    if (team.getPresence().equals(PresenceType.PRESENT)) {
                        score += ModelUtils.getWeightedScore(new Integer[]{
                            worker.getCharisma()
                        });
                    }
                }
                case PROMO_TARGET: {
                    if (team.getPresence().equals(PresenceType.PRESENT)) {
                        score += ModelUtils.getWeightedScore(new Integer[]{
                            worker.getCharisma()
                        });
                    } else {
                        score += ModelUtils.getWeightedScore(new Integer[]{
                            worker.getPopularity()
                        });
                    }
                }
                break;
                default:
                    score += ModelUtils.getMatchWorkRating(worker);
                    break;
            }
        }

        return (Math.round(score / team.getWorkers().size()));
    }

}
