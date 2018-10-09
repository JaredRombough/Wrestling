package wrestling.model;

import wrestling.model.modelView.PromotionView;
import java.io.Serializable;
import wrestling.model.segmentEnum.ActiveType;

public class Title implements Serializable {

    private PromotionView promotion;

    private final int teamSize;
    private String name;
    private ActiveType activeType;
    private int prestige;

    //vacant title
    public Title(PromotionView promotion, int teamSize, String name) {
        this.promotion = promotion;
        this.teamSize = teamSize;
        this.name = name;
        this.activeType = ActiveType.ACTIVE;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    /**
     * @return the promotion
     */
    public PromotionView getPromotion() {
        return promotion;
    }

    /**
     * @param promotion the promotion to set
     */
    public void setPromotion(PromotionView promotion) {
        this.promotion = promotion;
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
     * @return the teamSize
     */
    public int getTeamSize() {
        return teamSize;
    }

    /**
     * @return the activeType
     */
    public ActiveType getActiveType() {
        return activeType;
    }

    /**
     * @param activeType the activeType to set
     */
    public void setActiveType(ActiveType activeType) {
        this.activeType = activeType;
    }

    /**
     * @return the prestige
     */
    public int getPrestige() {
        return prestige;
    }

    /**
     * @param prestige the prestige to set
     */
    public void setPrestige(int prestige) {
        this.prestige = prestige;
    }

}
