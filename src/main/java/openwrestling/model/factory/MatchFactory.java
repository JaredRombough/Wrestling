package openwrestling.model.factory;

import java.io.Serializable;
import java.util.List;

import openwrestling.manager.WorkerManager;
import org.apache.commons.lang3.RandomUtils;
import openwrestling.model.SegmentItem;
import openwrestling.model.SegmentWorker;
import static openwrestling.model.constants.GameConstants.BASE_INJURY_RATE;
import static openwrestling.model.constants.GameConstants.BROADCAST_TEAM_MODIFIER_WEIGHT;
import static openwrestling.model.constants.GameConstants.CREATIVE_MODIFIER_WEIGHT;
import static openwrestling.model.constants.GameConstants.CROWD_RATING_MODIFIER_WEIGHT;
import static openwrestling.model.constants.GameConstants.ENTOURAGE_MODIFIER_WEIGHT;
import static openwrestling.model.constants.GameConstants.MAX_INJURY_DAYS;
import static openwrestling.model.constants.GameConstants.PRODUCTION_MODIFIER_WEIGHT;
import static openwrestling.model.constants.GameConstants.REF_MODIFIER_WEIGHT;
import static openwrestling.model.constants.GameConstants.ROAD_AGENT_MODIFIER_WEIGHT;
import static openwrestling.model.constants.GameConstants.TIME_OVERRUN_PENALTY_WEIGHT;
import static openwrestling.model.constants.GameConstants.TIME_UNDERRUN_PENALTY_WEIGHT;
import static openwrestling.model.constants.GameConstants.TITLE_MODIFIER_WEIGHT;
import openwrestling.model.interfaces.Segment;
import openwrestling.model.manager.DateManager;
import openwrestling.model.manager.InjuryManager;
import openwrestling.model.manager.SegmentManager;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.modelView.SegmentTeam;
import openwrestling.model.modelView.SegmentView;
import openwrestling.model.modelView.StaffView;
import openwrestling.model.modelView.TitleView;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segmentEnum.AngleLength;
import openwrestling.model.segmentEnum.MatchLength;
import openwrestling.model.segmentEnum.MatchRule;
import openwrestling.model.segmentEnum.PresenceType;
import openwrestling.model.segmentEnum.SegmentType;
import openwrestling.model.segmentEnum.StaffType;
import openwrestling.model.utility.ModelUtils;
import openwrestling.model.utility.StaffUtils;
import static openwrestling.model.utility.StaffUtils.getStaffSkillAverage;

public class MatchFactory implements Serializable {

    private final SegmentManager matchManager;
    private final DateManager dateManager;
    private final InjuryManager injuryManager;
    private final WorkerManager workerManager;

    public MatchFactory(SegmentManager matchManager, DateManager dateManager, InjuryManager injuryManager, WorkerManager workerManager) {
        this.matchManager = matchManager;
        this.dateManager = dateManager;
        this.injuryManager = injuryManager;
        this.workerManager = workerManager;
    }

    public Segment saveSegment(SegmentView segmentView) {
        setSegmentRatings(segmentView);
        segmentView.setDate(dateManager.today());
        processInjuries(segmentView);
        matchManager.addSegmentView(segmentView);
        for (SegmentTeam team : segmentView.getTeams()) {
            for (Worker worker : team.getWorkers()) {
                matchManager.addSegmentWorker(new SegmentWorker(segmentView.getSegment(), worker, segmentView.getTeams().indexOf(team)));
            }
        }

        matchManager.addSegment(segmentView.getSegment());
        return segmentView.getSegment();
    }

    private int getSegmentWorkRating(SegmentView segmentView) {
        int workRatingTotal = 0;
        boolean isMatch = segmentView.getSegmentType().equals(SegmentType.MATCH);

        for (SegmentTeam team : segmentView.getTeams()) {
            workRatingTotal += getWorkRating(team, segmentView.getMatchRule());
        }

        int segmentRating = workRatingTotal / segmentView.getTeams().size();

        if (isMatch) {
            segmentRating = getSegmentRatingWithMatchModifiers(segmentView, segmentRating);
        } else {
            segmentRating = getSegmentRatingWithAngleModifiers(segmentView, segmentRating);
        }

        segmentRating = getSegmentRatingWithTimeModifier(segmentView, segmentRating);

        return segmentRating;
    }

    private int getSegmentRatingWithTimeModifier(SegmentView segmentView, int baseRating) {
        int maxTime = segmentView.getSegmentType().equals(SegmentType.MATCH) ? MatchLength.MAXIMUM.value() : AngleLength.MAXIMUM.value();
        int actual = segmentView.getSegment().getSegmentLength() / maxTime * 100;

        int timeDiff = (int) actual - baseRating;

        int penalty = timeDiff > 0 ? TIME_OVERRUN_PENALTY_WEIGHT : TIME_UNDERRUN_PENALTY_WEIGHT;

        return baseRating - Math.abs(timeDiff) / penalty;
    }

