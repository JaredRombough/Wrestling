package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.SegmentItem;
import openwrestling.model.segmentEnum.StaffType;
import openwrestling.model.utility.StaffUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Promotion extends GameObject implements SegmentItem, Serializable {

    private static int serialNumber = 0;

    private String name;
    private String shortName;
    private String imagePath;
    private int promotionID;
    private int importKey;
    @Builder.Default
    private int popularity = 50;
    private int level;
    @Builder.Default
    private List<StaffMember> allStaff = new ArrayList<>();
    @Builder.Default
    private List<StaffMember> defaultBroadcastTeam = new ArrayList<>();


    public void setPopularity(int popularity) {
        if (popularity > 100) {
            popularity = 100;
        } else if (popularity < 1) {
            popularity = 1;
        }
        this.popularity = popularity;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (level > 5) {
            level = 5;
        }
        if (level < 1) {
            level = 1;
        }
        this.level = level;
    }

    @Override
    public String toString() {
        return name;
    }

    public StaffMember getOwner() {
        List<StaffMember> owner = StaffUtils.getStaff(StaffType.OWNER, this);
        return owner.isEmpty() ? null : owner.get(0);
    }

    public void addToStaff(StaffMember staff) {
        allStaff.add(staff);
    }

    public void removeFromStaff(StaffMember staff) {
        allStaff.remove(staff);
        defaultBroadcastTeam.remove(staff);
        //TODO
//        eventTemplates.forEach(template -> {
//            template.getDefaultBroadcastTeam().remove(staff);
//        });
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Promotion &&
                Objects.equals(((Promotion) object).getPromotionID(), promotionID);
    }
}
