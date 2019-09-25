package openwrestling.view.utility.comparators;

import java.util.Comparator;
import openwrestling.model.gameObjects.Title;

public class TitlePrestigeComparator implements Comparator<Title> {

    @Override
    public int compare(Title title1, Title title2) {
        if (title1 != null && title2 != null) {

            return -Integer.valueOf(title1.getPrestige()).compareTo(title2.getPrestige());
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Prestige";
    }

}
