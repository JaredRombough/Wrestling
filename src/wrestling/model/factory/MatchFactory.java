package wrestling.model.factory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wrestling.model.SegmentWorker;
import wrestling.model.interfaces.Segment;
import wrestling.model.manager.DateManager;
import wrestling.model.manager.SegmentManager;
import wrestling.model.modelView.PromotionView;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.modelView.SegmentView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.segmentEnum.PresenceType;
import wrestling.model.segmentEnum.SegmentType;
import wrestling.model.segmentEnum.StaffType;
import wrestling.model.segmentEnum.TeamType;
import wrestling.model.utility.ModelUtils;
import wrestling.model.utility.StaffUtils;

public class MatchFactory implements Serializable {

    private final int REF_DIFF_RATIO = 10;
    private final int ROAD_AGENT_DIFF_RATIO = 10;
    private final int ENTOURAGE_DIFF_RATIO = 5;
    private final int CROWD_RATING_DIFF_RATIO = 5;

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

    private int getMatchRating(SegmentView segmentView) {
        Map<TeamType, List<WorkerView>> segmentTeamsMap = getMap(segmentView.getTeams());
        Map<TeamType, Integer> teamAvgs = new HashMap<>();
        segmentTeamsMap.forEach((type, workers) -> {
            double total = 0;
            for (WorkerView w : workers) {
                total += ModelUtils.getMatchWorkRating(w);
            }
            Integer avg = (int) total / workers.size();
            teamAvgs.put(type, avg);
        });

        List<TeamType> matchTypes = Arrays.asList(TeamType.WINNER, TeamType.LOSER, TeamType.DRAW);

        int baseMatchRatingTotal = 0;
        int count = 0;

        for (TeamType type : matchTypes) {
            if (teamAvgs.containsKey(type)) {
                baseMatchRatingTotal += teamAvgs.get(type);
                count++;
            }
        }
        int baseMatchRating = baseMatchRatingTotal / count;

        int refScore = segmentView.getReferee().getSkill();
        int refDiff = refScore - baseMatchRating;
        int refModified = baseMatchRating += (refDiff / REF_DIFF_RATIO);

        int roadAgentModifier = StaffUtils.getStaffSkillModifier(StaffType.ROAD_AGENT, segmentView.getPromotion());
        int roadAgentDiff = roadAgentModifier - baseMatchRating;
        int roadAgentModified = refModified += (roadAgentDiff / ROAD_AGENT_DIFF_RATIO);

        return roadAgentModified;
    }

    private int getMatchCrowdRating(SegmentView segmentView) {
        int teamCount = 0;
        int totalPop = 0;

        for (SegmentTeam team : segmentView.getTeams()) {
            int teamPopTotal = 0;
            int entouragePopTotal = 0;
            for (WorkerView worker : team.getWorkers()) {

                for (WorkerView entourage : team.getEntourage()) {
                    entouragePopTotal += entourage.getPopularity();
                }
                teamPopTotal += worker.getPopularity();
            }

            int teamPop = team.getWorkers().isEmpty() ? 0 : teamPopTotal / team.getWorkers().size();

            if (!team.getEntourage().isEmpty()) {
                int entourageAvg = entouragePopTotal / team.getEntourage().size();
                int diff = entourageAvg - teamPop;

                teamPop += (diff / ENTOURAGE_DIFF_RATIO);
            }
            totalPop += teamPop;
            teamCount++;
        }

        return totalPop / teamCount;
    }

    private Map<TeamType, List<WorkerView>> getMap(List<SegmentTeam> teams) {
        Map<TeamType, List<WorkerView>> segmentTeams = new HashMap<>();

        for (SegmentTeam team : teams) {
            if (segmentTeams.get(team.getType()) == null) {
                segmentTeams.put(team.getType(), new ArrayList<>());
            }

            segmentTeams.get(team.getType()).addAll(team.getWorkers());

            if (!team.getEntourage().isEmpty()) {
                if (segmentTeams.get(TeamType.ENTOURAGE) == null) {
                    segmentTeams.put(TeamType.ENTOURAGE, new ArrayList<>());
                }
                segmentTeams.get(TeamType.ENTOURAGE).addAll(team.getEntourage());
            }

        }

        return segmentTeams;
    }

    private void setSegmentRatings(SegmentView segmentView) {

        int workRatingTotal = 0;
        int crowdRatingTotal = 0;
        int interferenceTotal = 0;

        if (segmentView.getSegmentType().equals(SegmentType.MATCH)) {
            int rating = getMatchRating(segmentView);
            segmentView.getSegment().setWorkRating(rating);
            int crowdRating = getMatchCrowdRating(segmentView);
            int diff = rating - crowdRating;
            crowdRating += (diff / CROWD_RATING_DIFF_RATIO);
            segmentView.getSegment().setCrowdRating(crowdRating);
            return;
        }

        for (SegmentTeam team : segmentView.getTeams()) {

            if (segmentView.getSegmentType().equals(SegmentType.MATCH)
                    && team.getType().equals(TeamType.INTERFERENCE)) {
                interferenceTotal += getWorkRating(segmentView.getPromotion(), team);
            }

            workRatingTotal += getWorkRating(segmentView.getPromotion(), team);

            for (WorkerView worker : team.getWorkers()) {
                crowdRatingTotal += ModelUtils.getPrioritizedScore(new Integer[]{
                    worker.getPopularity(),
                    worker.getCharisma()
                });
            }

        }

        if (segmentView.getSegmentType().equals(SegmentType.MATCH)) {
            if (segmentView.getReferee() == null) {
                workRatingTotal *= .5;
            } else {
                workRatingTotal += segmentView.getReferee().getSkill() / 10;
            }
        }

        int finalMatchRating;

        if (interferenceTotal > 0) {
            int intRating = interferenceTotal
                    / segmentView.getTeams(TeamType.INTERFERENCE).size();
            int workRating = workRatingTotal
                    / (segmentView.getTeams().size()
                    - segmentView.getTeams(TeamType.INTERFERENCE).size());

            finalMatchRating = ModelUtils.getPrioritizedScore(new Integer[]{
                intRating,
                workRating
            });
        } else {
            finalMatchRating = Math.round(workRatingTotal / segmentView.getTeams().size());
        }

        if (!segmentView.getBroadcastTeam().isEmpty()) {
            finalMatchRating += finalMatchRating * StaffUtils.getBroadcastTeamMatchRatingModifier(segmentView.getBroadcastTeam());
        }

        segmentView.getSegment().setWorkRating(finalMatchRating);

        int crowdRating = Math.round(crowdRatingTotal / segmentView.getWorkers().size());

        crowdRating += crowdRating * StaffUtils.getCombinedCrowdRatingModifier(segmentView.getPromotion());

        segmentView.getSegment().setCrowdRating(crowdRating);
    }

    private int getWorkRating(PromotionView promotion, SegmentTeam team) {
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
                        score += getAngleRatingModified(promotion, worker.getCharisma());
                    }
                }
                case PROMO_TARGET: {
                    if (team.getPresence().equals(PresenceType.PRESENT)) {
                        score += getAngleRatingModified(promotion, worker.getCharisma());
                    } else {
                        score += getAngleRatingModified(promotion, worker.getPopularity());
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

    private int getAngleRatingModified(PromotionView promotion, int rating) {
        int baseScore = ModelUtils.getWeightedScore(new Integer[]{
            rating
        });
        return baseScore += baseScore + StaffUtils.getAngleRatingModifier(promotion);
    }

}
