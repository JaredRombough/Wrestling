package openwrestling.view.utility.comparators;

import java.util.Comparator;
import openwrestling.model.gameObjects.StaffMember;

public class StaffSkillComparator implements Comparator<StaffMember> {

    @Override
    public int compare(StaffMember staffMember1, StaffMember staffMember2) {
        if (staffMember1 != null && staffMember2 != null) {

            return -Integer.valueOf(staffMember1.getSkill()).compareTo(staffMember2.getSkill());
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Skill";
    }

}
