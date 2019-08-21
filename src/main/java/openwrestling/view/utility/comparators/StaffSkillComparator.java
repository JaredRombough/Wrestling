package openwrestling.view.utility.comparators;

import java.util.Comparator;
import openwrestling.model.modelView.StaffView;

public class StaffSkillComparator implements Comparator<StaffView> {

    @Override
    public int compare(StaffView staffView1, StaffView staffView2) {
        if (staffView1 != null && staffView2 != null) {

            return -Integer.valueOf(staffView1.getSkill()).compareTo(staffView2.getSkill());
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Skill";
    }

}
