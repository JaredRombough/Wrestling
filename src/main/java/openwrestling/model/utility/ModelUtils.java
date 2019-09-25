package openwrestling.model.utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import openwrestling.model.SegmentItem;
import openwrestling.model.SegmentTemplate;
import openwrestling.model.gameObjects.Promotion;
import openwrestling.model.modelView.SegmentTeam;
import openwrestling.model.modelView.SegmentView;
import openwrestling.model.gameObjects.Title;
import openwrestling.model.gameObjects.Worker;
import openwrestling.model.segmentEnum.MatchRule;
import openwrestling.model.segmentEnum.SegmentType;
import openwrestling.model.segmentEnum.TeamType;
import openwrestling.view.event.controller.TeamPaneWrapper;
import openwrestling.view.utility.GameScreen;

public final class ModelUtils {

    public static String dateString(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy (cccc)"));
    }

    public static String timeString(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%d:%02d", hours, minutes);
    }

    public static String slashNames(List<? extends SegmentItem> segmentItems) {
        return slashNames(segmentItems, "?");
    }

    public static String slashNames(List<? extends SegmentItem> segmentItems, String placeholder) {
        if (segmentItems.isEmpty()) {
            return placeholder;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segmentItems.size(); i++) {
            sb.append(segmentItems.get(i).toString());
            if (segmentItems.size() - i > 1) {
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

    public static String joinGrammatically(final List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
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
    public static int maxPopularity(Promotion promotion) {
        return promotion.getLevel() * 20;
    }

    public static int getMatchWorkRating(Worker worker) {
        return getWeightedScore(new Integer[]{
            worker.getFlying(),
            worker.getWrestling(),
            worker.getStriking(),
            worker.getCharisma()
        });
    }

    public static int getMatchWorkRating(Worker worker, MatchRule matchRule) {
        int flying = worker.getFlying() + (matchRule.getFlyingModifier() * worker.getFlying() / 100);
        int wrestling = worker.getWrestling() + (matchRule.getWrestingModifier() * worker.getWrestling() / 100);
        int striking = worker.getStriking() + (matchRule.getStrikingModifier() * worker.getStriking() / 100);
        return getWeightedScore(new Integer[]{
            flying,
            wrestling,
            striking,
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

    public static List<Worker> getWorkersFromSegmentItems(List<SegmentItem> segmentItems) {
        List<Worker> workers = new ArrayList<>();

        segmentItems.forEach((item) -> {
            if (item instanceof Worker) {
                workers.add((Worker) item);
            }
        });
        return workers;
    }

    public static List<Title> getTitleViewsFromSegmentItems(List<SegmentItem> segmentItems) {
        List<Title> titles = new ArrayList<>();

        segmentItems.forEach((item) -> {
            if (item instanceof Title) {
                titles.add((Title) item);
            }
        });
        return titles;
    }

    public static boolean teamIsPresent(List<? extends SegmentItem> workers, List<GameScreen> workerTeamWrappers) {
        for (GameScreen workerTeamWrapper : workerTeamWrappers) {
            TeamPaneWrapper controller = (TeamPaneWrapper) workerTeamWrapper.controller;
            if (controller.getSegmentItems().equals(workers)) {
                return true;
            }
        }
        return false;
    }

    public static SegmentView getSegmentFromTemplate(SegmentTemplate template) {
        return getSegmentFromTeams(template.getSegmentTeams());
    }

    public static SegmentView getSegmentFromTeams(List<SegmentTeam> segmentTeams) {
        SegmentView challengeMatch = new SegmentView(SegmentType.MATCH);
        segmentTeams.forEach(team -> {
            if (TeamType.CHALLENGER.equals(team.getType()) || TeamType.CHALLENGED.equals(team.getType())) {
                SegmentTeam segmentTeam = new SegmentTeam();
                segmentTeam.setType(TeamType.CHALLENGER.equals(team.getType()) ? TeamType.WINNER : TeamType.LOSER);
                segmentTeam.setWorkers(team.getWorkers());
                challengeMatch.getTeams().add(segmentTeam);
            }
        });
        return challengeMatch;
    }

}
