package wrestling.model.modelView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import wrestling.model.EventTemplate;
import wrestling.model.SegmentItem;
import wrestling.model.segmentEnum.StaffType;
import wrestling.model.utility.StaffUtils;

public class PromotionView implements SegmentItem, Serializable {

    private static int serialNumber = 0;

    private String name;
    private String shortName;
    private String imagePath;
    private int promotionID;
    private int popularity;
    private int level;

    private final List<WorkerView> fullRoster;
    private final List<StaffView> allStaff;
    private List<StaffView> defaultBroadcastTeam;
    private List<EventTemplate> eventTemplates;

    public PromotionView() {

        fullRoster = new ArrayList<>();
        allStaff = new ArrayList<>();
        defaultBroadcastTeam = new ArrayList<>();
        eventTemplates = new ArrayList<>();

        name = "Promotion #" + serialNumber;
        shortName = "PRO" + serialNumber;

        //default popularity of 50 for now
        popularity = 50;

        serialNumber++;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPromotionID(int promotionID) {
        this.promotionID = promotionID;
    }

    public int indexNumber() {
        return promotionID;
    }

    public int getPopularity() {
        return popularity;
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

    /**
     * @return the shortName
     */
    @Override
    public String getShortName() {
        return shortName;
    }

    /**
     * @param shortName the shortName to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * @return the imagePath
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * @param imagePath the imagePath to set
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * @return the fullRoster
     */
    public List<WorkerView> getFullRoster() {
        return fullRoster;
    }

    public void addToRoster(WorkerView worker) {
        fullRoster.add(worker);
    }

    public void removeFromRoster(WorkerView worker) {
        if (fullRoster.contains(worker)) {
            fullRoster.remove(worker);
        }
    }

    /**
     * @return the allStaff
     */
    public List<StaffView> getAllStaff() {
        return allStaff;
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

    /**
     * @return the defaultBroadcastTeam
     */
    public List<StaffView> getDefaultBroadcastTeam() {
        return defaultBroadcastTeam;
    }

    /**
     * @param defaultBroadcastTeam the defaultBroadcastTeam to set
     */
    public void setDefaultBroadcastTeam(List<StaffView> defaultBroadcastTeam) {
        this.defaultBroadcastTeam = defaultBroadcastTeam;
    }

    /**
     * @return the eventTemplates
     */
    public List<EventTemplate> getEventTemplates() {
        return eventTemplates;
    }

    public void addEventTemplate(EventTemplate template) {
        eventTemplates.add(template);
    }
}
