package openwrestling.model.gameObjects;

import lombok.Getter;
import lombok.Setter;
import openwrestling.model.EventTemplate;
import openwrestling.model.SegmentItem;
import openwrestling.model.modelView.StaffView;
import openwrestling.model.segmentEnum.StaffType;
import openwrestling.model.utility.StaffUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Promotion extends GameObject implements SegmentItem, Serializable {

    private static int serialNumber = 0;

    private String name;
    private String shortName;
    private String imagePath;
    private int promotionID;
    private int popularity;
    private int level;

    private final List<Worker> fullRoster;
    private final List<StaffView> allStaff;
    private List<StaffView> defaultBroadcastTeam;
    private List<EventTemplate> eventTemplates;

    public Promotion() {

        fullRoster = new ArrayList<>();
        allStaff = new ArrayList<>();
        defaultBroadcastTeam = new ArrayList<>();
        eventTemplates = new ArrayList<>();

        //default popularity of 50 for now
        popularity = 50;

        serialNumber++;
    }

    public int indexNumber() {
        return promotionID;
    }


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


    public void addToRoster(Worker worker) {
        fullRoster.add(worker);
    }

    public void removeFromRoster(Worker worker) {
        if (fullRoster.contains(worker)) {
            fullRoster.remove(worker);
        }
    }

    public StaffView getOwner() {
        List<StaffView> owner = StaffUtils.getStaff(StaffType.OWNER, this);
        return owner.isEmpty() ? null : owner.get(0);
    }

    public void addToStaff(StaffView staff) {
        allStaff.add(staff);
    }

    public void removeFromStaff(StaffView staff) {
        allStaff.remove(staff);
        defaultBroadcastTeam.remove(staff);
        eventTemplates.forEach(template -> {
            template.getDefaultBroadcastTeam().remove(staff);
        });
    }


    public void addEventTemplate(EventTemplate template) {
        eventTemplates.add(template);
    }
}
