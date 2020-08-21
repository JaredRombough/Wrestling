package openwrestling.model.gameObjects;

import lombok.Getter;
import lombok.Setter;
import openwrestling.model.SegmentItem;
import openwrestling.model.interfaces.iContract;
import openwrestling.model.interfaces.iPerson;
import openwrestling.model.segment.constants.Gender;
import openwrestling.model.segment.constants.StaffType;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class StaffMember extends GameObject implements Serializable, SegmentItem, iPerson {

    private long staffMemberID;
    private String name;
    private Gender gender;
    private int age;
    private int skill;
    private long importKey;
    private int behaviour;
    private StaffType staffType;
    private String imageString;
    private StaffContract staffContract;

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public iContract getContract() {
        return staffContract;
    }

    @Override
    public String getShortName() {
        String[] splitName = name.split(" ");
        return splitName[splitName.length - 1];
    }

    @Override
    public void setShortName(String name) {
        // no short name
    }

    @Override
    public iContract getContract(Promotion promotion) {
        return staffContract != null && staffContract.getPromotion().equals(promotion)
                ? staffContract : null;
    }

    @Override
    public List<? extends iContract> getContracts() {
        return staffContract != null ? Collections.singletonList(staffContract) : Collections.emptyList();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof StaffMember &&
                Objects.equals(((StaffMember) object).getStaffMemberID(), staffMemberID);
    }

}
