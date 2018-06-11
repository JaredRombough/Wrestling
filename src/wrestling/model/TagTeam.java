package wrestling.model;

import wrestling.model.segmentEnum.ActiveType;

public class TagTeam {

    private String name;
    private int experience;
    private ActiveType activeType;

    public TagTeam() {
        experience = 0;
        activeType = ActiveType.ACTIVE;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the experience
     */
    public int getExperience() {
        return experience;
    }

    /**
     * @param experience the experience to set
     */
    public void setExperience(int experience) {
        this.experience = experience;
    }

    /**
     * @return the activeType
     */
    public ActiveType isActive() {
        return getActiveType();
    }

    /**
     * @param activeType the active to set
     */
    public void setActiveType(ActiveType activeType) {
        this.activeType = activeType;
    }

    /**
     * @return the activeType
     */
    public ActiveType getActiveType() {
        return activeType;
    }

}
