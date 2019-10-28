package openwrestling.model.factory;

import openwrestling.manager.WorkerManager;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.manager.DateManager;
import openwrestling.model.manager.InjuryManager;
import openwrestling.model.manager.SegmentManager;
import openwrestling.model.modelView.Segment;
import openwrestling.model.modelView.SegmentTeam;
import openwrestling.model.segmentEnum.AngleLength;
import openwrestling.model.segmentEnum.MatchLength;
import openwrestling.model.segmentEnum.MatchRule;
import openwrestling.model.segmentEnum.PresenceType;
import openwrestling.model.segmentEnum.SegmentType;
import openwrestling.model.segmentEnum.StaffType;
import openwrestling.model.utility.ModelUtils;
import openwrestling.model.utility.StaffUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.Serializable;
import java.util.List;

import static openwrestling.model.constants.GameConstants.*;
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

    public Segment saveSegment(Segment segment) {
        setSegmentRatings(segment);

        //TODO
        //processInjuries(segmentView);


        //  matchManager.addSegment(segment);
//        for (SegmentTeam team : segmentView.getTeams()) {
//            for (Worker worker : team.getWorkers()) {
//                matchManager.addSegmentWorker(new SegmentWorker(segmentView.getSegment(), worker, segmentView.getTeams().indexOf(team)));
//            }
//        }

        //matchManager.addSegment(segmentView.getSegment());
        return segment;
    }

    private int getSegmentWorkRating(Segment segment) {
        int workRatingTotal = 0;
        boolean isMatch = segment.getSegmentType().equals(SegmentType.MATCH);

        for (SegmentTeam team : segment.getTeams()) {
            workRatingTotal += getWorkRating(team, segment.getMatchRule());
        }

        int segmentRating = workRatingTotal / segment.getTeams().size();

        if (isMatch) {
            segmentRating = getSegmentRatingWithMatchModifiers(segment, segmentRating);
        } else {
            segmentRating = getSegmentRatingWithAngleModifiers(segment, segmentRating);
        }

        segmentRating = getSegmentRatingWithTimeModifier(segment, segmentRating);

        return segmentRating;
    }

    private int getSegmentRatingWithTimeModifier(Segment segment, int baseRating) {
        int maxTime = segment.getSegmentType().equals(SegmentType.MATCH) ? MatchLength.MAXIMUM.value() : AngleLength.MAXIMUM.value();
        int actual = segment.getSegmentLength() / maxTime * 100;

        int timeDiff = (int) actual - baseRating;

        int penalty = timeDiff > 0 ? TIME_OVERRUN_PENALTY_WEIGHT : TIME_UNDERRUN_PENALTY_WEIGHT;

        return baseRating - Math.abs(timeDiff) / penalty;
    }

    private int getSegmentRatingWithAngleModifiers(Segment segment, int baseRating) {
        baseRating = modifyRating(baseRating,
                getStaffSkillAverage(StaffType.CREATIVE, segment.getPromotion()),
                CREATIVE_MODIFIER_WEIGHT);

        return baseRating;
    }

    private int getSegmentRatingWithMatchModifiers(Segment segment, int baseRating) {
        baseRating = modifyRating(baseRating,
                getStaffSkillAverage(StaffType.REFEREE, segment.getPromotion()),
                REF_MODIFIER_WEIGHT);
        baseRating = modifyRating(baseRating,
                getStaffSkillAverage(StaffType.ROAD_AGENT, segment.getPromotion()),
                ROAD_AGENT_MODIFIER_WEIGHT);

        return baseRating;
    }

    private int getMatchCrowdRating(Segment segment) {
        int teamCount = 0;
        int totalPop = 0;

        for (SegmentTeam team : segment.getTeams()) {
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

        if (!segment.getTitles().isEmpty()) {
            int titleTotal = 0;
            for (Title title : segment.getTitles()) {
                titleTotal += title.getPrestige();
            }

            int titleAvg = titleTotal / segment.getTitles().size();
            crowdRating = modifyRating(crowdRating, titleAvg, TITLE_MODIFIER_WEIGHT);
        }

        crowdRating = modifyRating(crowdRating,
                getStaffSkillAverage(StaffType.CREATIVE, segment.getPromotion()),
                CREATIVE_MODIFIER_WEIGHT);
        crowdRating = modifyRating(crowdRating,
                getStaffSkillAverage(StaffType.PRODUCTION, segment.getPromotion()),
                PRODUCTION_MODIFIER_WEIGHT);

        return crowdRating;
    }

    private int modifyRating(int base, int modifier, int ratio) {
        int diff = modifier - base;
        base += (diff / ratio);
        return base;
    }

    private void setSegmentRatings(Segment segment) {
        int workRating = getSegmentWorkRating(segment);
        segment.setWorkRating(workRating);

        int crowdRating = modifyRating(getMatchCrowdRating(segment), workRating, CROWD_RATING_MODIFIER_WEIGHT);
//TODO
//        if (!segmentView.getBroadcastTeam().isEmpty()) {
//            int broadCastTeamTotal = 0;
//            for (SegmentItem item : segmentView.getBroadcastTeam()) {
//                if (item instanceof StaffMember) {
//                    broadCastTeamTotal += ((StaffMember) item).getSkill();
//                } else if (item instanceof Worker) {
//                    broadCastTeamTotal += ModelUtils.getWeightedScore(new Integer[]{
//                        ((Worker) item).getCharisma(),
//                        ((Worker) item).getPopularity()
//                    });
//                }
//            }
//
//            crowdRating = modifyRating(crowdRating,
//                    broadCastTeamTotal / segmentView.getBroadcastTeam().size(),
//                    BROADCAST_TEAM_MODIFIER_WEIGHT);
//        }

        segment.setCrowdRating(crowdRating);
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

    private void processInjuries(Segment segment) {
        List<Worker> matchWorkers = segment.getMatchParticipants();
        matchWorkers.stream().forEach((w) -> {
            Promotion promotion = segment.getPromotion();
            int medicModifier = StaffUtils.getStaffSkillModifier(StaffType.MEDICAL, promotion, workerManager.selectRoster(promotion));
            int injuryRate = BASE_INJURY_RATE - segment.getMatchRule().getInjuryModifier() * BASE_INJURY_RATE / 100;
            if (RandomUtils.nextInt(0, injuryRate) == 1 && RandomUtils.nextInt(0, injuryRate) > medicModifier) {
                int injuryDays = RandomUtils.nextInt(0, MAX_INJURY_DAYS);
                int duration = injuryDays - (medicModifier / 10);
                if (duration > 0) {
                    injuryManager.createInjury(dateManager.today(), duration, w, segment);
                }
            }
        });
    }

}