    private int getSegmentRatingWithAngleModifiers(SegmentView segmentView, int baseRating) {
        baseRating = modifyRating(baseRating,
                getStaffSkillAverage(StaffType.CREATIVE, segmentView.getPromotion()),
                CREATIVE_MODIFIER_WEIGHT);

        return baseRating;
    }

    private int getSegmentRatingWithMatchModifiers(SegmentView segmentView, int baseRating) {
        baseRating = modifyRating(baseRating,
                getStaffSkillAverage(StaffType.REFEREE, segmentView.getPromotion()),
                REF_MODIFIER_WEIGHT);
        baseRating = modifyRating(baseRating,
                getStaffSkillAverage(StaffType.ROAD_AGENT, segmentView.getPromotion()),
                ROAD_AGENT_MODIFIER_WEIGHT);

        return baseRating;
    }

    private int getMatchCrowdRating(SegmentView segmentView) {
        int teamCount = 0;
        int totalPop = 0;

        for (SegmentTeam team : segmentView.getTeams()) {
            int teamPopTotal = 0;
            for (Worker worker : team.getWorkers()) {
                teamPopTotal += worker.getPopularity();
            }

            int teamPop = team.getWorkers().isEmpty() ? 0 : teamPopTotal / team.getWorkers().size();

            if (!team.getEntourage().isEmpty()) {
                int entouragePopTotal = 0;
                for (Worker entourage : team.getEntourage()) {
                    entouragePopTotal += entourage.getPopularity();
                }
                int entourageAvg = entouragePopTotal / team.getEntourage().size();
                teamPop += modifyRating(teamPop, entourageAvg, ENTOURAGE_MODIFIER_WEIGHT);
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
            crowdRating = modifyRating(crowdRating, titleAvg, TITLE_MODIFIER_WEIGHT);
        }

        crowdRating = modifyRating(crowdRating,
                getStaffSkillAverage(StaffType.CREATIVE, segmentView.getPromotion()),
                CREATIVE_MODIFIER_WEIGHT);
        crowdRating = modifyRating(crowdRating,
                getStaffSkillAverage(StaffType.PRODUCTION, segmentView.getPromotion()),
                PRODUCTION_MODIFIER_WEIGHT);

        return crowdRating;
    }

    private int modifyRating(int base, int modifier, int ratio) {
        int diff = modifier - base;
        base += (diff / ratio);
        return base;
    }

    private void setSegmentRatings(SegmentView segmentView) {
        int workRating = getSegmentWorkRating(segmentView);
        segmentView.getSegment().setWorkRating(workRating);

        int crowdRating = modifyRating(getMatchCrowdRating(segmentView), workRating, CROWD_RATING_MODIFIER_WEIGHT);

        if (!segmentView.getBroadcastTeam().isEmpty()) {
            int broadCastTeamTotal = 0;
            for (SegmentItem item : segmentView.getBroadcastTeam()) {
                if (item instanceof StaffView) {
                    broadCastTeamTotal += ((StaffView) item).getSkill();
                } else if (item instanceof Worker) {
                    broadCastTeamTotal += ModelUtils.getWeightedScore(new Integer[]{
                        ((Worker) item).getCharisma(),
                        ((Worker) item).getPopularity()
                    });
                }
            }

            crowdRating = modifyRating(crowdRating,
                    broadCastTeamTotal / segmentView.getBroadcastTeam().size(),
                    BROADCAST_TEAM_MODIFIER_WEIGHT);
        }

        segmentView.getSegment().setCrowdRating(crowdRating);
    }

    private int getWorkRating(SegmentTeam team, MatchRule matchRule) {
        if (team.getWorkers().isEmpty()) {
            return 0;
        }

        int totalTeamScore = 0;
        int entourageTotalScore = 0;

        for (Worker worker : team.getWorkers()) {
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
                    totalTeamScore += ModelUtils.getMatchWorkRating(worker, matchRule);
                    break;
            }
        }

        for (Worker worker : team.getEntourage()) {
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
            totalTeamScore = modifyRating(totalTeamScore, entourageAvg, ENTOURAGE_MODIFIER_WEIGHT);
        }
        return totalTeamScore / team.getWorkers().size();
    }

    private void processInjuries(SegmentView segmentView) {
        List<Worker> matchWorkers = segmentView.getMatchParticipants();
        matchWorkers.stream().forEach((w) -> {
            Promotion promotion = segmentView.getPromotion();
            int medicModifier = StaffUtils.getStaffSkillModifier(StaffType.MEDICAL, promotion, workerManager.selectRoster(promotion));
            int injuryRate = BASE_INJURY_RATE - segmentView.getMatchRule().getInjuryModifier() * BASE_INJURY_RATE / 100;
            if (RandomUtils.nextInt(0, injuryRate) == 1 && RandomUtils.nextInt(0, injuryRate) > medicModifier) {
                int injuryDays = RandomUtils.nextInt(0, MAX_INJURY_DAYS);
                int duration = injuryDays - (medicModifier / 10);
                if (duration > 0) {
                    injuryManager.createInjury(dateManager.today(), duration, w, segmentView);
                }
            }
        });
    }

}
