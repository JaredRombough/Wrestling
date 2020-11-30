package openwrestling.model.gameObjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openwrestling.model.SegmentItem;

import java.io.Serializable;
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
    private String imageFileName;
    private long promotionID;
    private int importKey;
    @Builder.Default
    private int popularity = 50;
    private int level;


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

    @Override
    public boolean equals(Object object) {
        return object instanceof Promotion &&
                Objects.equals(((Promotion) object).getPromotionID(), promotionID);
    }
}
