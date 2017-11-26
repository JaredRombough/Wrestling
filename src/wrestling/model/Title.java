package wrestling.model;

import java.io.Serializable;

public class Title implements Serializable {

    private Promotion promotion;

    private int teamSize;
    private String name;

    //vacant title
    public Title(Promotion promotion, int teamSize, String name) {
        this.promotion = promotion;
        this.teamSize = teamSize;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    /**
     * @return the promotion
     */
    public Promotion getPromotion() {
        return promotion;
    }

    /**
     * @param promotion the promotion to set
     */
    public void setPromotion(Promotion promotion) {
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

}
