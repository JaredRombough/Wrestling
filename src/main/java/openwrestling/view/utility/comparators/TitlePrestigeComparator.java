package openwrestling.view.utility.comparators;

import java.util.Comparator;
import openwrestling.model.modelView.TitleView;

public class TitlePrestigeComparator implements Comparator<TitleView> {

    @Override
    public int compare(TitleView titleView1, TitleView titleView2) {
        if (titleView1 != null && titleView2 != null) {

            return -Integer.valueOf(titleView1.getPrestige()).compareTo(titleView2.getPrestige());
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Prestige";
    }

}
