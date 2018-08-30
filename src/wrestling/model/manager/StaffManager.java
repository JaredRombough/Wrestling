package wrestling.model.manager;

import java.util.ArrayList;
import java.util.List;
import wrestling.model.Contract;
import wrestling.model.Promotion;
import wrestling.model.modelView.StaffView;
import wrestling.model.modelView.WorkerView;

public class StaffManager {

    private final List<StaffView> staffViews = new ArrayList<>();

    public void addStaff(List<StaffView> staff) {
        getStaffViews().addAll(staff);
    }

    /**
     * @return the staffViews
     */
    public List<StaffView> getStaffViews() {
        return staffViews;
    }
    
    

}
