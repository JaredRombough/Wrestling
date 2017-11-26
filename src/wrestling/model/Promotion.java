package wrestling.model;

import java.io.Serializable;

public class Promotion implements Serializable {

    private static int serialNumber = 0;

    private String name;
    private String shortName;
    private String imagePath;
    private int promotionID;
    private int popularity;
    private int level;

    public Promotion() {

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

    public int getPopulatirty() {
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
}
