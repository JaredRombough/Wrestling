package wrestling.model.factory;

import java.io.Serializable;
import wrestling.model.SegmentItem;
import wrestling.model.SegmentWorker;
import static wrestling.model.constants.GameConstants.BROADCAST_TEAM_DIFF_RATIO;
import static wrestling.model.constants.GameConstants.CREATIVE_DIFF_RATIO;
import static wrestling.model.constants.GameConstants.CROWD_RATING_DIFF_RATIO;
import static wrestling.model.constants.GameConstants.ENTOURAGE_DIFF_RATIO;
import static wrestling.model.constants.GameConstants.PRODUCTION_DIFF_RATIO;
import static wrestling.model.constants.GameConstants.REF_DIFF_RATIO;
import static wrestling.model.constants.GameConstants.ROAD_AGENT_DIFF_RATIO;
import static wrestling.model.constants.GameConstants.TITLE_DIFF_RATIO;
import wrestling.model.interfaces.Segment;
import wrestling.model.manager.DateManager;
import wrestling.model.manager.SegmentManager;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.modelView.SegmentView;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.TitleView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.segmentEnum.PresenceType;
import wrestling.model.segmentEnum.SegmentType;
import wrestling.model.segmentEnum.StaffType;
import wrestling.model.utility.ModelUtils;
import static wrestling.model.utility.StaffUtils.getStaffSkillAverage;

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

    private int getSegmentRating(SegmentView segmentView) {
        int workRatingTotal = 0;

        for (SegmentTeam team : segmentView.getTeams()) {
            workRatingTotal += getWorkRating(team);
        }

        int segmentRating = workRatingTotal / segmentView.getTeams().size();

        if (segmentView.getSegmentType().equals(SegmentType.MATCH)) {
            segmentRating = getSegmentRatingWithMatchModifiers(segmentView, segmentRating);
        } else {
            segmentRating = getSegmentRatingWithAngleModifiers(segmentView, segmentRating);
        }

        return segmentRating;
    }

    private int getSegmentRatingWithAngleModifiers(SegmentView segmentView, int baseRating) {
        baseRating = modifyRating(baseRating,
                getStaffSkillAverage(StaffType.CREATIVE, segmentView.getPromotion()),
                CREATIVE_DIFF_RATIO);

        return baseRating;
    }

    private int getSegmentRatingWithMatchModifiers(SegmentView segmentView, int baseRating) {
        baseRating = modifyRating(baseRating,
                getStaffSkillAverage(StaffType.REFEREE, segmentView.getPromotion()),
                REF_DIFF_RATIO);
        baseRating = modifyRating(baseRating,
                getStaffSkillAverage(StaffType.ROAD_AGENT, segmentView.getPromotion()),
                ROAD_AGENT_DIFF_RATIO);

        return baseRating;
    }

    private int getMatchCrowdRating(SegmentView segmentView) {
        int teamCount = 0;
        int totalPop = 0;

        for (SegmentTeam team : segmentView.getTeams()) {
            int teamPopTotal = 0;
            for (WorkerView worker : team.getWorkers()) {
                teamPopTotal += worker.getPopularity();
            }

            int teamPop = team.getWorkers().isEmpty() ? 0 : teamPopTotal / team.getWorkers().size();

            if (!team.getEntourage().isEmpty()) {
                int entouragePopTotal = 0;
                for (WorkerView entourage : team.getEntourage()) {
                    entouragePopTotal += entourage.getPopularity();
                }
                int entourageAvg = entouragePopTotal / team.getEntourage().size();
                teamPop += modifyRating(teamPop, entourageAvg, ENTOURAGE_DIFF_RATIO);
            }

            totalPop += teamPop;
            teamCount++;
        }

        int crowdRating = totalPop / teamCount;

        if (!segmentView.getTitleViews().isEmpty()) {
            int titleTotal = 0;
            for (TitleView title : segmentView.getTitleViews()) {
                titleTotal += title.getPrestige();
            }

            int titleAvg = titleTotal / segmentView.getTitleViews().size();
            crowdRating = modifyRating(crowdRating, titleAvg, TITLE_DIFF_RATIO);
        }

        crowdRating = modifyRating(crowdRating,
                getStaffSkillAverage(StaffType.CREATIVE, segmentView.getPromotion()),
                CREATIVE_DIFF_RATIO);
        crowdRating = modifyRating(crowdRating,
                getStaffSkillAverage(StaffType.PRODUCTION, segmentView.getPromotion()),
                PRODUCTION_DIFF_RATIO);

        return crowdRating;
    }

    private int modifyRating(int base, int modifier, int ratio) {
        int diff = modifier - base;
        base += (diff / ratio);
        return base;
    }

    private void setSegmentRatings(SegmentView segmentView) {
        int workRating = getSegmentRating(segmentView);
        segmentView.getSegment().setWorkRating(workRating);

        int crowdRating = modifyRating(getMatchCrowdRating(segmentView), workRating, CROWD_RATING_DIFF_RATIO);

        if (!segmentView.getBroadcastTeam().isEmpty()) {
            int broadCastTeamTotal = 0;
            for (SegmentItem item : segmentView.getBroadcastTeam()) {
                if (item instanceof StaffView) {
                    broadCastTeamTotal += ((StaffView) item).getSkill();
                } else if (item instanceof WorkerView) {
                    broadCastTeamTotal += ModelUtils.getWeightedScore(new Integer[]{
                        ((WorkerView) item).getCharisma(),
                        ((WorkerView) item).getPopularity()
                    });
                }
            }

            crowdRating = modifyRating(crowdRating,
                    broadCastTeamTotal / segmentView.getBroadcastTeam().size(),
                    BROADCAST_TEAM_DIFF_RATIO);
        }

        segmentView.getSegment().setCrowdRating(crowdRating);
    }

    private int getWorkRating(SegmentTeam team) {
        if (team.getWorkers().isEmpty()) {
            return 0;
        }

        int totalTeamScore = 0;
        int entourageTotalScore = 0;

        for (WorkerView worker : team.getWorkers()) {
            switch (team.getType()) {
                case OFFERER:
                case OFFEREE:
                case CHALLENGER:
                case CHALLENGED:
                case ANNOUNCER:
                case AUDIENCE:
                case PROMO: {
                    totalTeamScore += worker.getCharisma();
                }
                case PROMO_TARGET: {
                    if (team.getPresence().equals(PresenceType.PRESENT)) {
                        totalTeamScore += worker.getCharisma();
                    } else {
                        totalTeamScore += worker.getPopularity();
                    }
                }
                break;
                default:
                    totalTeamScore += ModelUtils.getMatchWorkRating(worker);
                    break;
            }
        }

        for (WorkerView worker : team.getEntourage()) {
            switch (team.getType()) {
                case OFFERER:
                case OFFEREE:
                case CHALLENGER:
                case CHALLENGED:
                case ANNOUNCER:
                case AUDIENCE:
                case PROMO: {
                    entourageTotalScore += worker.getCharisma();
                }
                case PROMO_TARGET: {
                    if (team.getPresence().equals(PresenceType.PRESENT)) {
                        entourageTotalScore += worker.getCharisma();
                    } else {
                        entourageTotalScore += worker.getPopularity();
                    }
                }
                break;
                default:
                    break;
            }
        }

        if (!team.getEntourage().isEmpty() && entourageTotalScore > 0) {
            int entourageAvg = entourageTotalScore / team.getEntourage().size();
            totalTeamScore = modifyRating(totalTeamScore, entourageAvg, ENTOURAGE_DIFF_RATIO);
        }
        return totalTeamScore / team.getWorkers().size();
    }

}
