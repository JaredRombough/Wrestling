package wrestling.model.utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.RandomUtils;
import wrestling.model.modelView.PromotionView;
import wrestling.model.SegmentItem;
import wrestling.model.constants.GameConstants;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.modelView.TitleView;
import wrestling.model.modelView.WorkerView;
import wrestling.model.segmentEnum.StaffType;
import wrestling.view.event.controller.TeamPaneWrapper;
import wrestling.view.utility.GameScreen;

public final class ModelUtils {

    private static final int medicManages = 30;

    public static String dateString(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy (cccc)"));
    }

    public static String timeString(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%d:%02d", hours, minutes);
    }

    public static String slashNames(List<? extends SegmentItem> workers) {
        if (workers.isEmpty()) {
            return "?";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < workers.size(); i++) {
            sb.append(workers.get(i).toString());
            if (workers.size() - i > 1) {
                sb.append("/");
            }
        }

        return sb.toString();
    }

    public static String andItemsLongName(List<? extends SegmentItem> items) {
        List<String> slashed = new ArrayList<>();
        for (SegmentItem item : items) {
            slashed.add(item.getLongName());
        }
        return slashed.isEmpty() ? "?" : joinGrammatically(slashed);
    }

    public static String andTeams(List<SegmentTeam> teams) {
        List<String> slashed = new ArrayList<>();
        for (SegmentTeam team : teams) {
            slashed.add(slashNames(team.getWorkers()));
        }
        return slashed.isEmpty() ? "?" : joinGrammatically(slashed);
    }

    private static String joinGrammatically(final List<String> list) {
        return list.size() > 1
                ? String.join(", ", list.subList(0, list.size() - 1))
                .concat(String.format("%s and ", list.size() > 2 ? "," : ""))
                .concat(list.get(list.size() - 1))
                : list.get(0);
    }

    public static String slashShortNames(List<? extends SegmentItem> workers) {
        if (workers.isEmpty()) {
            return "?";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < workers.size(); i++) {
            sb.append(workers.get(i).getShortName());
            if (workers.size() - i > 1) {
                sb.append("/");
            }
        }

        return sb.toString();
    }

    public static int weekOfMonth(LocalDate date) {
        Calendar ca1 = Calendar.getInstance();
        ca1.set(date.getYear(), date.getMonth().getValue(), date.getDayOfMonth());
        return ca1.get(Calendar.WEEK_OF_MONTH);
    }

    //the maximum popularity worker the promotion can hire
    public static int maxPopularity(PromotionView promotion) {
        return promotion.getLevel() * 20;
    }

    public static int getMatchWorkRating(WorkerView worker) {
        return getWeightedScore(new Integer[]{
            worker.getFlying(),
            worker.getWrestling(),
            worker.getStriking(),
            worker.getCharisma()
        });
    }

    public static int getWeightedScore(Integer[] attributes) {
        Arrays.sort(attributes, Collections.reverseOrder());

        return getPrioritizedScore(attributes);
    }

    public static int getPrioritizedScore(Integer[] attributes) {
        int totalScore = 0;

        for (int i = 0; i < attributes.length; i++) {
            totalScore += (attributes[i] * (attributes.length - i));
        }

        int denominator = (attributes.length * (attributes.length + 1)) / 2;

        return totalScore / denominator;
    }

    public static List<WorkerView> getWorkersFromSegmentItems(List<SegmentItem> segmentItems) {
        List<WorkerView> workers = new ArrayList<>();

        segmentItems.forEach((item) -> {
            if (item instanceof WorkerView) {
                workers.add((WorkerView) item);
            }
        });
        return workers;
    }

    public static List<TitleView> getTitleViewsFromSegmentItems(List<SegmentItem> segmentItems) {
        List<TitleView> titleViews = new ArrayList<>();

        segmentItems.forEach((item) -> {
            if (item instanceof TitleView) {
                titleViews.add((TitleView) item);
            }
        });
        return titleViews;
    }

    public static boolean teamIsPresent(List<? extends SegmentItem> workers, List<GameScreen> workerTeamWrappers) {
        for (GameScreen workerTeamWrapper : workerTeamWrappers) {
            TeamPaneWrapper controller = (TeamPaneWrapper) workerTeamWrapper.controller;
            if (controller.getTeamPaneController().getSegmentItems().equals(workers)) {
                return true;
            }
        }
        return false;
    }

    public static int getInjuryRange(PromotionView promotion) {
        int range = 100;
        int remainder = getMedicsRemainder(promotion);

        if (remainder < medicManages) {
            return range - (medicManages - remainder);
        }
        return range + (3 * remainder - medicManages);
    }

    public static double getInjuryRate(PromotionView promotion) {
        int staffDifferential = getMedicDifferential(promotion);

        int range = 100;
        if (staffDifferential < 0) {
            range = range - 20 * Math.abs(staffDifferential);
            range = range - (int) Math.pow(2, 2 + staffDifferential);
        } else if (staffDifferential > 0) {
            range = range + staffDifferential * 2;
        }

        int skillDifferential = getSkillDifferential(promotion, StaffType.MEDICAL);

        if (skillDifferential < 0) {
            range = range - (int) Math.pow(2, 2 + Math.abs(skillDifferential));
        } else if (skillDifferential > 0) {
            range = range + skillDifferential;
        }

        return (double) 1 / range;
    }

    public static int getInjuryDuration(PromotionView promotion) {
        int min = 7;
        int max = 160;
        return RandomUtils.nextInt(min, max) + getInjuryDurationModifier(promotion);
    }

    public static int getInjuryDurationModifier(PromotionView promotion) {
        int staffDifferential = getMedicDifferential(promotion);
        int skillDifferential = getSkillDifferential(promotion, StaffType.MEDICAL);
        int modifyDuration = 0;

        if (staffDifferential < 0) {
            modifyDuration = modifyDuration + 5 * Math.abs(staffDifferential);
            modifyDuration = modifyDuration + (int) Math.pow(2, 2 + staffDifferential);
        } else if (staffDifferential > 0) {
            modifyDuration = modifyDuration - staffDifferential * 2;
        }

        if (skillDifferential < 0) {
            modifyDuration = modifyDuration + (int) Math.pow(2, 2 + Math.abs(skillDifferential));
        } else if (skillDifferential > 0) {
            modifyDuration = modifyDuration - skillDifferential;
        }

        return modifyDuration;
    }

    public static int getMedicsRemainder(PromotionView promotion) {
        int medicsCount = promotion.getStaff(StaffType.MEDICAL).size();
        int rosterSize = promotion.getFullRoster().size();
        return (medicsCount * GameConstants.WORKERS_PER_MEDIC) % rosterSize;
    }

    public static int getMedicsRequired(PromotionView promotion) {
        double roster = promotion.getFullRoster().size();
        double medicsNeeded = roster / GameConstants.WORKERS_PER_MEDIC;
        return (int) Math.ceil(medicsNeeded);
    }

    public static int getMedicDifferential(PromotionView promotion) {
        return promotion.getStaff(StaffType.MEDICAL).size() - getMedicsRequired(promotion);
    }

    public static int getSkillDifferential(PromotionView promotion, StaffType staffType) {
        int skillRequired = promotion.getLevel() * 20 - 20;
        int avgSkill = promotion.getStaffSkillAverage(staffType);
        return avgSkill - skillRequired;
    }

}
