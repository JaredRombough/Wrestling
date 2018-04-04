package wrestling.model.factory;

import java.io.Serializable;
import java.util.List;
import wrestling.model.SegmentWorker;
import wrestling.model.Worker;
import wrestling.model.interfaces.Segment;
import wrestling.model.manager.DateManager;
import wrestling.model.manager.SegmentManager;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.modelView.SegmentView;

public class MatchFactory implements Serializable {

    private final SegmentManager matchManager;
    private final DateManager dateManager;

    public MatchFactory(SegmentManager matchManager, DateManager dateManager) {
        this.matchManager = matchManager;
        this.dateManager = dateManager;
    }

    public Segment saveSegment(SegmentView segmentView) {
        segmentView.getSegment().setRating(calculateSegmentRating(segmentView));
        segmentView.setDate(dateManager.today());
        matchManager.addSegmentView(segmentView);
        for (SegmentTeam team : segmentView.getTeams()) {
            for (Worker worker : team.getWorkers()) {
                matchManager.addSegmentWorker(new SegmentWorker(segmentView.getSegment(), worker, segmentView.getTeams().indexOf(team)));
            }
        }

        matchManager.addSegment(segmentView.getSegment());
        return segmentView.getSegment();
    }

    private int calculateSegmentRating(SegmentView segmentView) {
        List<SegmentTeam> teams = segmentView.getTeams();

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
