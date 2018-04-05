package wrestling.model.factory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import wrestling.model.SegmentWorker;
import wrestling.model.Worker;
import wrestling.model.interfaces.Segment;
import wrestling.model.manager.DateManager;
import wrestling.model.manager.SegmentManager;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.modelView.SegmentView;
import wrestling.model.segmentEnum.SegmentType;

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
            for (Worker worker : team.getWorkers()) {
                matchManager.addSegmentWorker(new SegmentWorker(segmentView.getSegment(), worker, segmentView.getTeams().indexOf(team)));
            }
        }

        matchManager.addSegment(segmentView.getSegment());
        return segmentView.getSegment();
    }

    private void setSegmentRatings(SegmentView segmentView) {
        List<Worker> workers = segmentView.getMatchParticipants();

        float workRatingTotal = 0;
        float crowdRatingTotal = 0;

        for (Worker worker : workers) {
            if (segmentView.getSegmentType().equals(SegmentType.MATCH)) {
                workRatingTotal += getMatchWorkRating(worker);
            } else {
                workRatingTotal += getAngleWorkRating(worker);
            }

            crowdRatingTotal += getPrioritizedScore(new Integer[]{
                worker.getPopularity(),
                worker.getCharisma()
            });
        }

        segmentView.getSegment().setWorkRating(Math.round(workRatingTotal / workers.size()));
        segmentView.getSegment().setCrowdRating(Math.round(crowdRatingTotal / workers.size()));
    }

    public int getMatchWorkRating(Worker worker) {
        return getWeightedScore(new Integer[]{
            worker.getFlying(),
            worker.getWrestling(),
            worker.getStriking(),
            worker.getCharisma()
        });
    }

    private int getAngleWorkRating(Worker worker) {
        return getWeightedScore(new Integer[]{
            worker.getCharisma()
        });
    }

    private int getWeightedScore(Integer[] attributes) {
        Arrays.sort(attributes, Collections.reverseOrder());

        return getPrioritizedScore(attributes);
    }

    private int getPrioritizedScore(Integer[] attributes) {
        int totalScore = 0;

        for (int i = 0; i < attributes.length; i++) {
            totalScore += (attributes[i] * (attributes.length - i));
        }

        int denominator = (attributes.length * (attributes.length + 1)) / 2;

        return totalScore / denominator;
    }

}
