package openwrestling.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import openwrestling.database.Database;
import openwrestling.model.gameObjects.StaffMember;

public class StaffManager implements Serializable {

    private final List<StaffMember> staffMembers = new ArrayList<>();

    public void addStaff(List<StaffMember> staff) {
        getStaffMembers().addAll(staff);
    }

    public List<StaffMember> creatStaffMembers(List<StaffMember> staffMembers) {
        List saved = Database.insertOrUpdateList(staffMembers);
        this.staffMembers.addAll(saved);
        return saved;
    }


    public void addStaff(StaffMember staff) {
        staffMembers.add(staff);
    }

    /**
     * @return the staffViews
     */
    public List<StaffMember> getStaffMembers() {
        return staffMembers;
    }

    public List<StaffMember> getAvailableStaff() {
        List<StaffMember> availableStaff = new ArrayList<>();
        for (StaffMember staff : staffMembers) {
            if (staff.getStaffContract() == null) {
                availableStaff.add(staff);
            }
        }
        return availableStaff;
    }

}
