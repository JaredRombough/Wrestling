package openwrestling.model.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import openwrestling.model.modelView.StaffView;

public class StaffManager implements Serializable {

    private final List<StaffView> staffViews = new ArrayList<>();

    public void addStaff(List<StaffView> staff) {
        getStaffViews().addAll(staff);
    }

    public void addStaff(StaffView staff) {
        staffViews.add(staff);
    }

    /**
     * @return the staffViews
     */
    public List<StaffView> getStaffViews() {
        return staffViews;
    }

    public List<StaffView> getAvailableStaff() {
        List<StaffView> availableStaff = new ArrayList<>();
        for (StaffView staff : staffViews) {
            if (staff.getStaffContract() == null) {
                availableStaff.add(staff);
            }
        }
        return availableStaff;
    }

}
