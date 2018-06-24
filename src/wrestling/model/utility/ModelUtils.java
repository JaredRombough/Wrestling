package wrestling.model.utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import wrestling.model.Promotion;
import wrestling.model.SegmentItem;
import wrestling.model.Worker;
import wrestling.model.modelView.SegmentTeam;
import wrestling.model.modelView.TitleView;

public final class ModelUtils {

    //returns a random int between the two passed ints
    public static int randRange(int low, int high) {
        Random r = new Random();
        return r.nextInt(high - low) + low;
    }

    public static String dateString(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy (cccc)"));
    }

    public static String timeString(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%d:%02d", hours, minutes);
    }

    public static String slashNames(List<Worker> workers) {
        if (workers.isEmpty()) {
            return "?";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < workers.size(); i++) {
            sb.append(workers.get(i).getName());
            if (workers.size() - i > 1) {
                sb.append("/");
            }
        }

        return sb.toString();
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

    public static List<TitleView> getTitleViewsFromSegmentItems(List<SegmentItem> segmentItems) {
        List<TitleView> titleViews = new ArrayList<>();

        segmentItems.forEach((item) -> {
            if (item instanceof TitleView) {
                titleViews.add((TitleView) item);
            }
        });
        return titleViews;
    }

}
