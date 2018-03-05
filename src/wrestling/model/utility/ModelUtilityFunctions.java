package wrestling.model.utility;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import wrestling.model.Promotion;
import wrestling.model.Worker;

public final class ModelUtilityFunctions {

    //returns a random int between the two passed ints
    public static int randRange(int low, int high) {
        Random r = new Random();
        return r.nextInt(high - low) + low;
    }

    public static String slashNames(List<Worker> workers) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < workers.size(); i++) {
            sb.append(workers.get(i).getName());
            if (workers.size() - i > 1) {
                sb.append("/");
            }
        }

        return sb.toString();
    }
    
    public static String slashShortNames(List<Worker> workers) {
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

}
