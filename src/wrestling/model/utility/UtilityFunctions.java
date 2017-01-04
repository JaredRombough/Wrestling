package wrestling.model.utility;

import java.util.Random;

public final class UtilityFunctions {

    //returns a random int between the two passed ints
    public static int randRange(int low, int high) {
        Random r = new Random();
        return r.nextInt(high - low) + low;
    }
}
